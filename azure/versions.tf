terraform {
  required_version = "~> 1.1"

  required_providers {
    random = {
      version = "~> 3.0"
    }
    azurerm = {
      version = "~> 3.9"
    }
    http = {
      version = "~> 2.1"
    }
  }
}
