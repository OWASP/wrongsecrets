# WrongSecrets Desktop

Welcome to the WrongSecretss desktop/toolcontainer! With this Docker container you have the minimal tools next to Docker ready on your finger tips.
Need more? Don't worry: you can always do `sudo apk add <package>` .

## What is inside

We have packed it with the following content:

### Tools

The WrongSecrets Desktop contains the following tools:

- Radare2 for reverse engineering (Use it with `r2`/`radare2` in the commandline)
- OpenSSL for encoding/decoding
- AWS-cli for AWS challenges (Use it with `aws` in the commandline)
- KeepassXC for password manager related challenges (Use it with `keepassXC` in the commandline)
- Firefox
- Docker (disabled in clod env)
- Kubectl
- Geany to have a look at the code (use it with `geany` in the commandline)

### Binaries to play with

We added the Keepass file and the binaries for the reverse-engineer challenges to /config/Desktop/wrongsecrets.
Just open the `wrongsecrets` folder on the Desktop and you will find it.

### Note on kubectl

When working in a minikube deployment: make sure to export KUBERNETES_SERVICE_HOST and KUBERNETES_SERVICE_PORT first!
on Aws that would be: 
export KUBERNETES_SERVICE_HOST=10.100.0.1
export KUBERNETES_SERVICE_PORT=443
export KUBERNETES_SERVICE_PORT_HTTPS=443
