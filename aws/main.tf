locals {
  cluster_version = "1.21"
}

provider "aws" {
  region = var.region
}

provider "random" {}

resource "random_pet" "cluster" {
  length    = 4
  separator = "-"
}

resource "aws_default_vpc" "default" {
  tags = {
    Name = "Default VPC"
  }
}

resource "aws_default_subnet" "default_az1" {
  availability_zone = "${var.region}a"
}

module "eks" {
  source = "terraform-aws-modules/eks/aws"

  cluster_name    = random_pet.cluster.id
  cluster_version = "1.21"

  vpc_id          = aws_default_vpc.default.id
  fargate_subnets = [aws_default_subnet.default_az1.id]

  fargate_profiles = {
    default = {
      name = "default"
      selectors = [
        {
          namespace = "kube-system"
          labels = {
            k8s-app = "kube-dns"
          }
        },
        {
          namespace = "default"
          labels = {
            WorkerType = "fargate"
          }
        }
      ]

      tags = {
        Owner = "default"
      }
    }
  }

  manage_aws_auth = true

  tags = {
    Environment = "test"
    Application = "wrongsecrets"
  }
}



#############
# Kubernetes
#############

data "aws_eks_cluster" "cluster" {
  name = module.eks.cluster_id
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.eks.cluster_id
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}
