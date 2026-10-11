provider "aws" {
  region = var.region

  default_tags {
    tags = var.tags
  }
}

locals {
  bucket_name = var.bucket_suffix == "" ? var.bucket_name : "${var.bucket_name}-${var.bucket_suffix}"
}

resource "aws_s3_bucket" "backup" {
  bucket        = local.bucket_name
  force_destroy = true
}

resource "aws_s3_bucket_ownership_controls" "backup" {
  bucket = aws_s3_bucket.backup.id

  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "backup" {
  bucket = aws_s3_bucket.backup.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "backup" {
  bucket                  = aws_s3_bucket.backup.id
  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "public_read_backup_key" {
  bucket = aws_s3_bucket.backup.id

  depends_on = [aws_s3_bucket_public_access_block.backup]

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowAnonymousReadOfBackupKey"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.backup.arn}/${var.object_key}"
      }
    ]
  })
}
