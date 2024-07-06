#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable helm jq vault sed grep cat aws

if test -n "${AWS_REGION-}"; then
  echo "AWS_REGION is set to <$AWS_REGION>"
else
  AWS_REGION=eu-west-1
  echo "AWS_REGION is not set or empty, defaulting to ${AWS_REGION}"
fi

if test -n "${CLUSTERNAME-}"; then
  echo "CLUSTERNAME is set to <$CLUSTERNAME>"
else
  CLUSTERNAME=wrongsecrets-exercise-cluster
  echo "CLUSTERNAME is not set or empty, defaulting to ${CLUSTERNAME}"
fi

aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTERNAME --kubeconfig ~/.kube/wrongsecrets

export KUBECONFIG=~/.kube/wrongsecrets

echo "This is a script to bootstrap the configuration. You need to have installed: helm, kubectl, jq, vault, grep, cat, sed, and awscli, and is only tested on mac, Debian and Ubuntu"
echo "This script is based on the steps defined in https://learn.hashicorp.com/tutorials/vault/kubernetes-minikube. Vault is awesome!"

echo "Setting kubeconfig to wrongsecrets-exercise-cluster"
aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTERNAME

echo "Setting up workspace PSA to restricted for default"
kubectl apply -f ../k8s/workspace-psa.yml

kubectl get configmaps | grep 'secrets-file' &>/dev/null
if [ $? == 0 ]; then
  echo "secrets config is already installed"
else
  kubectl apply -f ../k8s/secrets-config.yml
fi

echo "Setting up the bitnami sealed secret controler"
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.27.0/controller.yaml
kubectl apply -f ../k8s/sealed-secret-controller.yaml
kubectl apply -f ../k8s/main.key
kubectl delete pod -n kube-system -l name=sealed-secrets-controller
kubectl create -f ../k8s/sealed-challenge48.json
echo "finishing up the sealed secret controler part"
echo "do you need to decrypt and/or handle things for the sealed secret use kubeseal"

kubectl get secrets | grep 'funnystuff' &>/dev/null
if [ $? == 0 ]; then
  echo "secrets secret is already installed"
else
  kubectl apply -f ../k8s/secrets-secret.yml
  kubectl apply -f ../k8s/challenge33.yml
fi

helm list -n | grep 'aws-ebs-csi-driver' &> /dev/null
if [ $? == 0 ]; then
  echo "AWS EBS CSI driver is already installed"
else
  echo "Installing AWS EBS CSI driver"
  helm repo add aws-ebs-csi-driver https://kubernetes-sigs.github.io/aws-ebs-csi-driver
  helm repo update
  helm upgrade --install aws-ebs-csi-driver --version 2.32.0 \
    --namespace kube-system \
    aws-ebs-csi-driver/aws-ebs-csi-driver \
    --values ./k8s/ebs-csi-driver-values.yaml
fi

source ../scripts/install-vault.sh

echo "Setting up IRSA for the vault service account"
kubectl annotate --overwrite sa vault eks.amazonaws.com/role-arn="$(terraform output -raw irsa_role)"

echo "Add secrets manager driver to EKS"
helm repo add secrets-store-csi-driver https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts

helm list --namespace kube-system | grep 'csi-secrets-store' &>/dev/null
if [ $? == 0 ]; then
  echo "CSI driver is already installed"
else
  helm install -n kube-system csi-secrets-store secrets-store-csi-driver/secrets-store-csi-driver --set enableSecretRotation=true --set rotationPollInterval=60s
fi

echo "Install ACSP"
kubectl apply -f https://raw.githubusercontent.com/aws/secrets-store-csi-driver-provider-aws/main/deployment/aws-provider-installer.yaml

echo "Generate secrets manager challenge secret 2"
aws secretsmanager put-secret-value --secret-id wrongsecret-2 --secret-string "$(openssl rand -base64 24)" --region $AWS_REGION --output json --no-cli-pager

echo "Generate Parameter store challenge secret"
aws ssm put-parameter --name wrongsecretvalue --overwrite --type SecureString --value "$(openssl rand -base64 24)" --region $AWS_REGION --output json --no-cli-pager

echo "Apply secretsmanager storage volume"
kubectl apply -f./k8s/secret-volume.yml

source ../scripts/apply-and-portforward.sh

echo "Run terraform destroy to clean everything up."
