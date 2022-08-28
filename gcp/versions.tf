terraform {
  required_version = ">= 1.0.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.1.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = ">= 4.1.0"
    }
    local      = ">= 1.4"
    random     = ">= 2.1"
    kubernetes = ">= 1.11"
  }

  # For shared state:
  # Set the resource group in the backend configuration below, then uncomment and apply!
  # Note that you probably already create a resource group. Don't forget to set that correctly in this file.
  #  backend "gcs" {
  #    bucket  = ""
  #    prefix  = "terraform/state"
  #  }
}
