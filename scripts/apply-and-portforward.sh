#!/usr/bin/env bash
kubectl apply -f./k8s/secret-challenge-vault-deployment.yml
if ! kubectl rollout status deployment/secret-challenge --timeout=30m; then
  echo "secret-challenge deployment did not become ready within 30 minutes" >&2
  kubectl describe deployment secret-challenge
  kubectl describe pod -l app=secret-challenge
  kubectl logs -l app=secret-challenge --all-containers --tail=100
  return 1 2>/dev/null || exit 1
fi
#kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
# pick a Running, non-terminating pod instead of blindly taking the first one
SECRET_CHALLENGE_POD=$(kubectl get pods -l app=secret-challenge --field-selector=status.phase=Running \
  -o go-template='{{range .items}}{{if not .metadata.deletionTimestamp}}{{.metadata.name}}{{"\n"}}{{end}}{{end}}' | head -n 1)
if [ -z "${SECRET_CHALLENGE_POD}" ]; then
  echo "No running secret-challenge pod found" >&2
  kubectl get pods -l app=secret-challenge >&2
  return 1 2>/dev/null || exit 1
fi
echo "Port-forwarding to pod ${SECRET_CHALLENGE_POD}"
kubectl port-forward "pod/${SECRET_CHALLENGE_POD}" 8080:8080 8090:8090 &
echo "Run terraform destroy to clean everything up."
