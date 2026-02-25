#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable kubectl

echo "set up ingress class"
kubectl apply -f ./k8s/ingress-class-params.yaml
kubectl apply -f ./k8s/ingress-class.yaml

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
