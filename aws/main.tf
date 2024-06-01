terraform {
  # Set your region and bucket name (output from shared state) in the placeholder below
  # Then uncomment and apply!
  # backend "s3" {
  #   region = "eu-west-1" # Change if desired
  #   bucket = ""
  #   key    = "wrongsecrets/terraform.tfstate"
  # }
}

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
  default_tags {
    tags = var.tags
  }
}

provider "random" {}

provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}

data "aws_availability_zones" "available" {}


module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.8.1"

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
  source  = "terraform-aws-modules/eks/aws"
  version = "20.13.0"

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets


  cluster_endpoint_private_access = true
  cluster_endpoint_public_access  = true

  cluster_endpoint_public_access_cidrs = ["${data.http.ip.response_body}/32"]

  enable_irsa = true

  enable_cluster_creator_admin_permissions = true

  eks_managed_node_group_defaults = {
    disk_size       = 50
    disk_type       = "gp3"
    disk_throughput = 150
    disk_iops       = 3000
    instance_types  = ["t3a.large"]

    iam_role_additional_policies = {
      AmazonEKSWorkerNodePolicy : "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy",
      AmazonEKS_CNI_Policy : "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy",
      AmazonEC2ContainerRegistryReadOnly : "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly",
      AmazonSSMManagedInstanceCore : "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore",
      AmazonEKSVPCResourceController : "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController",
      AmazonEBSCSIDriverPolicy : "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy"
    }
  }

  eks_managed_node_groups = {
    bottlerocket_default = {
      use_custom_launch_template = false
      min_size                   = 1
      max_size                   = 3
      desired_size               = 1
      capacity_type              = "SPOT"

      ami_type = "BOTTLEROCKET_x86_64"
      platform = "bottlerocket"
    }
  }

  node_security_group_additional_rules = {
    aws_lb_controller_webhook = {
      description                   = "Cluster API to AWS LB Controller webhook"
      protocol                      = "all"
      from_port                     = 9443
      to_port                       = 9443
      type                          = "ingress"
      source_cluster_security_group = true
    }
  }

  tags = {
    Environment = "test"
    Application = "wrongsecrets"
  }
}
