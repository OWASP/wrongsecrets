#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable helm jq vault sed grep cat gcloud envsubst

echo "This is a script to bootstrap the configuration. You need to have installed: helm, kubectl, jq, vault, grep, cat, sed, envsubst, and google cloud cli, and is only tested on mac, Debian and Ubuntu"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube. Vault is awesome!"

export GCP_PROJECT=$(gcloud config list --format 'value(core.project)' 2>/dev/null)

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

while [[ $(kubectl get pods -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True True True" ]]; do echo "waiting for Consul" && sleep 2; done

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
kubectl port-forward vault-0 -n vault 8200:8200 &
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
helm repo add secrets-store-csi-driver https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts

helm list --namespace kube-system | grep 'csi-secrets-store' &>/dev/null
if [ $? == 0 ]; then
  echo "CSI driver is already installed"
else
  helm install -n kube-system csi-secrets-store secrets-store-csi-driver/secrets-store-csi-driver --set enableSecretRotation=true --set rotationPollInterval=60s
fi

kubectl apply -f https://raw.githubusercontent.com/GoogleCloudPlatform/secrets-store-csi-driver-provider-gcp/main/deploy/provider-gcp-plugin.yaml

echo "Generate secret manager challenge secret 2"
echo -n "$(openssl rand -base64 16)" |
  gcloud secrets versions add wrongsecret-2 --data-file=-

echo "Generate secret manager challenge secret 3"
echo -n "$(openssl rand -base64 16)" |
  gcloud secrets versions add wrongsecret-3 --data-file=-

echo "Fill-out the secret volume manifest template"
envsubst <./k8s/secret-volume.yml.tpl >./k8s/secret-volume.yml

echo "Apply secretsmanager storage volume"
kubectl apply -f./k8s/secret-volume.yml

echo "Annotate service accounts"
kubectl annotate serviceaccount \
  --namespace default vault \
  "iam.gke.io/gcp-service-account=wrongsecrets-workload-sa@${GCP_PROJECT}.iam.gserviceaccount.com"

kubectl annotate serviceaccount \
  --namespace default default \
  "iam.gke.io/gcp-service-account=wrongsecrets-workload-sa@${GCP_PROJECT}.iam.gserviceaccount.com"

envsubst <./k8s/secret-challenge-vault-deployment.yml.tpl >./k8s/secret-challenge-vault-deployment.yml

kubectl apply -f./k8s/secret-challenge-vault-deployment.yml
while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done

echo "Deploying service"
kubectl apply -f k8s/k8s-gke-service.yaml

echo "Deploying ingress"
kubectl apply -f k8s/k8s-gke-ingress.yaml

while [[ -z $(kubectl get ingress basic-ingress --output jsonpath='{.status.loadBalancer.ingress[].ip}') ]]; do echo "waiting for ingress IP, this will take a few minutes... last check: $(date +\%T)" && sleep 10; done

echo " "
echo "Your ingress url is: http://$(kubectl get ingress basic-ingress --output jsonpath='{.status.loadBalancer.ingress[].ip}')"
echo " "

echo "Run terraform destroy to clean everything up. You may need to go to the 'network endpoint groups' in google cloud and clean up some remaining resources."
