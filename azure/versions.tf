
terraform {
  required_version = ">= 0.14.0"

  required_providers {
    local      = ">= 1.4"
    random     = ">= 2.1"
    kubernetes = ">= 1.11"
    azurerm    = ">2.8.0"
  }
}