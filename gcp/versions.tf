terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.63.1"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 4.63.1"
    }
    random = {
      version = "~> 3.5.1"
    }
    http = {
      version = "~> 3.3.0"
    }
  }
}
