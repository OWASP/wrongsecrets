# Setup your secrets in AWS

In this setup we integrate the secrets online on AWS.

## Pre-requisites

Have the following tos installed:

- AWS CLI (`ip3 install awscliv2`)
- EKS CTL (`https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html`)

Make sure you have an active account at AWS for which you have configured the credentials on the system where you will execute the steps below. In this example we stored the credentials under an aws profile as `awsuser`.

## Installation

### Install the EKS cluster on your account

### Setup integration with SSM


### Setup integration with Secretsmanager


### Deploy materials to the cluster


### Test it

### A few things to consider

1. Does your worker node now have access as well?
2. Can you easily obtain the instance profile of the Node?
3. Can you get the secrets in the SSM Parameter Store and Secrets Manager easily? Which paths do you see?
4. Which of the 2 (SSM Parameter Store and Secrets Manager) works cross-account?