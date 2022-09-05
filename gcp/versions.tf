terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.1"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 4.1"
    }
    random = {
      version = "~> 3.0"
    }
    kubernetes = {
      version = "~> 1.11"
    }
    http = {
      version = "~> 2.1"
    }
  }
}
