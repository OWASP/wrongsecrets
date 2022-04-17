# Setup your secrets in Azure

In this setup we integrate the secrets exercise with Azure AKS and let pods consume secrets from an Azure Key Vault. If you want to know more about integrating secrets with AKS, check [this link](https://azure.github.io/secrets-store-csi-driver-provider-azure/getting-started/usage/#provide-identity-to-access-key-vault).
Please make sure that the account in which you run this exercise has either Log Analytics enabled, or is not linked to your current subscriptions and/or DTAP environment.

## Pre-requisites

Have the following tools installed:

- az CLI - [Installation](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
- Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
- Terraform CLI - [Installation](https://learn.hashicorp.com/tutorials/terraform/install-cli)
- Wget - [Installation](https://www.jcchouinard.com/wget/)
- Helm [Installation](https://helm.sh/docs/intro/install/)
- Kubectl [Installation](https://kubernetes.io/docs/tasks/tools/)
- jq [Installation](https://stedolan.github.io/jq/download/)

Make sure you have an active subscription at Azure for which you have configured the credentials on the system where you will execute the steps below.

## Installation

**Note-I**: We create resources in `east us` by default. You can set the region by editing `terraform.tfvars`.

**Note-II**: The cluster you create has its access bound to the public IP of the creator. In other words: the cluster you create with this code has its access bound to your public IP-address if you apply it locally.

### (Optional) Multi-user setup: shared state

If you want to host a multi-user setup, you will probably want to share the state file so that everyone can try related challenges. We have provided a starter to easily do so using an Azure storage container.

First, enable the `Microsoft.Storage` API (if it isn't already) using:

```bash
az provider register --namespace Microsoft.Storage
```

Then, apply the Terraform (optionally add `-var="region=YOUR_DESIRED_REGION"` to the apply to use a region other than the default `East US`):

```bash
cd shared-state
terraform init
terraform apply
```

The storage account name should be in the output. Please use that to configure the Terraform backend in `main.tf` by uncommenting the part on the `backend "azurerm"`.

**Note**: You'll need to follow the description below for the "existing resource group" i.e., use the `data.azurerm_resource_group.default` resource.

### WrongSecrets

1. Set either a new resource group or use an existing resource group in `main.tf` (it defaults to the existing `OWASP-Projects` resource group). Note that you'll need to find/replace references to "data.azurerm_resource_group.default" to "arurerm_resource_group.default" if you want to create a new one.
2. check whether you have the right project by doing `az account show` (after `az login`). Want to set the project as your default? Use `az account set --subscription  <.id here>`.
3. If not yet enabled, register the required services for the subscription, run:
    - `az provider register --namespace Microsoft.ContainerService`
    - `az provider register --namespace Microsoft.KeyVault`
    - `az provider register --namespace Microsoft.ManagedIdentity`
4. Run `terraform init` (if required, use `tfenv` to select TF 0.14.0 or higher )
5. Run `terraform plan` to see what will be created (optional).
6. Run `terraform apply`. Note: the apply will take 5 to 20 minutes depending on the speed of the Azure backplane.
7. Run `./k8s-vault-azure-start.sh`. Your kubeconfig file will automatically be updated.
8. (Optional) To make the app available over a load balancer, run `kubectl apply -f ./k8s/lb.yml`, then  look for the public IP using `kubectl describe service wrongsecrets-lb`. The app should be available on HTTP port 80 within a few minutes.

Your AKS cluster should be visible in your resource group. Want a different region? You can modify `terraform.tfvars` or input it directly using the `region` variable in plan/apply.

Are you done playing? Please run `terraform destroy` twice to clean up.

### Test it

Run `./k8s-vault-azure-start.sh` and connect to [http://localhost:8080](http://localhost:8080) when it's ready to accept connections (you'll read the line `Forwarding from 127.0.0.1:8080 -> 8080` in your console). Now challenge 9 and 10 should be available as well.

### Resume it

When you stopped the `k8s-vault-azure-start.sh` script and want to resume the port forward run: `k8s-vault-azure-resume.sh`. This is because if you run the start script again it will replace the secret in the vault and not update the secret-challenge application with the new secret.

### Clean it up

When you're done:

1. Kill the port forward.
2. Run `terraform destroy` to clean up the infrastructure. Note that you may need to repeat the destroy to fully clean up.
3. If you've used the shared state, `cd` to the `shared-state` folder and run `terraform destroy` there too.
4. Run `rm terraform.ts*` to remove local state files.

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the AKS managed identity of the Node?
3. Can you get the secrets in the Key vault? Which paths do you see?
