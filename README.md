# Wrong Secrets
Examples with how to not use secrets, used for the ADDO talk "Our secrets management journey from Code to Vault"


## Basic docker exercises

For the basic docker exercises you currently require:

- Docker

You can install it by doing:

```bash
docker run -p 8080:8080 jeroenwillemsen/addo-example:10
```

Now you can try to find the secrets by means of curl ;-).

`curl -d '{"solution":"DefaultLoginPasswordDoNotChange!"}' -X POST -H "Content-Type: application/json" localhost:8080/challenge/1`
Will solve challenge 1, can you solve challenge 2-4?

## Basic K8s exercise

The K8S setup currently is based on using Minikube for local fun:

```bash
    minikube start
    kubectl apply -f k8s/secrets-config.yml
    kubectl apply -f k8s/secret-challenge-deployment.yml
    kubectl expose deployment secret-challenge --type=LoadBalancer --port=8080
    minikube service secret-challenge
```
now you can use the provided ip-address and port to further play with the K8s variant.

