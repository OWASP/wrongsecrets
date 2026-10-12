terraform {
  required_version = "~> 1.6, >= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.34"
    }
  }
}
