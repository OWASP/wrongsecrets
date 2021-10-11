# Setup your secrets in AWS

In this setup we integrate the secrets-exercise online at AWS Fargate and let Pods consume secrets from the AWS Parameter Store and AWS Secrets Manager.
Why Fargate? We on't want the hassle of managing the EC2 instances ourselves. If you want to know more about integrating them with EKS, check [EKS and SSM Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/integrating_csi_driver.html) and [EKS and Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_csi_driver.html).

## Pre-requisites

Have the following tools installed:

- AWS CLI - [Installation](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
- EKS CTL - [Installation](https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html)
- Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
- Terraform CLI - [Installation](https://learn.hashicorp.com/tutorials/terraform/install-cli)
- Wget - [Installation](https://www.jcchouinard.com/wget/)

Make sure you have an active account at AWS for which you have configured the credentials on the system where you will execute the steps below. In this example we stored the credentials under an aws profile as `awsuser`.

## Installation (NOTE; WIP!!!)

The terraform code is loosely based on [This Fargate TF example](https://github.com/terraform-aws-modules/terraform-aws-eks/tree/master/examples/fargate).
Note: Applying the Terraform means you are creating cloud infrastructure which actually costs you money. The authors are not responsible for any cost coming from following the instructions below.
Note-II: The cluster you will create with this code has its access bound to your public IP-address.

1. export your AWS credentials (`export AWS_PROFILE=awsuser`)
2. check whether you have the right profile by doing `aws sts get-caller-identity` and make sure you have enough rights with the caller its identity and that the actual accountnumber displayed is the account designated for you to apply this TF to.
3. Do `terraform init` (if required, use tfenv to select TF 0.13.1 or higher )
4. Do `terraform plan`
5. Do `terraform apply`
6. When creation is done, do `aws eks update-kubeconfig --region eu-west-1 --name wrongsecrets-exercise-cluster --kubeconfig ~/.kube/wrongsecrets`
7. Do `export KUBECONFIG=~/.kube/wrongsecrets`

TODO: continue here with adding SM/SSM and actual deployment instructions, as for now: follow ../README.md its instructions on how to deploy the wrongsecrets application to AWS Fargate.

Your Fargate EKS cluster should be visilbe in [EU-West-1](https://eu-west-1.console.aws.amazon.com/eks/home?region=eu-west-1#/clusters).

Are you done playing? Please do `terraform destroy` again.

### Test it

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the instance profile of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secrets Manager easily? Which paths do you see?
4. Which of the 2 (SSM Parameter Store and Secrets Manager) works cross-account?
5. If you have applied the secrets to the cluster, you should see at the configuration details of the cluster that Secrets encryption is "Disabled", what does that mean?
