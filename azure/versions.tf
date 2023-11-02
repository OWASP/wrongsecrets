terraform {
  required_version = "~> 1.1"

  required_providers {
    random = {
      source  = "hashicorp/random"
      version = "~> 3.5.1"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.78.0"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.4.0"
    }
  }
}
