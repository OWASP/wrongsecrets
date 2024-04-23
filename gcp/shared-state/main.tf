terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.1"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
}


provider "google" {
  project = var.project_id
  region  = var.region
}

resource "random_id" "suffix" {
  byte_length = 4
}

resource "google_storage_bucket" "state_bucket" {
  name     = "tfstate-wrongsecrets-${random_id.suffix.hex}"
  location = var.region

  versioning {
    enabled = true
  }
}
