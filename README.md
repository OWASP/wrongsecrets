# Wrong Secrets
Examples with how to not use secrets, used for the ADDO talk "Our secrets management journey from Code to Vault"


## Basic docker exercises

For the basic docker exercises you currently require:

- Maven
- java 13
- Docker

You can install it by doing:

```bash
mvn clean install
docker build --build-arg "argBasedPassword=bla2" -t addo-example:10
docker run -p 8080:8080 addo-example:10
```

Now you can try to find the secrets by means of curl ;-).

`curl -d '{"solution":"DefaultLoginPasswordDoNotChange!"}' -X POST -H "Content-Type: application/json" localhost:8080/challenge/1`
Will solve challenge 1, can you solve challenge 2-4?

## Basic K8s exercise

