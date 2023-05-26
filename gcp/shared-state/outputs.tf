output "bucket" {
  value       = google_storage_bucket.state-bucket.name
  description = "Terraform backend storage bucket"
}
