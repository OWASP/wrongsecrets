
terraform {
  required_version = ">= 0.14.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.1.0"
    }
    local      = ">= 1.4"
    random     = ">= 2.1"
    kubernetes = ">= 1.11"
    shell = {
      source  = "scottwinkler/shell"
      version = "1.7.7"
    }
  }
}
