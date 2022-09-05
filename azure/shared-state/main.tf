terraform {
  required_version = "~> 1.1"
  required_providers {
    azurerm = {
      version = "~> 3.0"
    }
    random = {
      version = "~> 3.0"
    }
  }
}

provider "azurerm" {
  features {}
  skip_provider_registration = true
}

# If you're using an existing resource group, modify this part. That'll definitely be the case if you're using shared state!
# Note that you'll need to find/replace references to "arurerm_resource_group.default" to "data.azurerm_resource_group.default"
#data "azurerm_resource_group" "default" {
#  name = "owasp-wrongsecrets"
#}

# If you're creating a new resource group, modify this.
resource "azurerm_resource_group" "default" {
  name     = "owasp-wrongsecrets"
  location = var.region
}


resource "random_integer" "suffix" {
  min = 00000
  max = 99999
}

resource "random_string" "suffix" {
  length  = 5
  special = false
  upper   = false
  numeric = true
}


resource "azurerm_storage_account" "account" {
  name                     = format("wrongsecrets%s%s", random_string.suffix.result, random_integer.suffix.result)
  resource_group_name      = azurerm_resource_group.default.name
  location                 = azurerm_resource_group.default.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}



resource "azurerm_storage_container" "blob" {
  name                  = "tfstate"
  storage_account_name  = azurerm_storage_account.account.name
  container_access_type = "private"
}
