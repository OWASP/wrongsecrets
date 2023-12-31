#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable helm vault jq sed grep cat az envsubst

echo "This is a script to bootstrap the configuration. You need to have installed: helm, kubectl, vault, grep, cat, sed, envsubst, and azure cli, and is only tested on mac, Debian and Ubuntu"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube. Vault is awesome!"

# Most of the variables below are used in envsubst later.
export AZURE_SUBSCRIPTION_ID="$(az account show --query id --output tsv)"
export AZURE_TENANT_ID="$(az account show --query tenantId --output tsv)"

export RESOURCE_GROUP="$(terraform output -raw resource_group)"
export CLUSTER_NAME="$(terraform output -raw cluster_name)"

# for this demo, we will be deploying a user-assigned identity to the AKS node resource group
export IDENTITY_RESOURCE_GROUP="$(az aks show -g ${RESOURCE_GROUP} -n ${CLUSTER_NAME} --query nodeResourceGroup -otsv)"
export IDENTITY_NAME="wrongsecrets-identity"

export AZ_AD_APP_CLIENT_ID="$(terraform output -raw app_client_id)"

export AZ_VAULT_URI="$(terraform output -raw vault_uri)"
export AZ_KEY_VAULT_TENANT_ID="$(terraform output -raw tenant_id)"
export AZ_KEY_VAULT_NAME="$(terraform output -raw vault_name)"

# Set the kubeconfig
az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME --overwrite-existing

echo "Setting up workspace PSA to restricted for default"
kubectl apply -f k8s/workspace-psa.yml

kubectl get configmaps | grep 'secrets-file' &>/dev/null
if [ $? == 0 ]; then
  echo "secrets config is already installed"
else
  kubectl apply -f ../k8s/secrets-config.yml
fi

kubectl get secrets | grep 'funnystuff' &>/dev/null
if [ $? == 0 ]; then
  echo "secrets secret is already installed"
else
  kubectl apply -f ../k8s/secrets-secret.yml
  kubectl apply -f ../k8s/challenge33.yml
fi

source ../scripts/install-consul.sh

source ../scripts/install-vault.sh

echo "Add secrets manager driver to repo"
helm repo add csi-secrets-store-provider-azure https://azure.github.io/secrets-store-csi-driver-provider-azure/charts

helm list --namespace kube-system | grep 'csi-secrets-store' &>/dev/null
if [ $? == 0 ]; then
  echo "CSI driver is already installed"
else
  echo "Installing CSI driver"
  helm install csi csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace kube-system
fi

echo "Add Azure workload identity to repo"
helm repo add azure-workload-identity https://azure.github.io/azure-workload-identity/charts

helm list --namespace kube-system | grep 'workload-identity-webhook' &>/dev/null
if [ $? == 0 ]; then
  echo "Azure workload identity chart already installed"
else
helm install workload-identity-webhook azure-workload-identity/workload-identity-webhook \
   --namespace azure-workload-identity-system \
   --create-namespace \
   --set azureTenantID="${AZURE_TENANT_ID}"
fi

echo "Generate secret manager challenge secret 2"
az keyvault secret set --name wrongsecret-2 --vault-name "${AZ_KEY_VAULT_NAME}" --value "$(openssl rand -base64 16)" >/dev/null

echo "Generate secret manager challenge secret 3"
az keyvault secret set --name wrongsecret-3 --vault-name "${AZ_KEY_VAULT_NAME}" --value "$(openssl rand -base64 16)" >/dev/null

echo "Fill-out the secret volume manifest template"
envsubst <./k8s/secret-volume.yml.tpl >./k8s/secret-volume.yml

echo "Apply secretsmanager storage volume"
kubectl apply -f./k8s/secret-volume.yml

envsubst <./k8s/serviceAccount.yml.tpl >./k8s/serviceAccount.yml
envsubst <./k8s/secret-challenge-vault-deployment.yml.tpl >./k8s/secret-challenge-vault-deployment.yml

kubectl apply -f./k8s/serviceAccount.yml

while [[ $(kubectl --namespace=default get pods -l "app.kubernetes.io/component=mic" -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True" ]]; do echo "waiting for component=mic" && sleep 2; done
while [[ $(kubectl --namespace=default get pods -l "app.kubernetes.io/component=nmi" -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for component=nmi" && sleep 2; done



source ../scripts/apply-and-portforward.sh

echo "Run terraform destroy to clean everything up."
