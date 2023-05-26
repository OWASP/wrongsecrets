#!/bin/bash
# set -o errexit
# set -o pipefail
# set -o nounset

source ../scripts/check-available-commands.sh

checkCommandsAvailable gcloud kubectl

export GCP_PROJECT=$(gcloud config list --format 'value(core.project)' 2>/dev/null)

kubectl delete -f ./k8s/k8s-gke-service.yaml
kubectl delete -f ./k8s/k8s-gke-ingress.yaml

echo "Waiting 10 seconds..."
sleep 10

echo "Fecthing network endpoint groups. If this yields results, clean them up:"
gcloud compute network-endpoint-groups list
