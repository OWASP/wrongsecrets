<!-- CRE Link: [223-780](https://www.opencre.org/cre/223-780?register=true&type=tool&tool_type=training&tags=secrets,training&description=With%20this%20app%2C%20we%20have%20packed%20various%20ways%20of%20how%20to%20not%20store%20your%20secrets.%20These%20can%20help%20you%20to%20realize%20whether%20your%20secret%20management%20is%20ok.%20The%20challenge%20is%20to%20find%20all%20the%20different%20secrets%20by%20means%20of%20various%20tools%20and%20techniques.%20Can%20you%20solve%20all%20the%2015%20challenges%3F) -->

# OWASP WrongSecrets [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Want%20to%20dive%20into%20secrets%20management%20and%20do%20some%20hunting?%20try%20this&url=https://github.com/OWASP/wrongsecrets&hashtags=secretsmanagement,secrets,hunting,p0wnableapp,OWASP,WrongSecrets)[<img src="https://raw.githubusercontent.com/mastodon/mastodon/main/app/javascript/images/app-icon.svg" width=16>](https://tootpick.org/#text=Want%20to%20dive%20into%20secrets%20management%20and%20do%20some%20hunting?%20try%20this%0A%0Ahttps://github.com/OWASP/wrongsecrets%20%23secretsmanagement,%20%23secrets,%20%23hunting,%20%23p0wnableapp,%20%23OWASP,%20%23WrongSecrets)

[![Java checkstyle and testing](https://github.com/OWASP/wrongsecrets/actions/workflows/main.yml/badge.svg)](https://github.com/OWASP/wrongsecrets/actions/workflows/main.yml) ![Pre-commit](https://github.com/OWASP/wrongsecrets/actions/workflows/pre-commit.yml/badge.svg)[![Terraform FMT](https://github.com/OWASP/wrongsecrets/actions/workflows/terraform.yml/badge.svg)](https://github.com/OWASP/wrongsecrets/actions/workflows/terraform.yml) [![Test minikube script (k8s)](https://github.com/OWASP/wrongsecrets/actions/workflows/minikube-k8s-test.yml/badge.svg)](https://github.com/OWASP/wrongsecrets/actions/workflows/minikube-k8s-test.yml) [![Test minikube script (k8s&vault)](https://github.com/OWASP/wrongsecrets/actions/workflows/minikube-vault-test.yml/badge.svg)](https://github.com/OWASP/wrongsecrets/actions/workflows/minikube-vault-test.yml) [![Docker container test](https://github.com/OWASP/wrongsecrets/actions/workflows/container_test.yml/badge.svg)](https://github.com/OWASP/wrongsecrets/actions/workflows/container_test.yml)
[![OWASP Lab Project](https://img.shields.io/badge/OWASP-lab%20project-48A646.svg)](https://owasp.org/projects/)
[![OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/projects/7024/badge)](https://bestpractices.coreinfrastructure.org/projects/7024)
[![Discussions](https://img.shields.io/github/discussions/OWASP/wrongsecrets)](https://github.com/OWASP/wrongsecrets/discussions)

Welcome to the OWASP WrongSecrets p0wnable app. With this app, we have packed various ways of how to not store your
secrets. These can help you to realize whether your secret management is ok. The challenge is to find all the different
secrets by means of various tools and techniques.

Can you solve all the 28 challenges?
![screenshotOfChallenge1](/images/screenshot.png)

<a href="https://github.com/vshymanskyy/StandWithUkraine/blob/main/README.md"><img src="https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-no-action.svg" /></a>

## Table of contents

-   [Support](#support)
-   [Basic docker exercises](#basic-docker-exercises)
    -   [Running these on Heroku](#running-these-on-heroku)
    -   [Running on Fly.io](#running-on-flyio)
-   [Basic K8s exercise](#basic-k8s-exercise)
    -   [Minikube based](#minikube-based)
    -   [k8s based](#k8s-based)
    -   [Okteto based](#okteto-based)
    -   [Vault exercises with minikube](#vault-exercises-with-minikube)
-   [Cloud Challenges](#cloud-challenges)
    -   [Running WrongSecrets in AWS](#running-wrongsecrets-in-aws)
    -   [Running WrongSecrets in GCP](#running-wrongsecrets-in-gcp)
    -   [Running WrongSecrets in Azure](#running-wrongsecrets-in-azure)
    -   [Running Challenge15 in your own cloud only](#running-challenge15-in-your-own-cloud-only)
-   [Do you want to play without guidance?](#do-you-want-to-play-without-guidance)
-   [Special thanks & Contributors](#special-thanks--contributors)
-   [Sponsorships](#sponsorships)
-   [Help Wanted](#help-wanted)
-   [Use OWASP WrongSecrets as a secret detection benchmark](#use-owasp-wrongsecrets-as-a-secret-detection-benchmark)
-   [CTF](#ctf)
    -   [CTFD Support](#ctfd-support)
    -   [FBCTF Support](#fbctf-support--experimental--)
-   [Notes on development](#notes-on-development)
    -   [Dependency management](#dependency-management)
    -   [Get the project started in IntelliJ IDEA](#get-the-project-started-in-intellij-idea)
    -   [Automatic reload during development](#automatic-reload-during-development)
    -   [How to add a Challenge](#how-to-add-a-challenge)
    -   [Local testing](#local-testing)
-   [Want to play, but are not allowed to install the tools?](#want-to-play-but-are-not-allowed-to-install-the-tools)
-   [Further reading on secrets management](#further-reading-on-secrets-management)

## Support

Need support? Contact us
via [OWASP Slack](https://owasp.slack.com/archives/C02KQ7D9XHR) for which you sign up [here](https://owasp.org/slack/invite)
, file a [PR](https://github.com/OWASP/wrongsecrets/pulls), file
an [issue](https://github.com/OWASP/wrongsecrets/issues) , or
use [discussions](https://github.com/OWASP/wrongsecrets/discussions). Please note that this is an OWASP volunteer
based project, so it might take a little while before we respond.

Copyright (c) 2020-2023 Jeroen Willemsen and WrongSecrets contributors.

## Basic docker exercises

_Can be used for challenges 1-4, 8, 12-28_

For the basic docker exercises you currently require:

-   Docker [Install from here](https://docs.docker.com/get-docker/)
-   Some Browser that can render HTML

You can install it by doing:

```bash
docker run -p 8080:8080 jeroenwillemsen/wrongsecrets:latest-no-vault
```

Now you can try to find the secrets by means of solving the challenge offered at:

-   [localhost:8080/challenge/1](http://localhost:8080/challenge/1)
-   [localhost:8080/challenge/2](http://localhost:8080/challenge/2)
-   [localhost:8080/challenge/3](http://localhost:8080/challenge/3)
-   [localhost:8080/challenge/4](http://localhost:8080/challenge/4)
-   [localhost:8080/challenge/8](http://localhost:8080/challenge/8)
-   [localhost:8080/challenge/12](http://localhost:8080/challenge/12)
-   [localhost:8080/challenge/13](http://localhost:8080/challenge/13)
-   [localhost:8080/challenge/14](http://localhost:8080/challenge/14)
-   [localhost:8080/challenge/15](http://localhost:8080/challenge/15)
-   [localhost:8080/challenge/16](http://localhost:8080/challenge/16)
-   [localhost:8080/challenge/17](http://localhost:8080/challenge/17)
-   [localhost:8080/challenge/18](http://localhost:8080/challenge/18)
-   [localhost:8080/challenge/19](http://localhost:8080/challenge/19)
-   [localhost:8080/challenge/20](http://localhost:8080/challenge/20)
-   [localhost:8080/challenge/21](http://localhost:8080/challenge/21)
-   [localhost:8080/challenge/22](http://localhost:8080/challenge/22)
-   [localhost:8080/challenge/23](http://localhost:8080/challenge/23)
-   [localhost:8080/challenge/24](http://localhost:8080/challenge/24)
-   [localhost:8080/challenge/25](http://localhost:8080/challenge/25)
-   [localhost:8080/challenge/26](http://localhost:8080/challenge/26)
-   [localhost:8080/challenge/27](http://localhost:8080/challenge/27)
-   [localhost:8080/challenge/28](http://localhost:8080/challenge/28)

Note that these challenges are still very basic, and so are their explanations. Feel free to file a PR to make them look
better ;-).

### Running these on Heroku

You can test them out at [https://wrongsecrets.herokuapp.com/](https://wrongsecrets.herokuapp.com/) as well! But please
understand that we have NO guarantees that this works. Given we run in Heroku free-tier, please do not fuzz and/or try
to bring it down: you would be spoiling it for others that want to testdrive it.

### Running on Fly.io

You can test them out at [https://wrongsecrets.fly.dev](https://wrongsecrets.fly.dev) as well! Please understand that we run on a free-tier instance, we cannot give any guarantees. Please do not fuzz and/or try to bring it down: you would be spoiling it for others that want to testdrive it.

## Basic K8s exercise

_Can be used for challenges 1-6, 8, 12-28_

### Minikube based

Make sure you have the following installed:

-   Docker [Install from here](https://docs.docker.com/get-docker/)
-   Minikube [Install from here](https://minikube.sigs.k8s.io/docs/start/)

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

-   [localhost:8080/challenge/5](http://localhost:8080/challenge/5)
-   [localhost:8080/challenge/6](http://localhost:8080/challenge/6)

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

-   [localhost:8080/challenge/5](http://localhost:8080/challenge/5)
-   [localhost:8080/challenge/6](http://localhost:8080/challenge/6)

### Okteto based

[![Develop on Okteto](https://okteto.com/develop-okteto.svg)](https://cloud.okteto.com/deploy?repository=https://github.com/OWASP/wrongsecrets.git&branch=master&filename=okteto/okteto.yml)

Don't want to go over the hassle of setting up K8S yourself? visit [https://wrongsecrets-commjoen.cloud.okteto.net](https://wrongsecrets-commjoen.cloud.okteto.net/). Please note that we are using the free Developer version here, so it might take a while for it to respond at first (e.g. "development environment not ready" and then a 50x for a minute). Please: do not try to hack/Fuzz the application as this might bring it down and spoil the fun for others.

## Vault exercises with minikube

_Can be used for challenges 1-8, 12-28_
Make sure you have the following installed:

-   minikube with docker (or comment out line 8 and work at your own k8s setup),
-   docker,
-   helm [Install from here](https://helm.sh/docs/intro/install/),
-   kubectl [Install from here](https://kubernetes.io/docs/tasks/tools/),
-   jq [Install from here](https://stedolan.github.io/jq/download/),
-   vault [Install from here](https://www.vaultproject.io/downloads),
-   grep, Cat, and Sed

Run `./k8s-vault-minkube-start.sh`, when the script is done, then the challenges will wait for you at <http://localhost:8080> . This will allow you to run challenges 1-8, 12-22.

When you stopped the `k8s-vault-minikube-start.sh` script and want to resume the port forward run: `k8s-vault-minikube-resume.sh`.
This is because if you run the start script again it will replace the secret in the vault and not update the secret-challenge application with the new secret.

## Cloud Challenges

_Can be used for challenges 1-28_

**READ THIS**: Given that the exercises below contain IAM privilege escalation exercises,
never run this on an account which is related to your production environment or can influence your account-over-arching
resources.

### Running WrongSecrets in AWS

Follow the steps in [the README in the AWS subfolder](aws/README.md).

### Running WrongSecrets in GCP

Follow the steps in [the README in the GCP subfolder](gcp/README.md).

### Running WrongSecrets in Azure

Follow the steps in [the README in the Azure subfolder](azure/README.md).

### Running Challenge15 in your own cloud only

When you want to include your own Canarytokens for your cloud-deployment, do the following:

1. Fork the project.
2. Make sure you use the [GCP ingress](/gcp/k8s-vault-gcp-ingress-start.sh) or [AWS ingress](aws/k8s-aws-alb-script.sh) scripts to generate an ingress for your project.
3. Go to [canarytokens.org](https://canarytokens.org/generate) and select `AWS Keys`, in the webHook URL field add `<your-domain-created-at-step1>/canaries/tokencallback`.
4. Encrypt the received credentials so that [Challenge15](/src/main/java/org/owasp/wrongsecrets/challenges/docker/Challenge15.java) can decrypt them again.
5. Commit the unencrypted and encrypted materials to Git and then commit again without the decrypted materials.
6. Adapt the hints of Challenge 15 in your fork to point to your fork.
7. Create a container and push it to your registry
8. Override the K8s definition files for either [AWS](/aws/k8s/secret-challenge-vault-deployment.yml) or [GCP](/gcp/k8s/secret-challenge-vault-deployment.yml.tpl).

## Do you want to play without guidance?

Each challenge has a `Show hints` button and a `What's wrong?` button. These buttons help to simplify the challenges and give explanation to the reader. Though, the explanations can spoil the fun if you want to do this as a hacking exercise.
Therefore, you can manipulate them by overriding the following settings in your env:

-   `hints_enabled=false` will turn off the `Show hints` button.
-   `reason_enabled=false` will turn of the `What's wrong?` explanation button.

## Special thanks & Contributors

Leaders:

-   [Ben de Haan @bendehaan](https://github.com/bendehaan)
-   [Jeroen Willemsen @commjoen](https://github.com/commjoen)

Top contributors:

-   [Nanne Baars @nbaars](https://github.com/nbaars)
-   [Joss Sparkes @remakingeden](https://github.com/remakingeden)
-   [Marcin Nowak @MarcinNowak-codes](https://github.com/MarcinNowak-codes)
-   [Tibor Hercz @tiborhercz](https://github.com/tiborhercz)
-   [Chris Elbring Jr. @neatzsche](https://github.com/neatzsche)
-   [Puneeth Y @puneeth072003](https://github.com/puneeth072003)
-   [Filip Chyla @fchyla](https://github.com/fchyla)
-   [Dmitry Litosh @Dlitosh](https://github.com/Dlitosh)
-   [Josh Grossman @tghosth](https://github.com/tghosth)
-   [Spyros @northdpole](https://github.com/northdpole)
-   [Mike Woudenberg @mikewoudenberg](https://github.com/mikewoudenberg)
-   [Ruben Kruiver @RubenAtBinx](https://github.com/RubenAtBinx)
-   [Nicolas Humblot @nhumblot](https://github.com/nhumblot)
-   [Finn @f3rn0s](https://github.com/f3rn0s)
-   [Alex Bender @alex-bender](https://github.com/alex-bender)
-   [Rick M @kingthorin](https://github.com/kingthorin)

Testers:

-   [Dave van Stein @davevs](https://github.com/davevs)
-   [Marcin Nowak @MarcinNowak-codes](https://github.com/MarcinNowak-codes)
-   [Marc Chang Sing Pang @mchangsp](https://github.com/mchangsp)

Special mentions for helping out:

-   [Madhu Akula @madhuakula](https://github.com/madhuakula)
-   [Björn Kimminich @bkimminich](https://github.com/bkimminich)
-   [Xiaolu Dai @saragluna](https://github.com/saragluna)
-   [Jonathan Giles @jonathanGiles](https://github.com/JonathanGiles)

### Sponsorships

We would like to thank the following parties for helping us out:

[![gitguardian_logo.png](images/gitguardian_logo.jpeg)](https://blog.gitguardian.com/gitguardian-is-proud-sponsor-of-owasp/)

[GitGuardian](https://www.gitguardian.com/) for their sponsorship which allows us to pay the bills for our cloud-accounts.

[![jetbrains_logo.png](images/jetbrains_logo.png)](https://www.jetbrains.com/)

[Jetbrains](https://www.jetbrains.com/) for licensing an instance of Intellij IDEA Ultimate edition to the project leads. We could not have been this fast with the development without it!

[![docker_logo.png](images/docker_logo.png)](https://www.docker.com)

[Docker](https://www.docker.com) for granting us their Docker Open Source Sponsored program.

[![1password_logo.png](images/1password_logo.png)](https://github.com/1Password/1password-teams-open-source/pull/552)

[1Password](https://1password.com/) for granting us an open source license to 1Password for the secret detection testbed.

## Help Wanted

You can help us by the following methods:

-   Star us
-   Share this app with others
-   Of course, we can always use your help [to get more flavors](https://github.com/OWASP/wrongsecrets/issues/37) of "wrongly" configured secrets in to spread awareness! We would love to get some help with other cloud providers, like Alibaba or Tencent cloud for instance. Do you miss something else than a cloud provider? File an issue or create a PR! See [our guide on contributing for more details](CONTRIBUTING.md). Contributors will be listed in releases, in the "Special thanks & Contributors"-section, and the web-app.

## Use OWASP WrongSecrets as a secret detection benchmark

As tons of secret detection tools are coming up for both Docker and Git, we are creating a Benchmark testbed for it.
Want to know if your tool detects everything? We will keep track of the embedded secrets in [this issue](https://github.com/OWASP/wrongsecrets/issues/201) and have a [branch](https://github.com/OWASP/wrongsecrets/tree/experiment-bed) in which we put additional secrets for your tool to detect.
The branch will contain a Docker container generation script using which you can eventually test your container secret scanning.

## CTF

We have 3 ways of playing CTFs:

-   The quick "let's play"-approach based on our own Heroku domain [https://wrongsecrets-ctf.herokuapp.com](https://wrongsecrets-ctf.herokuapp.com) or our Okteto domain [https://wrongsecrets-ctf-commjoen.cloud.okteto.net/](https://wrongsecrets-ctf-commjoen.cloud.okteto.net/), which we documented for you here.
-   A more extended approach documented in [ctf-instructions.md](/ctf-instructions.md).
-   A fully customizable CTF setup where every player gets its own virtual instance of WrongSecrets and a virtual instance of the wrongsecrets-desktop, so they all can play hassle-free. For this you have to use [the WrongSecrets CTF Party setup](https://github.com/OWASP/wrongsecrets-ctf-party).

### CTFD Support

Want to use CTFD to play a CTF based on the free Heroku wrongsecrets-ctf instance together with CTFD? You can!

NOTE: CTFD support now works based on the [Juiceshop CTF CLI](https://github.com/juice-shop/juice-shop-ctf).

NOTE-II: [https://wrongsecrets-ctf.herokuapp.com](https://wrongsecrets-ctf.herokuapp.com) (temporary down based on lack of oss credits) is based on Heroku and has limited capacity. Alternatively you can use our Okteto setup at [https://wrongsecrets-ctf-commjoen.cloud.okteto.net/](https://wrongsecrets-ctf-commjoen.cloud.okteto.net/), which uses a free tier and needs some time to warm up. However, the Okteto environment does have more resources & supports the kubernetes challenges, unlike our Heroku setup that only supports the Docker challenges.

Initial creation of the zip file for CTFD requires you to visit [https://wrongsecrets-ctf.herokuapp.com/api/Challenges](https://wrongsecrets-ctf.herokuapp.com/api/Challenges) or [https://wrongsecrets-ctf-commjoen.cloud.okteto.net/](https://wrongsecrets-ctf-commjoen.cloud.okteto.net/) once before executing the steps below.

Follow the following steps:

```shell
    npm install -g juice-shop-ctf-cli@9.1.0
    juice-shop-ctf #choose ctfd and https://wrongsecrets-ctf.herokuapp.com (or https://wrongsecrets-ctf-commjoen.cloud.okteto.net/) as domain. No trailing slash! The key is 'TRwzkRJnHOTckssAeyJbysWgP!Qc2T', feel free to enable hints. We do not support snippets or links/urls to code or hints.
    docker run -p 8001:8000 -it ctfd/ctfd:3.4.3
```

Now visit the CTFD instance at [http://localhost:8001](http://localhost:8001) and setup your CTF.
Then use the administrative backup function to import the zipfile you created with the juice-shop-ctf command.
Game on using [https://wrongsecrets-ctf.herokuapp.com](https://wrongsecrets-ctf.herokuapp.com) or [https://wrongsecrets-ctf-commjoen.cloud.okteto.net/](https://wrongsecrets-ctf-commjoen.cloud.okteto.net/)!
Want to setup your own? You can! Watch out for people finding your key though, so secure it properly: make sure the running container with the actual ctf-key is not exposed to the audience, similar to our heroku container.

## FBCTF Support (Experimental!)

NOTE: FBCTF support is experimental.

Follow the same step as with CTFD, only now choose fbctfd and as a url for the countrymapping choose `https://raw.githubusercontent.com/OWASP/wrongsecrets/79a982558016c8ce70948a8106f9a2ee5b5b9eea/config/fbctf.yml`.
Then follow [https://github.com/facebookarchive/fbctf/wiki/Quick-Setup-Guide](https://github.com/facebookarchive/fbctf/wiki/Quick-Setup-Guide) to run the FBCTF.

## Notes on development

For development on local machine use the `local` profile `./mvnw spring-boot:run -Dspring-boot.run.profiles=local,without-vault`

If you want to test against vault without K8s: start vault locally with

```shell
 export SPRING_CLOUD_VAULT_URI='http://127.0.0.1:8200'
 export VAULT_API_ADDR='http://127.0.0.1:8200'
 vault server -dev
```

and in your next terminal, do (with the token from the previous commands):

```shell
export SPRING_CLOUD_VAULT_URI='http://127.0.0.1:8200'
export SPRING_CLOUD_VAULT_TOKEN='<TOKENHERE>'
vault token create -id="00000000-0000-0000-0000-000000000000" -policy="root"
vault kv put secret/secret-challenge vaultpassword.password="$(openssl rand -base64 16)"
```

Now use the `local-vault` profile to do your development.

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local,local-vault
```

If you want to dev without a Vault instance, use additionally the `without-vault` profile to do your development:

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local,without-vault
```

Want to push a container? See `.github/scripts/docker-create-and-push.sh` for a script that generates and pushes all containers. Do not forget to rebuild the app before composing the container

### Dependency management

We have CycloneDX and OWASP Dependency-check integrated to check dependencies for vulnerabilities.
You can use the OWASP Dependency-checker by calling `mvn dependency-check:aggregate` and `mvn cyclonedx:makeBom` to use CycloneDX to create an SBOM.

### Get the project started in IntelliJ IDEA

Requirements: make sure you have the following tools installed: [Docker](https://www.docker.com/products/docker-desktop/), [Java19 JDK](https://jdk.java.net/19/), [NodeJS 18](https://nodejs.org/dist/v18.12.1/node-v18.12.1-x86.msi) and [IntelliJ IDEA](https://www.jetbrains.com/idea/download).

1. Fork and clone the project as described in the [documentation](https://github.com/OWASP/wrongsecrets/blob/master/CONTRIBUTING.md).
2. Import the project in IntelliJ (e.g. import as mvn project / local sources)
3. Go to the project settings and make sure it uses Java19 (And that the JDK can be found)
4. Go to the IDE settings>Language & Frameworks > Lombok and make sure Lombok processing is enabled
5. Open the Maven Tab in your IDEA and run "Reload All Maven Projects" to make the system sync and download everything. Next, in that same tab use the "install" option as part of the OWASP WrongSecrets Lifecycle to genereate the asciidoc and such.
6. Now run the `main` method in `org.owasp.wrongsecrets.WrongSecretsApplication.java`. This should fail with a stack trace.
7. Now go to the run configuration of the app and make sure you have the active profile `without-vault`. This is done by setting the VM options arguments to `-Dserver.port=8080 -Dspring.profiles.active=local,without-vault`. Set `K8S_ENV=docker` as environment argument.
8. Repeat step 6: run the app again, you should have a properly running application which is visitable in your browser at http://localhost:8080.

**Pictorial Guide** on how to get the project started in IntelliJ IDEA is available at [_Contributing.md_](https://github.com/OWASP/wrongsecrets/blob/master/CONTRIBUTING.md#how-to-get-started-with-the-project-in-intellij-idea).

Feel free to edit and propose changes via pull requests. Be sure to follow our guidance in the [documentation](https://github.com/OWASP/wrongsecrets/blob/master/CONTRIBUTING.md) to get your work accepted.

Please note that we officially only support Linux and MacOS for development. If you want to develop using a Windows machine, use WSL2 or a virtual machine running Linux. We did include Windows detection & a bunch of `exe` files for a first experiment, but are looking for active maintainers of them. Want to make sure it runs on Windows? Create PRs ;-).

If, after reading this section, you still have no clue on the application code: Have a look [at some tutorials on Spring boot from Baeldung](https://www.baeldung.com/spring-boot)

### Automatic reload during development

To make changes made load faster we added `spring-dev-tools` to the Maven project.
To enable this in IntelliJ automatically, make sure:

-   Under Compiler -> Automatically build project is enabled, and
-   Under Advanced settings -> Allow auto-make to start even if developed application is currently running.

You can also manually invoke: Build -> Recompile the file you just changed, this will also force reloading of the application.

### How to add a Challenge

Follow the steps below on adding a challenge:

1. First make sure that you have an [Issue](https://github.com/OWASP/wrongsecrets/issues) reported for which a challenge is really wanted.
2. Add the new challenge in the `org.owasp.wrongsecrets.challenges` folder. Make sure you add an explanation in `src/main/resources/explanations` and refer to it from your new Challenge class.
3. Add a unit and integration test to show that your challenge is working.
4. Don't forget to add `@Order` annotation to your challenge ;-).
5. Review the [CONTRIBUTING guide](CONTRIBUTING.md) for setting up your contributing environment and writing good commit messages.

For more details please refer [_Contributing.md_](https://github.com/OWASP/wrongsecrets/blob/master/CONTRIBUTING.md#how-to-add-a-challenge).

If you want to move existing cloud challenges to another cloud: extend Challenge classes in the `org.owasp.wrongsecrets.challenges.cloud` package and make sure you add the required Terraform in a folder with the separate cloud identified. Make sure that the environment is added to `org.owasp.wrongsecrets.RuntimeEnvironment`.
Collaborate with the others at the project to get your container running so you can test at the cloud account.

### Local testing

If you have made some changes to the codebase or added a new challenge and would like to see exactly how the container will look after merge for testing, we have a script that makes this very easy. Follow the steps below:

1. Ensure you have bash installed and open.
2. Navigate to .github/scripts.
3. Run the docker-create script `bash docker-create.sh`.
4. Follow any instructions given, you made need to install/change packages.
5. Run the newly created container `docker run -p 8080:8080 jeroenwillemsen/wrongsecrets:local-test`

## Want to play, but are not allowed to install the tools?

If you want to play the challenges, but cannot install tools like keepass, Radare, etc. But are allowed to run Docker containers, try the following:

```shell
docker run -p 3000:3000 -v /var/run/docker.sock:/var/run/docker.sock jeroenwillemsen/wrongsecrets-desktop:latest
```

or use something more configurable:

```shell
docker run -d \
  --name=webtop \
  --security-opt seccomp=unconfined \
  -e PUID=1000 \
  -e PGID=1000 \
  -e TZ=Europe/London \
  -e SUBFOLDER=/ \
  -e KEYBOARD=en-us-qwerty \
  -p 3000:3000 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --shm-size="2gb" \
  --restart unless-stopped \
  jeroenwillemsen/wrongsecrets-desktop:latest
```

And then at [http://localhost:3000](http://localhost:3000).

Note: be careful with trying to deploy the `jeroenwillemsen/wrongsecrets-desktop` container to Heroku ;-).

## Further reading on secrets management

Want to learn more? Checkout the sources below:

-   [Blog: 10 Pointers on Secrets Management](https://dev.to/commjoen/secure-deployment-10-pointers-on-secrets-management-187j)
-   [OWASP SAMM on Secret Management](https://owaspsamm.org/model/implementation/secure-deployment/stream-b/)
-   [The secret detection topic at Github](https://github.com/topics/secrets-detection)
-   [OWASP Secretsmanagement Cheatsheet](https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Secrets_Management_Cheat_Sheet.md)
-   [OpenCRE on secrets management](https://www.opencre.org/cre/223-780?register=true&type=tool&tool_type=training&tags=secrets,training&description=With%20this%20app%2C%20we%20have%20packed%20various%20ways%20of%20how%20to%20not%20store%20your%20secrets.%20These%20can%20help%20you%20to%20realize%20whether%20your%20secret%20management%20is%20ok.%20The%20challenge%20is%20to%20find%20all%20the%20different%20secrets%20by%20means%20of%20various%20tools%20and%20techniques.%20Can%20you%20solve%20all%20the%2014%20challenges%3F&trk=flagship-messaging-web&messageThreadUrn=urn:li:messagingThread:2-YmRkNjRkZTMtNjRlYS00OWNiLWI2YmUtMDYwNzY3ZjI1MDcyXzAxMg==&lipi=urn:li:page:d_flagship3_feed;J58Sgd80TdanpKWFMH6z+w==)
