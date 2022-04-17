provider "google" {
    project = var.project_id
    region  = var.region
}

resource "random_id" "suffix" {
    byte_length = 4
}

resource "google_storage_bucket" "state-bucket" {
    name     = "tfstate-wrongsecrets-${random_id.suffix.hex}"
    location = var.region

    versioning {
        enabled = true
    }
}

output "bucket" {
    value = google_storage_bucket.state-bucket.name
    description = "Terraform backend storage bucket"
}
