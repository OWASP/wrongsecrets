# Setup your secrets in GCP

In this setup we integrate the secrets exercise with GCP GKE and let pods consume secrets from the GCP Secret manager. If you want to know more about integrating secrets with GKE, check [this link](https://github.com/GoogleCloudPlatform/secrets-store-csi-driver-provider-gcp).
Please make sure that the account in which you run this exercise has either Cloud Audit Logs enabled, or is not linked to your current organization and/or DTAP environment.

## Pre-requisites

Have the following tools installed:

-   gcloud CLI - [Installation](https://cloud.google.com/sdk/docs/install)
-   Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
-   Terraform CLI - [Installation](https://learn.hashicorp.com/tutorials/terraform/install-cli)
-   Wget - [Installation](https://www.jcchouinard.com/wget/)
-   Helm [Installation](https://helm.sh/docs/intro/install/)
-   Kubectl [Installation](https://kubernetes.io/docs/tasks/tools/)
-   jq [Installation](https://stedolan.github.io/jq/download/)

Make sure you have an active account at GCP for which you have configured the credentials on the system where you will execute the steps below.

Please note that this setup relies on bash scripts that have been tested in MacOS and Linux. We have no intention of supporting vanilla Windows at the moment.


### Multi-user setup: shared state

If you want to host a multi-user setup, you will probably want to share the state file so that everyone can try related challenges. We have provided a starter to easily do so using a Terraform gcs backend.

First, create an storage bucket:

1. Navigate to the 'shared-state' directory `cd shared-state`
2. Change the `project_id` in the `terraform.tfvars` file to your project id
3. Run `terraform init`
4. Run `terraform apply`.

The bucket name should be in the output. Please use that to configure the Terraform backend in `main.tf`.

## Installation

**Note**: Applying the Terraform means you are creating cloud infrastructure which actually costs you money. The authors are not responsible for any cost coming from following the instructions below. If you have a brand new GCP account, you could use the $300 in credits to set up the infrastructure for free.

**Note-II**: We create resources in `europe-west4` by default. You can set the region by editing `terraform.tfvars`.

**Note-III**: The cluster you create has its access bound to the public IP of the creator. In other words: the cluster you create with this code has its access bound to your public IP-address if you apply it locally.

1. Check whether you have the right project by doing `gcloud config list`. Otherwise configure it by doing `gcloud init`.
2. Change the `project_id` in the `terraform.tfvars` file to your project id
3. Run `gcloud auth application-default login` to be able to use your account credentials for terraform.
4. Enable the required gcloud services using `gcloud services enable compute.googleapis.com container.googleapis.com secretmanager.googleapis.com`
5. Run `terraform init` (if required, use tfenv to select TF 0.14.0 or higher )
6. Run `terraform plan`
7. Run `terraform apply`. Note: the apply will take 10 to 20 minutes depending on the speed of the GCP backplane.
8. Run `export USE_GKE_GCLOUD_AUTH_PLUGIN=True`
9. When creation is done, run `gcloud container clusters get-credentials wrongsecrets-exercise-cluster --region YOUR_REGION`. Note if it errors on a missing plugin to support `kubectl`, then run `gcloud components install gke-gcloud-auth-plugin` and `gcloud container clusters get-credentials wrongsecrets-exercise-cluster` .
10. Run `./k8s-vault-gcp-start.sh`

### GKE ingres for shared deployment

By default the deployment uses a nodePort tunneled to localhost. For a larger audience deployment the wrongsecrets app can deployed with a GKE ingress, run `k8s-vault-gcp-ingress-start.sh`
Please note that the GKE ingress can take a few minues to deploy and is publicly available. A connection URL will be returned once the ingress is available. Note that, after the connection URL is returned, a first lookup might still take a minute, after which it is much faster.

Your GKE cluster should be visible in [EU-West4](https://console.cloud.google.com/kubernetes?referrer=search&project=wrongsecrets) by default. Want a different region? You can modify `terraform.tfvars` or input it directly using the `region` variable in plan/apply.

Are you done playing? Please run `terraform destroy` twice to clean up.

### Test it

Run `./k8s-vault-gcp-start.sh` and connect to [http://localhost:8080](http://localhost:8080) when it's ready to accept connections (you'll read the line `Forwarding from 127.0.0.1:8080 -> 8080` in your console). Now challenge 9 and 10 should be available as well.

### Resume it

When you stopped the `k8s-vault-gcp-start.sh` script and want to resume the port forward run: `k8s-vault-gcp-resume.sh`. This is because if you run the start script again it will replace the secret in the vault and not update the secret-challenge application with the new secret.

### Clean it up

When you're done:

1. Kill the port forward.
2. Run `terraform destroy` to clean up the infrastructure.
3. Run `unset KUBECONFIG` to unset the KUBECONFIG env var.
4. Run `rm ~/.kube/wrongsecrets` to remove the kubeconfig file.
5. Run `rm terraform.tfstate*` to remove local state files.

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the GCP IAM role of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secret Manager easily? Which paths do you see?
4. You should see at the configuration details of the cluster that `databaseEncryption` is `DECRYPTED` (`gcloud container clusters describe wrongsecrets-exercise-cluster --region europe-west4`). What does that mean?

## Running Terratest

Want to see if the setup still works? You can use terratest to check if the current setup works via automated terratest tests, for this you need to make sure that you have installed terraform and Go version 1.21. Next, you will need to install the modules and set up credentials.

1. Run `go mod download`.
2. Run `gcloud auth application-default login`.
3. Run `go test -timeout 99999s`. The default timeout is 10 min, which is too short for our purposes. We need to override that.

## Terraform documentation

The documentation below is auto-generated to give insight on what's created via Terraform.

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.1 |
| <a name="requirement_google"></a> [google](#requirement\_google) | ~> 5.4.0 |
| <a name="requirement_google-beta"></a> [google-beta](#requirement\_google-beta) | ~> 5.4.0 |
| <a name="requirement_http"></a> [http](#requirement\_http) | ~> 3.4.0 |
| <a name="requirement_random"></a> [random](#requirement\_random) | ~> 3.5.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_google"></a> [google](#provider\_google) | 5.4.0 |
| <a name="provider_google-beta"></a> [google-beta](#provider\_google-beta) | 5.4.0 |
| <a name="provider_http"></a> [http](#provider\_http) | 3.4.0 |
| <a name="provider_random"></a> [random](#provider\_random) | 3.5.1 |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [google-beta_google_iam_workload_identity_pool.pool](https://registry.terraform.io/providers/hashicorp/google-beta/latest/docs/resources/google_iam_workload_identity_pool) | resource |
| [google_compute_network.vpc](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_network) | resource |
| [google_compute_subnetwork.master_subnet](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_subnetwork) | resource |
| [google_compute_subnetwork.node_subnet](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_subnetwork) | resource |
| [google_container_cluster.gke](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_cluster) | resource |
| [google_project_iam_member.wrongsecrets_cluster_sa_roles](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/project_iam_member) | resource |
| [google_secret_manager_secret.wrongsecret_1](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret) | resource |
| [google_secret_manager_secret.wrongsecret_2](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret) | resource |
| [google_secret_manager_secret.wrongsecret_3](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret) | resource |
| [google_secret_manager_secret_iam_member.wrongsecret_1_member](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret_iam_member) | resource |
| [google_secret_manager_secret_iam_member.wrongsecret_2_member](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret_iam_member) | resource |
| [google_secret_manager_secret_iam_member.wrongsecret_3_member](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret_iam_member) | resource |
| [google_secret_manager_secret_version.secret_version_basic](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/secret_manager_secret_version) | resource |
| [google_service_account.wrongsecrets_cluster](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account) | resource |
| [google_service_account.wrongsecrets_workload](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account) | resource |
| [google_service_account_iam_member.wrongsecret_pod_sa](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account_iam_member) | resource |
| [google_service_account_iam_member.wrongsecret_wrong_pod_sa](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/service_account_iam_member) | resource |
| [random_integer.int](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/integer) | resource |
| [random_password.password](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/password) | resource |
| [http_http.ip](https://registry.terraform.io/providers/hashicorp/http/latest/docs/data-sources/http) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | The GKE cluster name | `string` | `"wrongsecrets-exercise-cluster"` | no |
| <a name="input_cluster_version"></a> [cluster\_version](#input\_cluster\_version) | The GKE cluster version to use | `string` | `"1.28"` | no |
| <a name="input_project_id"></a> [project\_id](#input\_project\_id) | project id | `string` | n/a | yes |
| <a name="input_region"></a> [region](#input\_region) | The GCP region to use | `string` | `"europe-west4"` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_gke_config"></a> [gke\_config](#output\_gke\_config) | config string for the cluster credentials |
| <a name="output_kubernetes_cluster_host"></a> [kubernetes\_cluster\_host](#output\_kubernetes\_cluster\_host) | GKE Cluster Host |
| <a name="output_kubernetes_cluster_name"></a> [kubernetes\_cluster\_name](#output\_kubernetes\_cluster\_name) | GKE Cluster Name |
| <a name="output_project_id"></a> [project\_id](#output\_project\_id) | GCloud Project ID |
| <a name="output_region"></a> [region](#output\_region) | GCloud Region |
<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
