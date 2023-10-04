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

export AZ_POD_RESOURCE_ID="$(terraform output -raw aad_pod_identity_resource_id)"
export AZ_POD_CLIENT_ID="$(terraform output -raw aad_pod_identity_client_id)"

export AZ_EXTRA_POD_RESOURCE_ID="$(terraform output -raw aad_extra_pod_identity_resource_id)"
export AZ_EXTRA_POD_CLIENT_ID="$(terraform output -raw aad_extra_pod_identity_client_id)"

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

#TO BE REPLACED WITH https://azure.github.io/azure-workload-identity/docs/installation.html
echo "Add Azure pod identity to repo"
helm repo add aad-pod-identity https://raw.githubusercontent.com/Azure/aad-pod-identity/master/charts

helm list --namespace kube-system | grep 'aad-pod-identity' &>/dev/null
if [ $? == 0 ]; then
  echo "Azure pod identity chart already installed"
else
  helm upgrade --install aad-pod-identity aad-pod-identity/aad-pod-identity #NO LONGER WORKS BECAUSE OF OUR CONFIUGRATION (RESTRICTED IN DEFAULT)
fi

#END TO BE REPLACED WITH https://azure.github.io/azure-workload-identity/docs/installation.html

echo "Generate secret manager challenge secret 2"
az keyvault secret set --name wrongsecret-2 --vault-name "${AZ_KEY_VAULT_NAME}" --value "$(openssl rand -base64 16)" >/dev/null

echo "Generate secret manager challenge secret 3"
az keyvault secret set --name wrongsecret-3 --vault-name "${AZ_KEY_VAULT_NAME}" --value "$(openssl rand -base64 16)" >/dev/null

echo "Fill-out the secret volume manifest template"
envsubst <./k8s/secret-volume.yml.tpl >./k8s/secret-volume.yml

echo "Apply secretsmanager storage volume"
kubectl apply -f./k8s/secret-volume.yml

envsubst <./k8s/pod-id.yml.tpl >./k8s/pod-id.yml
envsubst <./k8s/secret-challenge-vault-deployment.yml.tpl >./k8s/secret-challenge-vault-deployment.yml

kubectl apply -f./k8s/pod-id.yml

while [[ $(kubectl --namespace=default get pods -l "app.kubernetes.io/component=mic" -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True" ]]; do echo "waiting for component=mic" && sleep 2; done
while [[ $(kubectl --namespace=default get pods -l "app.kubernetes.io/component=nmi" -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for component=nmi" && sleep 2; done



source ../scripts/apply-and-portforward.sh

echo "Run terraform destroy to clean everything up."
