# Setup your secrets in AWS

In this setup we integrate the secrets-exercise online with AWS EKS and let Pods consume secrets from the AWS Parameter Store and AWS Secrets Manager.
We use managed node groups so as we don't want the hassle of managing the EC2 instances ourselves, and Fargate doesn't suit our needs since we use a StatefulSet. If you want to know more about integrating secrets with EKS, check [EKS and SSM Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/integrating_csi_driver.html) and [EKS and Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_csi_driver.html).
Please make sure that the account in which you run this exercise has either CloudTrail enabled, or is not linked to your current organization and/or DTAP environment.

## Pre-requisites

Have the following tools installed:

-   AWS CLI - [Installation](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
-   EKS CTL - [Installation](https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html)
-   Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
-   Terraform CLI - [Installation](https://learn.hashicorp.com/tutorials/terraform/install-cli)
-   Wget - [Installation](https://www.jcchouinard.com/wget/)
-   Helm [Installation](https://helm.sh/docs/intro/install/)
-   Kubectl [Installation](https://kubernetes.io/docs/tasks/tools/)
-   jq [Installation](https://stedolan.github.io/jq/download/)

Make sure you have an active account at AWS for which you have configured the credentials on the system where you will execute the steps below. In this example we stored the credentials under an aws profile as `awsuser`.

Please note that this setup relies on bash scripts that have been tested in MacOS and Linux. We have no intention of supporting vanilla Windows at the moment.

### Multi-user setup: shared state

If you want to host a multi-user setup, you will probably want to share the state file so that everyone can try related challenges. We have provided a starter to easily do so using a Terraform S3 backend.

First, create an s3 bucket (optionally add `-var="region=YOUR_DESIRED_REGION"` to the apply to use a region other than the default eu-west-1):

```bash
cd shared-state
terraform init
terraform apply
```

The bucket name should be in the output. Please use that to configure the Terraform backend in `main.tf`.

## Installation

The terraform code is loosely based on [this EKS managed Node Group TF example](https://github.com/terraform-aws-modules/terraform-aws-eks/tree/master/examples/eks_managed_node_group).

**Note**: Applying the Terraform means you are creating cloud infrastructure which actually costs you money. The authors are not responsible for any cost coming from following the instructions below.

**Note-II**: The cluster you create has its access bound to the public IP of the creator. In other words: the cluster you create with this code has its access bound to your public IP-address if you apply it locally.

1. export your AWS credentials (`export AWS_PROFILE=awsuser`)
2. check whether you have the right profile by doing `aws sts get-caller-identity` and make sure you have enough rights with the caller its identity and that the actual accountnumber displayed is the account designated for you to apply this TF to.
3. Do `terraform init` (if required, use tfenv to select TF 0.13.1 or higher )
4. Do `terraform plan`
5. Do `terraform apply`. Note: the apply will take 10 to 20 minutes depending on the speed of the AWS backplane.
6. When creation is done, do `aws eks update-kubeconfig --region eu-west-1 --name wrongsecrets-exercise-cluster --kubeconfig ~/.kube/wrongsecrets`
7. Do `export KUBECONFIG=~/.kube/wrongsecrets`
8. Run `./k8s-vault-aws-start.sh`

Your EKS cluster should be visible in [EU-West-1](https://eu-west-1.console.aws.amazon.com/eks/home?region=eu-west-1#/clusters) by default. Want a different region? You can modify `terraform.tfvars` or input it directly using the `region` variable in plan/apply.

Are you done playing? Please run `terraform destroy` twice to clean up.

### Test it

Run `AWS_PROFILE=<your_profile> k8s-vault-aws-start.sh` and connect to [http://localhost:8080](http://localhost:8080) when it's ready to accept connections (you'll read the line `Forwarding from 127.0.0.1:8080 -> 8080` in your console). Now challenge 9 and 10 should be available as well.

### Resume it

When you stopped the `k8s-vault-aws-start.sh` script and want to resume the port forward run: `k8s-vault-aws-resume.sh`. This is because if you run the start script again it will replace the secret in the vault and not update the secret-challenge application with the new secret.

### Clean it up

When you're done:

1. Kill the port forward.
2. Run `terraform destroy` to clean up the infrastructure.
    1. If you've deployed the `shared-state` s3 bucket, also `cd shared-state` and `terraform destroy` there.
3. Run `unset KUBECONFIG` to unset the KUBECONFIG env var.
4. Run `rm ~/.kube/wrongsecrets` to remove the kubeconfig file.
5. Run `rm terraform.tfstate*` to remove local state files.

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the instance profile of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secrets Manager easily? Which paths do you see?
4. Which of the 2 (SSM Parameter Store and Secrets Manager) works cross-account?
5. If you have applied the secrets to the cluster, you should see at the configuration details of the cluster that Secrets encryption is "Disabled", what does that mean?

### When you want to share your environment with others (experimental)

We added additional scripts for adding an ALB and ingress so that you can use your cloudsetup with multiple people.
Do the following:

1. Follow the installation section first.
2. Run `k8s-aws-alb-script.sh` and the script will return the url at which you can reach the application.
3. When you are done, before you do cleanup, first run `k8s-aws-alb-script-cleanup.sh`.

Note that you might have to do some manual cleanups after that.

#### TLS

You might want to set up TLS. For that, refer to the [AWS documentation](https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.8/guide/ingress/annotations/#tls).

## Running Terratest

Want to see if the setup still works? You can use terratest to check if the current setup works via automated terratest tests, for this you need to make sure that you have installed terraform and Go version 1.21. Next, you will need to install the modules and set up credentials.

1. Run `go mod download`.
2. Set up your AWS profile using `export AWS_PROFILE=<your-profile-here>`.
3. Run `go test -timeout 99999s`. The default timeout is 10 min, which is too short for our purposes. We need to override that.

## Terraform documentation
The documentation below is auto-generated to give insight on what's created via Terraform.

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.1 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5.52.0 |
| <a name="requirement_http"></a> [http](#requirement\_http) | ~> 3.4.0 |
| <a name="requirement_random"></a> [random](#requirement\_random) | ~> 3.6.0 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | 5.52.0 |
| <a name="provider_http"></a> [http](#provider\_http) | 3.4.2 |
| <a name="provider_random"></a> [random](#provider\_random) | 3.6.2 |

## Modules

| Name | Source | Version |
|------|--------|---------|
| <a name="module_ebs_csi_irsa_role"></a> [ebs\_csi\_irsa\_role](#module\_ebs\_csi\_irsa\_role) | terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks | ~> 5.5 |
| <a name="module_eks"></a> [eks](#module\_eks) | terraform-aws-modules/eks/aws | 20.13.0 |
| <a name="module_vpc"></a> [vpc](#module\_vpc) | terraform-aws-modules/vpc/aws | ~> 5.8.1 |

## Resources

| Name | Type |
|------|------|
| [aws_iam_policy.secret_deny](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_policy.secret_manager](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_role.irsa_role](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role) | resource |
| [aws_iam_role.user_role](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role) | resource |
| [aws_iam_role_policy_attachment.irsa_role_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_role_policy_attachment.user_role_attachment](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_secretsmanager_secret.secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret.secret_2](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret) | resource |
| [aws_secretsmanager_secret_policy.policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_policy.policy_2](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_policy) | resource |
| [aws_secretsmanager_secret_version.secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/secretsmanager_secret_version) | resource |
| [aws_ssm_parameter.secret](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ssm_parameter) | resource |
| [random_password.password](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/password) | resource |
| [random_password.password2](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/password) | resource |
| [aws_availability_zones.available](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/availability_zones) | data source |
| [aws_caller_identity.current](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/caller_identity) | data source |
| [aws_iam_policy_document.assume_role_with_oidc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.secret_manager](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.user_assume_role](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.user_policy](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [http_http.ip](https://registry.terraform.io/providers/hashicorp/http/latest/docs/data-sources/http) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cluster_name"></a> [cluster\_name](#input\_cluster\_name) | The EKS cluster name | `string` | `"wrongsecrets-exercise-cluster"` | no |
| <a name="input_cluster_version"></a> [cluster\_version](#input\_cluster\_version) | The EKS cluster version to use | `string` | `"1.29"` | no |
| <a name="input_region"></a> [region](#input\_region) | The AWS region to use | `string` | `"eu-west-1"` | no |
| <a name="input_tags"></a> [tags](#input\_tags) | List of tags to apply to resources | `map(string)` | <pre>{<br>  "Application": "wrongsecrets"<br>}</pre> | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_cluster_endpoint"></a> [cluster\_endpoint](#output\_cluster\_endpoint) | Endpoint for EKS control plane. |
| <a name="output_cluster_id"></a> [cluster\_id](#output\_cluster\_id) | The id of the cluster |
| <a name="output_cluster_security_group_id"></a> [cluster\_security\_group\_id](#output\_cluster\_security\_group\_id) | Security group ids attached to the cluster control plane. |
| <a name="output_irsa_role"></a> [irsa\_role](#output\_irsa\_role) | The role ARN used in the IRSA setup |
| <a name="output_secrets_manager_secret_name"></a> [secrets\_manager\_secret\_name](#output\_secrets\_manager\_secret\_name) | The name of the secrets manager secret |
<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
