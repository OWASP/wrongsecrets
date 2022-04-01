terraform {
  required_providers {
    aws = {
      version = "~> 4.0"
    }
  }
}

resource "aws_s3_bucket" "state" {}

output "s3_bucket_name" {
  description = "Name of the terraform state bucket"
  value       = aws_s3_bucket.state.id
}
