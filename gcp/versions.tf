terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.25.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 6.25.0"
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
