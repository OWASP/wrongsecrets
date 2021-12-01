# OWASP WrongSecrets [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Want%20to%20dive%20into%20secrets%20management%20and%20do%20some%20hunting?%20try%20this&url=https://github.com/commjoen/wrongsecrets&hashtags=secretsmanagement,secrets,hunting,p0wnableapp)

[![Java checkstyle and testing](https://github.com/commjoen/wrongsecrets/actions/workflows/main.yml/badge.svg)](https://github.com/commjoen/wrongsecrets/actions/workflows/main.yml) [![Terraform FMT](https://github.com/commjoen/wrongsecrets/actions/workflows/terraform.yml/badge.svg)](https://github.com/commjoen/wrongsecrets/actions/workflows/terraform.yml) [![Test minikube script](https://github.com/commjoen/wrongsecrets/actions/workflows/minikube-test.yml/badge.svg)](https://github.com/commjoen/wrongsecrets/actions/workflows/minikube-test.yml) [![OWASP Incubator Project](https://img.shields.io/badge/OWASP-Incubator%20project-48A646.svg)](https://owasp.org/projects/)[![Discussions](https://img.shields.io/github/discussions/commjoen/wrongsecrets)](https://github.com/commjoen/wrongsecrets/discussions)

Welcome to the OWASP WrongSecrets p0wnable app. With this app, we have packed various ways of how to not store your secrets. These can help you to realize whether your secret management is ok. The challenge is to find all the different secrets by means of various tools and techniques.

Can you solve all the 12 challenges?
![screenshot.png](screenshot.png)

## Support

Need support? Contact us via [OWASP Slack](https://owasp.slack.com/archives/C02KQ7D9XHR) [sign up here](https://owasp.org/slack/invite), file a [PR](https://github.com/commjoen/wrongsecrets/pulls), file an [issue](https://github.com/commjoen/wrongsecrets/issues), or use [discussions](https://github.com/commjoen/wrongsecrets/discussions). Please note that this is an OWASP volunteer based project, so it might take a little while before we respond.

## Basic docker exercises

_Can be used for challenges 1-4, 8, 12_

For the basic docker exercises you currently require:

- Docker [Install from here](https://docs.docker.com/get-docker/)
- Some Browser that can render HTML

You can install it by doing:

```bash
docker run -p 8080:8080 jeroenwillemsen/wrongsecrets:1.1.1-no-vault
```

Now you can try to find the secrets by means of solving the challenge offered at:

- [localhost:8080/challenge/1](http://localhost:8080/challenge/1)
- [localhost:8080/challenge/2](http://localhost:8080/challenge/2)
- [localhost:8080/challenge/3](http://localhost:8080/challenge/3)
- [localhost:8080/challenge/4](http://localhost:8080/challenge/4)
- [localhost:8080/challenge/8](http://localhost:8080/challenge/8)
- [localhost:8080/challenge/12](http://localhost:8080/challenge/12)
Note that these challenges are still very basic, and so are their explanations. Feel free to file a PR to make them look better ;-).

### Running these on Heroku

You can test them out at <a href="https://wrongsecrets.herokuapp.com/" target="_blank"> https://wrongsecrets.herokuapp.com/</a> as well! But please understand that we have NO guarantees that this works. Given we run in Heroku free-tier, please do not fuzz and/or try to bring it down: you would be spoiling it for others that want to testdrive it.

### Deploying the app under your own heroku account

1. [Sign up to Heroku](https://signup.heroku.com/) and
   [log in to your account](https://id.heroku.com/login)
2. Click the button below and follow the instructions

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

## Basic K8s exercise

_Can be used for challenges 1-6, 8, 12_

### Minikube based

Make sure you have the following installed:

- Docker [Install from here](https://docs.docker.com/get-docker/)
- Minikube [Install from here](https://minikube.sigs.k8s.io/docs/start/)

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

now you can use the provided IP address and port to further play with the K8s variant (instead of localhost).

- [localhost:8080/challenge/5](http://localhost:8080/challenge/5)
- [localhost:8080/challenge/6](http://localhost:8080/challenge/6)

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

now you can use the provided IP address and port to further play with the K8s variant (instead of localhost).

- [localhost:8080/challenge/5](http://localhost:8080/challenge/5)
- [localhost:8080/challenge/6](http://localhost:8080/challenge/6)

## Vault exercises with minikube

_Can be used for challenges 1-8, 12_
Make sure you have the following installed:

- minikube with docker (or comment out line 8 and work at your own k8s setup),
- docker,
- helm [Install from here](https://helm.sh/docs/intro/install/),
- kubectl [Install from here](https://kubernetes.io/docs/tasks/tools/),
- jq [Install from here](https://stedolan.github.io/jq/download/),
- vault [Install from here](https://www.vaultproject.io/downloads),
- grep, Cat, and Sed

Run `./k8s-vault-minkube-start.sh`, when the script is done, then the challenges will wait for you at <http://localhost:8080> .
This will allow you to run challenge 1-8.

## Cloud Challenges

_Can be used for challenges 1-12_

### Running WrongSecrets in AWS

Follow the steps in [the README in the AWS subfolder](aws/README.md).

### Running WrongSecrets in GCP

Follow the steps in [the README in the GCP subfolder](gcp/README.md).

## Special thanks & Contributors

Leaders:

- [Ben de Haan @bendehaan](https://github.com/bendehaan)
- [Jeroen willemsen @commjoen](https://github.com/commjoen)

Top contributors:

- [Nanne Baars @nbaars](https://github.com/nbaars)
- [Filip Chyla @fchyla](https://github.com/fchyla)
- [Dmitry Litosh @Dlitosh](https://github.com/Dlitosh)

Special mentions for helping out:

- [Madhu Akula @madhuakula](https://github.com/madhuakula)
- [Björn Kimminich @bkimminich](https://github.com/bkimminich)

## Help Wanted

You can help us by the following methods:

- Star us
- Share this app with others
- Of course, we can always use your help to get more flavors of "wrongly" configured secrets in to spread awareness! We would love to get some help with a Google Cloud or Azure integration for instance. Do you miss something else than a cloud provider as an example? File an issue or create a PR! See [our guide on contributing for more details](CONTRIBUTING.md). Contributors will be listed in releases, in "Sepcial thanks & Contributors", and the web-app.

## Notes on development

If you want to test against vault without K8s: start vault locally with

```shell
 export VAULT_ADDR='http://127.0.0.1:8200'
 export VAULT_API_ADDR='http://127.0.0.1:8200'
 vault server -dev
```

and in your next terminal, do (with the token from the previous commands):

```shell
export VAULT_ADDR='http://127.0.0.1:8200'
export VAULT_TOKEN='<TOKENHERE>'
vault token create -id="00000000-0000-0000-0000-000000000000" -policy="root"
vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)"
```

Now use the `local-vault` profile to do your development.

If you want to dev without a Vault instance, use the `without-vault` profile to do your development.

Want to push a container? See `.github/scripts/docker-create-and-push.sh` for a script that generates and pushes all containers. Do not forget to rebuild the app before composing the container

### Automatic reload during development

To make changes made load faster we added `spring-dev-tools` to the Maven project. To enable this in IntelliJ automatically, make sure:

- Under Compiler -> Automatically build project is enabled, and
- Under Advanced settings -> Allow auto-make to start even if developed application is currently running.

You can also manually invoke: Build -> Recompile the file you just changed, this will also force a reload of the application.

### How to add a Challenge

Follow the steps below on adding a challenge:

1. First make sure that you have an [Issue](https://github.com/commjoen/wrongsecrets/issues) reported for which a challenge is really wanteds.
2. Add the new challenge in the `org.owasp.wrongsecrets.challenges` folder. Make sure you add an explanation in `src/main/resources/explanations` and refer to it from your new Challenge class.
3. Add a unit and integration test to show that your challenge is working.

If you want to move existing cloud challenges to antoerh cloud: extend Challenge classes in the `org.owasp.wrongsecrets.challenges.cloud` package and make sure you add the required Terraform in a folder with the separate cloud identified. Collaborate with the others at the project to get your container running so you can test at the cloud account.
