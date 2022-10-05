helm list | grep 'consul' &>/dev/null
if [ $? == 0 ]; then
  echo "Consul is already installed"
else
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm repo update hashicorp
  helm upgrade --install consul hashicorp/consul --set global.name=consul --create-namespace -n consul --values ../k8s/helm-consul-values.yml
fi

while [[ $(kubectl get pods -n consul -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != *"True True"* && $(kubectl get pods -l app=consul -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != *"True True True True"* ]]; do echo "waiting for Consul" && sleep 2; done
