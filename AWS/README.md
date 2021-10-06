# Setup your secrets in AWS

In this setup we integrate the secrets-exercise online at AWS Fargate and let Pods consume secrets from the AWS Parameter Store and AWS Secrets Manager.
Why Fargate? We on't want the hassle of managing the EC2 instances ourselves. If you want to know more about integrating them with EKS, check [EKS and SSM Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/integrating_csi_driver.html) and [EKS and Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_csi_driver.html).

## Pre-requisites

Have the following tos installed:

- AWS CLI - [Installation](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
- EKS CTL - [Installation](https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html)
- Tfenv (Optional) - [Installation](https://github.com/tfutils/tfenv)
- Terraform CLI - [Installation(https://learn.hashicorp.com/tutorials/terraform/install-cli)

Make sure you have an active account at AWS for which you have configured the credentials on the system where you will execute the steps below. In this example we stored the credentials under an aws profile as `awsuser`.

## Installation

TODO ADD STUFF HERE


### Test it

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the instance profile of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secrets Manager easily? Which paths do you see?
4. Which of the 2 (SSM Parameter Store and Secrets Manager) works cross-account?