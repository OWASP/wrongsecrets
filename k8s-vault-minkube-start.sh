#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source scripts/check-available-commands.sh

checkCommandsAvailable helm minikube jq vault sed grep docker grep cat

echo "This is only a script for demoing purposes. You can comment out line 22 and work with your own k8s setup"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube . Vault is awesome!"
minikube start --kubernetes-version=v1.28.1

echo "Patching default ns with new PSA; we should run as restricted!"
kubectl apply -f k8s/workspace-psa.yml

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
  kubectl apply -f k8s/challenge33.yml
fi
helm list | grep 'consul' &> /dev/null
if [ $? == 0 ]; then
   echo "Consul is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
fi
helm upgrade --install consul hashicorp/consul --set global.name=consul --create-namespace -n consul --values k8s/helm-consul-values.yml

while [[ $(kubectl get pods -n consul -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True True True True" ]]; do echo "waiting for Consul" && sleep 2; done

helm list | grep 'vault' &> /dev/null
if [ $? == 0 ]; then
   echo "Vault is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
fi
kubectl create ns vault
helm upgrade --install vault hashicorp/vault --version 0.23.0 --namespace vault --values k8s/helm-vault-values.yml

isvaultrunning=$(kubectl get pods -n vault --field-selector=status.phase=Running)
while [[ $isvaultrunning != *"vault-0"* ]]; do echo "waiting for Vault1" && sleep 2 && isvaultrunning=$(kubectl get pods -n vault --field-selector=status.phase=Running); done
while [[ $isvaultrunning != *"vault-1"* ]]; do echo "waiting for Vault2" && sleep 2 && isvaultrunning=$(kubectl get pods -n vault --field-selector=status.phase=Running); done
while [[ $isvaultrunning != *"vault-2"* ]]; do echo "waiting for Vault3" && sleep 2 && isvaultrunning=$(kubectl get pods -n vault --field-selector=status.phase=Running); done
echo "Setting up port forwarding"
kubectl port-forward vault-0 8200:8200 -n vault &
echo "Unsealing Vault"
kubectl exec -n vault vault-0 -- vault operator init -key-shares=1 -key-threshold=1 -format=json > cluster-keys.json
cat cluster-keys.json | jq -r ".unseal_keys_b64[]"
VAULT_UNSEAL_KEY=$(cat cluster-keys.json | jq -r ".unseal_keys_b64[]")

echo "⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰⏰"
echo "PLEASE COPY PASTE THE FOLLOWING VALUE: ${VAULT_UNSEAL_KEY} , you will be asked for it 3 times to unseal the vaults"

kubectl exec -it vault-0 -n vault -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec -it vault-1 -n vault -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec -it vault-2 -n vault -- vault operator unseal $VAULT_UNSEAL_KEY


echo "Obtaining root token"
jq .root_token cluster-keys.json > commentedroottoken

sed "s/^\([\"']\)\(.*\)\1\$/\2/g" commentedroottoken > root_token
ROOTTOKEN=$(cat root_token)

echo "Logging in"
kubectl exec vault-0 -n vault -- vault login $ROOTTOKEN

echo "Enabling kv-v2 kubernetes"
kubectl exec vault-0 -n vault -- vault secrets enable -path=secret kv-v2

echo "Putting a secret in"
kubectl exec vault-0 -n vault -- vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)"

echo "Enable k8s auth"
kubectl exec vault-0 -n vault -- vault auth enable kubernetes

echo "Writing k8s auth config"

kubectl exec vault-0 -n vault -- /bin/sh -c 'vault write auth/kubernetes/config \
        token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
        kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
        kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt'

echo "Writing policy for secret-challenge"
kubectl exec vault-0 -n vault -- /bin/sh -c 'vault policy write secret-challenge - <<EOF
path "secret/data/secret-challenge" {
  capabilities = ["read"]
}
path "secret/data/application" {
  capabilities = ["read"]
}
EOF'

echo "Write secrets for secret-challenge"
kubectl exec vault-0 -n vault -- vault write auth/kubernetes/role/secret-challenge \
        bound_service_account_names=vault \
        bound_service_account_namespaces=default \
        policies=secret-challenge \
        ttl=24h \
 && vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)" \
 && vault kv put secret/application vaultpassword.password="$(openssl rand -base64 16)" \

kubectl create serviceaccount vault
echo "Deploy secret challenge app"
kubectl apply -f k8s/secret-challenge-vault-deployment.yml
while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done
kubectl logs -l app=secret-challenge -f >> pod.log &
kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
kubectl port-forward \
    $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") \
    8080:8080 \
    &
echo "Do minikube delete to stop minikube from running and cleanup to start fresh again"
echo "wait 20 seconds so we can check if vault-k8s-container works"
sleep 20
curl http://localhost:8080/spoil/challenge-7
echo "logs from pod to make sure:"
cat pod.log
