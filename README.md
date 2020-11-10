# Wrong Secrets
Examples with how to not use secrets, used for the ADDO talk "Our secrets management journey from Code to Vault"


## Basic docker exercises

For the basic docker exercises you currently require:

- Docker

You can install it by doing:

```bash
docker run -p 8080:8080 jeroenwillemsen/addo-example:36
```

Now you can try to find the secrets by means of solving the challenge offered at:

- [localhost:8080/challenge/1](localhost:8080/challenge/1)
- [localhost:8080/challenge/2](localhost:8080/challenge/2)
- [localhost:8080/challenge/3](localhost:8080/challenge/3)
- [localhost:8080/challenge/4](localhost:8080/challenge/4)
- [localhost:8080/challenge/5](localhost:8080/challenge/5)
- [localhost:8080/challenge/6](localhost:8080/challenge/6)
- [localhost:8080/challenge/6](localhost:8080/challenge/7)
- [localhost:8080/challenge/6](localhost:8080/challenge/8)

## Basic K8s exercise

### Minikube based

The K8S setup currently is based on using Minikube for local fun:

```bash
    minikube start
    kubectl apply -f k8s/secrets-config.yml
    kubectl apply -f k8s/secrets-secret.yml
    kubectl apply -f k8s/secret-challenge-deployment.yml
    while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done
    kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
    minikube service secret-challenge
```

now you can use the provided ip-address and port to further play with the K8s variant (instead of localhost).

### k8s based

Want to run vanilla on your own k8s? Use the commands below:

```bash

    kubectl apply -f k8s/secrets-config.yml
    kubectl apply -f k8s/secrets-secret.yml
    kubectl apply -f k8s/secret-challenge-deployment.yml
    while [[ $(kubectl get pods -l app=secret-challenge -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for secret-challenge" && sleep 2; done
    kubectl port-forward \
        $(kubectl get pod -l app=secret-challenge -o jsonpath="{.items[0].metadata.name}") \
        8080:8080

```

## Special thanks

With thanks to [@bendehaan](https://github.com/bendehaan) & [@nbaars](https://github.com/nbaars) for accelerating the project.
 