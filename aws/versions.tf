terraform {
  required_version = "~> 1.6, >= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.34"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.7.0"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.5.0"
    }
  }
}
