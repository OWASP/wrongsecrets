locals {
  cluster_version = "1.21"
  subnet_cidr     = "172.31.100.0/24"
}

provider "aws" {
  region = var.region
}

provider "random" {}

provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}

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

resource "aws_default_subnet" "default_az2" {
  availability_zone = "${var.region}b"
}

resource "aws_subnet" "private" {
  availability_zone               = "${var.region}a"
  map_public_ip_on_launch         = false
  assign_ipv6_address_on_creation = false
  cidr_block                      = "172.31.100.0/24"
  vpc_id                          = aws_default_vpc.default.id
}

resource "aws_route_table" "table" {
  vpc_id = aws_default_vpc.default.id

  route = []
}

resource "aws_route_table_association" "association" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.table.id
}

module "eks" {
  source = "terraform-aws-modules/eks/aws"

  cluster_name    = "wrongsecrets-exercise-cluster"
  cluster_version = "1.21"

  vpc_id          = aws_default_vpc.default.id
  subnets         = [aws_default_subnet.default_az1.id, aws_default_subnet.default_az2.id]
  fargate_subnets = [aws_subnet.private.id]

  
  cluster_endpoint_private_access                = true
  cluster_create_endpoint_private_access_sg_rule = true
  cluster_endpoint_private_access_cidrs          = [local.subnet_cidr]

  cluster_endpoint_public_access_cidrs = ["${data.http.ip.body}/32"]

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

  depends_on = [
    aws_route_table_association.association,
  ]
}

######
# PAtching as per https://github.com/terraform-aws-modules/terraform-aws-eks/issues/1286#issuecomment-811157662
######
# Provider configuration
provider "shell" {
  interpreter = ["/bin/bash", "-c"]
  sensitive_environment = {
    KUBECTL_CONFIG = base64encode(module.eks.kubeconfig)
  }
}

# Configures coredns to run on Fargate.
# Per default coredns runs with EC2. 
# The Terraform eks module does not offer any inputs to set the compute type of coredns to Fargate.
# See: https://github.com/terraform-aws-modules/terraform-aws-eks/issues/1286
# Therefore, we are using the kubectl to patch coredns using the Kubernetes API.
resource "shell_script" "coredns_fargate_patch" {
  lifecycle_commands {
    create = file("${path.module}/scripts/patch_coredns_for_fargate.sh")
    delete = file("${path.module}/scripts/patch_coredns_for_ec2.sh")
  }

  # Wait for the EKS module to get provisioned completely including the kube-system Fargate profile.
  depends_on = [module.eks]
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
