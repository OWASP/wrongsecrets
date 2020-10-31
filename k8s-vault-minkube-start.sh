#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset

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

helm list | grep 'vault' &> /dev/null
if [ $? == 0 ]; then
   echo "Vault is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm install vault hashicorp/vault --values k8s/helm-vault-values.yml
fi

kubectl port-forward vault-0 8200:8200 &
kubectl exec vault-0 -- vault operator init -key-shares=1 -key-threshold=1 -format=json > cluster-keys.json &
cat cluster-keys.json | jq -r ".unseal_keys_b64[]"
VAULT_UNSEAL_KEY=$(cat cluster-keys.json | jq -r ".unseal_keys_b64[]")
kubectl exec vault-0 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-1 -- vault operator unseal $VAULT_UNSEAL_KEY
kubectl exec vault-2 -- vault operator unseal $VAULT_UNSEAL_KEY

kubectl exec vault-0 -- vault auth enable kubernetes
kubectl exec vault-0 -- vault write auth/kubernetes/config \
        token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
        kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
        kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

kubectl exec vault-0 -- vault policy write webapp - <<EOF
path "secret/data/webapp/config" {
  capabilities = ["read"]
}
EOF

kubectl exec vault-0 -- vault write auth/kubernetes/role/webapp \
        bound_service_account_names=vault \
        bound_service_account_namespaces=default \
        policies=webapp \
        ttl=24h
cat cluster-keys.json | jq -r ".root_token"
kubectl exec vault-0 login && vault secrets enable -path=secret kv-v2 && vault kv put secret/webapp/config username="static-user" password="static-password"

#kubectl apply -f k8s/secret-challenge-deployment.yml
#kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
#minikube service secret-challenge