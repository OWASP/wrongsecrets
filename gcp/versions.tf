terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.39.1"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 5.39.1"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6.0"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.4.0"
    }
  }
}
