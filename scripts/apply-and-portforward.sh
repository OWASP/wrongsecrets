kubectl apply -f./k8s/secret-challenge-vault-deployment.yml
while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done
#kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
kubectl port-forward \
  $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") \
  8080:8080 \
  &
echo "Run terraform destroy to clean everything up."
