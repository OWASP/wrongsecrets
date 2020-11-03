#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

echo "This is only a script for demoing purposes. You need to have installed: minikube, helm, kubectl, jq, vault, grep, cat, sed and is only tested on mac"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube . Vault is awesome!"
minikube start
kubectl get configmaps | grep 'secrets-file' &> /dev/null
if [ $? == 0 ]; then
   echo "secrets config is already installed"
else
  kubectl apply -f k8s/secrets-config.yml
fi

kubectl get secrets | grep 'funnystuff' &> /dev/null
if [ $? == 0 ]; then
   echo "secrets secret is already installed"
else
  kubectl apply -f k8s/secrets-secret.yml
fi

helm list | grep 'consul' &> /dev/null
if [ $? == 0 ]; then
   echo "Consul is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm install consul hashicorp/consul --values k8s/helm-consul-values.yml
fi

while [[ $(kubectl get pods -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True" ]]; do echo "waiting for Consul" && sleep 2; done

helm list | grep 'vault' &> /dev/null
if [ $? == 0 ]; then
   echo "Vault is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm install vault hashicorp/vault --values k8s/helm-vault-values.yml
fi



isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running)
while [[ $isvaultrunning != *"vault-0"* ]]; do echo "waiting for Vault" && sleep 2 && isvaultrunning=$(kubectl get pods --field-selector=status.phase=Running); done

echo "Setting up port forwarding"
kubectl port-forward vault-0 8200:8200 &
echo "Unsealing Vault"
kubectl exec vault-0 -- vault operator init -key-shares=1 -key-threshold=1 -format=json > cluster-keys.json
cat cluster-keys.json | jq -r ".unseal_keys_b64[]"
VAULT_UNSEAL_KEY=$(cat cluster-keys.json | jq -r ".unseal_keys_b64[]")
kubectl exec vault-0 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-1 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-2 -- vault operator unseal $VAULT_UNSEAL_KEY


echo "Obtaining root token"
jq .root_token cluster-keys.json > commentedroottoken

sed "s/^\([\"']\)\(.*\)\1\$/\2/g" commentedroottoken > root_token
ROOTTOKEN=$(cat root_token)

echo "Logging in"
kubectl exec vault-0 -- vault login $ROOTTOKEN 

echo "Enabling kv-v2 kubernetes"
kubectl exec vault-0 -- vault secrets enable -path=secret kv-v2

echo "Putting a secret in"
kubectl exec vault-0 -- vault kv put secret/webapp/config username="static-user" password="$(openssl rand -base64 16)"

echo "Enable k8s auth"
kubectl exec vault-0 -- vault auth enable kubernetes

echo "Writing k8s auth config" 
#TODO: below should be executed on he host only, so pick it up from the pod!
kubectl exec vault-0 -- vault write auth/kubernetes/config \
        token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
        kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
        kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

echo "Writing policy for webapp"
kubectl exec vault-0 -- vault policy write webapp - <<EOF
path "secret/data/webapp/config" {
  capabilities = ["read"]
}
EOF

echo "Write secrets for webapp"
kubectl exec vault-0 -- vault write auth/kubernetes/role/webapp \
        bound_service_account_names=vault \
        bound_service_account_namespaces=default \
        policies=webapp \
        ttl=24h \
 && vault kv put secret/webapp/config username="static-user" password="static-password"

kubectl apply -f k8s/secret-challenge-deployment.yml
kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
kubectl port-forward secret-challenge 8080:8080 
#or 
#minikube service secret-challenge