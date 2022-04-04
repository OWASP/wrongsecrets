terraform {
  required_providers {
    aws = {
      version = "~> 4.0"
    }
  }
}

variable "region" {
  description = "The AWS region to use"
  type        = string
  default     = "eu-west-1"
}

provider "aws" {
  region = var.region
}

resource "aws_s3_bucket" "state" {}

resource "aws_s3_bucket_server_side_encryption_configuration" "encryption" {
  bucket = aws_s3_bucket.state.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

output "s3_bucket_name" {
  description = "Name of the terraform state bucket"
  value       = aws_s3_bucket.state.id
}
