# WrongSecrets Desktop

Welcome to the WrongSecretss desktop/toolcontainer! With this Docker container you have the minimal tools next to Docker ready on your finger tips.
Need more? Don't worry: you can always do `sudo apk add <package>` .

## Warning in CTF-mode

Please note that this is a 1:1 user interface: if you are running a CTF as a team, be aware you cannot run the webtop together at the same time. You will have to reconnect after someone else connected.

## What is inside

We have packed it with the following content:

### Tools

The WrongSecrets Desktop contains the following tools:

- Radare2 for reverse engineering (Use it with `r2`/`radare2` in the commandline)
- OpenSSL for encoding/decoding
- AWS-cli for AWS challenges (Use it with `aws` in the commandline, might be disabled during CTF)
- KeepassXC for password manager related challenges (Use it with `keepassXC` in the commandline)
- Firefox
- Docker (disabled in cloud env/CTF challenges)
- Kubectl
- Geany to have a look at the code (use it with `geany` in the commandline)

### Binaries to play with

We added the Keepass file and the binaries for the reverse-engineer challenges to /config/Desktop/wrongsecrets.
Just open the `wrongsecrets` folder on the Desktop and you will find it.

### Note on kubectl

When working in a minikube deployment: make sure to export KUBERNETES_SERVICE_HOST and KUBERNETES_SERVICE_PORT first!
on Aws that would be:

```shell
export KUBERNETES_SERVICE_HOST=10.100.0.1
export KUBERNETES_SERVICE_PORT=8443
export KUBERNETES_SERVICE_PORT_HTTPS=8443
```
Where the SERVICE_HOST IP might be different. You can find the right IP with the command below at the host serving the minikube:

```shell
kubectl -n kube-system get pod -l component=kube-apiserver -o=jsonpath="{.items[0].metadata.annotations.kubeadm\.kubernetes\.io/kube-apiserver\.advertise-address\.endpoint}"
```


### When you are in a CTF

- Want to get back to the overview of your environments? just go to /balancer
- Want to know where to hand over the actual flag? Check with your CTF instructor if you lost the URLs.
- Want to use AWS Cli and/or checkout the code in Git and/or check a container with the docker cli? Please use your own computer. In most cases you can use the online services (https://github.com/OWASP/wrongsecrets, https://hub.docker.com/r/jeroenwillemsen/wrongsecrets) to find the information you are looking for. For the AWS state-related challenge your CTF instructor will release credentials you can use to checkout the shared state file.
- Note that we have limited what you can do in your desktop in terms of file I/O. Please use /var/tmp/wrongsecrets to play around with the files instead.
