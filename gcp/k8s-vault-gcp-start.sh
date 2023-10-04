#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable helm jq vault sed grep cat gcloud envsubst

echo "This is a script to bootstrap the configuration. You need to have installed: helm, kubectl, jq, vault, grep, cat, sed, envsubst, and google cloud cli, and is only tested on mac, Debian and Ubuntu"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube. Vault is awesome!"

export GCP_PROJECT=$(gcloud config list --format 'value(core.project)' 2>/dev/null)
#export USE_GKE_GCLOUD_AUTH_PLUGIN=True

export REGION="$(terraform output -raw region)"
export CLUSTER_NAME="$(terraform output -raw kubernetes_cluster_name)"

gcloud container clusters get-credentials --project ${GCP_PROJECT} --zone ${REGION} ${CLUSTER_NAME}

echo "Setting up workspace PSA to restricted for default"
kubectl apply -f ../k8s/workspace-psa.yml

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

source ../scripts/apply-and-portforward.sh

echo "Run terraform destroy to clean everything up."
