#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable helm jq vault sed grep docker grep cat aws curl eksctl kubectl

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

ACCOUNT_ID=$(aws sts get-caller-identity | jq '.Account' -r)
echo "ACCOUNT_ID=${ACCOUNT_ID}"

LBC_VERSION="v2.7.1"
echo "LBC_VERSION=$LBC_VERSION"

# echo "executing eksctl utils associate-iam-oidc-provider"
# eksctl utils associate-iam-oidc-provider \
#     --region ${AWS_REGION} \
#     --cluster ${CLUSTERNAME} \
#     --approve

echo "creating iam policy"
curl -o iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/"${LBC_VERSION}"/docs/install/iam_policy.json
aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json

echo "creating iam service account for cluster ${CLUSTERNAME}"
eksctl create iamserviceaccount \
  --cluster $CLUSTERNAME \
  --namespace kube-system \
  --name aws-load-balancer-controller \
  --attach-policy-arn arn:aws:iam::${ACCOUNT_ID}:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --region $AWS_REGION \
  --approve

echo "setting up kubectl"

aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTERNAME --kubeconfig ~/.kube/wrongsecrets

export KUBECONFIG=~/.kube/wrongsecrets

echo "applying aws-lbc with kubectl"

kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller/crds?ref=master"

kubectl get crd

echo "do helm eks application"
helm repo add eks https://aws.github.io/eks-charts
helm repo update eks

echo "upgrade alb controller with helm"
helm upgrade -i aws-load-balancer-controller \
  eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=${CLUSTERNAME} \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set image.tag="${LBC_VERSION}" \
  --set region=${AWS_REGION} \
  --set image.repository=602401143452.dkr.ecr.${AWS_REGION}.amazonaws.com/amazon/aws-load-balancer-controller
# You may need to modify the account ID above if you're operating in af-south-1, ap-east-1, ap-southeast-3, cn-north and cn-northwest, eu-south-1, me-south-1, or the govcloud.
# See the full list of accounts per regions here: https://docs.aws.amazon.com/eks/latest/userguide/add-ons-images.html

echo "wait with rollout for 10 s"
sleep 10

echo "rollout status deployment"
kubectl -n kube-system rollout status deployment aws-load-balancer-controller

echo "wait after rollout for 10 s"
sleep 10

EKS_CLUSTER_VERSION=$(aws eks describe-cluster --name $CLUSTERNAME --region $AWS_REGION --query cluster.version --output text)

echo "apply -f k8s/secret-challenge-vault-service.yml in 10 s"
sleep 10
kubectl apply -f k8s/secret-challenge-vault-service.yml
echo "apply -f k8s/secret-challenge-vault-ingress.yml in 1 s"
sleep 1
kubectl apply -f k8s/secret-challenge-vault-ingress.yml

echo "waiting 10 s for loadBalancer"
sleep 10
echo "http://$(kubectl get ingress wrongsecrets -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')"

echo "Do not forget to cleanup afterwards! Run k8s-aws-alb-script-cleanup.sh"
