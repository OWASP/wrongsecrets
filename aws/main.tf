locals {
  vpc_cidr = "172.16.0.0/16"

  private_subnet_1_cidr = "172.16.1.0/24"
  private_subnet_2_cidr = "172.16.2.0/24"
  private_subnet_3_cidr = "172.16.3.0/24"

  public_subnet_1_cidr = "172.16.4.0/24"
  public_subnet_2_cidr = "172.16.5.0/24"
  public_subnet_3_cidr = "172.16.6.0/24"
}

provider "aws" {
  region = var.region
}

provider "random" {}

provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}

data "aws_availability_zones" "available" {}


module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 3.7.0"

  name                 = "${var.cluster_name}-vpc"
  cidr                 = local.vpc_cidr
  azs                  = data.aws_availability_zones.available.names
  private_subnets      = [local.private_subnet_1_cidr, local.private_subnet_2_cidr, local.private_subnet_3_cidr]
  public_subnets       = [local.public_subnet_1_cidr, local.public_subnet_2_cidr, local.public_subnet_3_cidr]
  enable_nat_gateway   = true
  single_nat_gateway   = true
  enable_dns_hostnames = true

  public_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/elb"                    = "1"
  }

  private_subnet_tags = {
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
    "kubernetes.io/role/internal-elb"           = "1"
  }
}


module "eks" {
  source = "terraform-aws-modules/eks/aws"

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id  = module.vpc.vpc_id
  subnets = module.vpc.private_subnets


  cluster_endpoint_private_access                = true
  cluster_create_endpoint_private_access_sg_rule = true
  cluster_endpoint_private_access_cidrs          = [local.private_subnet_1_cidr, local.private_subnet_2_cidr, local.private_subnet_3_cidr]

  cluster_endpoint_public_access_cidrs = ["${data.http.ip.body}/32"]

  node_groups_defaults = {
    ami_type  = "AL2_x86_64"
    disk_size = 50
  }

  node_groups = {
    managed = {
      create_launch_template = true

      desired_capacity = 1
      max_capacity     = 10
      min_capacity     = 1

      disk_size       = 50
      disk_type       = "gp3"
      disk_throughput = 150
      disk_iops       = 3000

      instance_types = ["t3a.large"]
      capacity_type  = "SPOT"

      additional_tags = {
        Environment = "test"
        Application = "wrongsecrets"
      }
      update_config = {
        max_unavailable_percentage = 25 # or set `max_unavailable`
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
