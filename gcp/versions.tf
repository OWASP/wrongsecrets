terraform {
  required_version = "~> 1.1"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.42.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 4.39.0"
    }
    random = {
      version = "~> 3.4.3"
    }
    http = {
      version = "~> 3.1.0"
    }
  }
}
