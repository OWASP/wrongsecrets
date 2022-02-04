#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

function checkCommandsAvailable() {
  for var in "$@"; do
    if ! [ -x "$(command -v "$var")" ]; then
      echo "🔥 ${var} is not installed." >&2
      exit 1
    else
      echo "🏄 $var is installed..."
    fi
  done
}

checkCommandsAvailable helm minikube jq vault sed grep docker grep cat az envsubst

echo "This is a script to bootstrap the configuration. You need to have installed: helm, kubectl, jq, vault, grep, cat, sed, envsubst, and azure cli, and is only tested on mac, Debian and Ubuntu"
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
az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME

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
fi

helm list | grep 'consul' &>/dev/null
if [ $? == 0 ]; then
  echo "Consul is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm install consul hashicorp/consul --version 0.30.0 --values ../k8s/helm-consul-values.yml
fi

while [[ $(kubectl get pods -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True" ]]; do echo "waiting for Consul" && sleep 2; done

helm list | grep 'vault' &>/dev/null
if [ $? == 0 ]; then
  echo "Vault is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm install vault hashicorp/vault --version 0.9.1 --values ../k8s/helm-vault-values.yml
fi

isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running)
while [[ $isvaultrunning != *"vault-0"* ]]; do echo "waiting for Vault0" && sleep 2 && isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running); done
while [[ $isvaultrunning != *"vault-1"* ]]; do echo "waiting for Vaul1" && sleep 2 && isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running); done
while [[ $isvaultrunning != *"vault-2"* ]]; do echo "waiting for Vaul2" && sleep 2 && isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running); done

echo "Setting up port forwarding"
kubectl port-forward vault-0 8200:8200 &
echo "Unsealing Vault"
kubectl exec vault-0 -- vault operator init -key-shares=1 -key-threshold=1 -format=json >cluster-keys.json
cat cluster-keys.json | jq -r ".unseal_keys_b64[]"
VAULT_UNSEAL_KEY=$(cat cluster-keys.json | jq -r ".unseal_keys_b64[]")
kubectl exec vault-0 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-1 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-2 -- vault operator unseal $VAULT_UNSEAL_KEY

echo "Obtaining root token"
jq .root_token cluster-keys.json >commentedroottoken

sed "s/^\([\"']\)\(.*\)\1\$/\2/g" commentedroottoken >root_token
ROOTTOKEN=$(cat root_token)

echo "Logging in"
kubectl exec vault-0 -- vault login $ROOTTOKEN

echo "Enabling kv-v2 kubernetes"
kubectl exec vault-0 -- vault secrets enable -path=secret kv-v2

echo "Putting a secret in"
kubectl exec vault-0 -- vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)"

echo "Enable k8s auth"
kubectl exec vault-0 -- vault auth enable kubernetes

echo "Writing k8s auth config"

kubectl exec vault-0 -- /bin/sh -c 'vault write auth/kubernetes/config \
        token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
        kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
        kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt'

echo "Writing policy for secret-challenge"
kubectl exec vault-0 -- /bin/sh -c 'vault policy write secret-challenge - <<EOF
path "secret/data/secret-challenge" {
  capabilities = ["read"]
}
path "secret/data/application" {
  capabilities = ["read"]
}
EOF'

echo "Write secrets for secret-challenge"
kubectl exec vault-0 -- vault write auth/kubernetes/role/secret-challenge \
  bound_service_account_names=vault \
  bound_service_account_namespaces=default \
  policies=secret-challenge \
  ttl=24h &&
  vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)" &&
  vault kv put secret/application vaultpassword.password="$(openssl rand -base64 16)"

echo "Add secrets manager driver to repo"
helm repo add csi-secrets-store-provider-azure https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/master/charts

helm list --namespace kube-system | grep 'csi-secrets-store' &>/dev/null
if [ $? == 0 ]; then
  echo "CSI driver is already installed"
else
  echo "Installing CSI driver"
  helm install -n kube-system csi csi-secrets-store-provider-azure/csi-secrets-store-provider-azure
fi

echo "Add Azure pod identity to repo"
helm repo add aad-pod-identity https://raw.githubusercontent.com/Azure/aad-pod-identity/master/charts

helm list --namespace kube-system | grep 'aad-pod-identity' &>/dev/null
if [ $? == 0 ]; then
  echo "Azure pod identity chart already installed"
else
  helm install aad-pod-identity aad-pod-identity/aad-pod-identity
fi

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

kubectl apply -f./k8s/secret-challenge-vault-deployment.yml
while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done
#kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
kubectl port-forward \
  $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") \
  8080:8080 \
  ;
echo "Run terraform destroy to clean everything up."
