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
  version = "~> 6.4.0"

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
  version = "21.4.0"

  name               = var.cluster_name
  kubernetes_version = var.cluster_version

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets


  endpoint_private_access = true
  endpoint_public_access  = true

  endpoint_public_access_cidrs = ["${data.http.ip.response_body}/32"]

  #create_auto_mode_iam_resources = true

  enable_cluster_creator_admin_permissions = true

  upgrade_policy = {
    support_type = "STANDARD"
  }

  compute_config = {
    enabled    = true
    node_pools = ["general-purpose", "system"]
  }
  tags = var.tags
}
