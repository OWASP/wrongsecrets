output "bucket_name" {
  description = "Name of the publicly readable backup bucket"
  value       = aws_s3_bucket.backup.id
}

output "backup_key_object_arn" {
  description = "ARN of the object which holds the ed25519 private key"
  value       = "${aws_s3_bucket.backup.arn}/${var.object_key}"
}

output "backup_key_object_url" {
  description = "Unauthenticated HTTPS URL which serves the ed25519 private key"
  value       = "https://${aws_s3_bucket.backup.bucket}.s3.${var.region}.amazonaws.com/${var.object_key}"
}
