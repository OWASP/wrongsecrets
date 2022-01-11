provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}

provider "azurerm" {
  features {}

  skip_provider_registration = true
}

data "azurerm_client_config" "current" {}

# If you're using an existing resource group, modify this part
data "azurerm_resource_group" "default" {
  name = "OWASP-Projects"
}

# If you're creating a new resource group, modify this. Note that you'll need to find/replace references to "data.azurerm_resource_group.default" to "arurerm_resource_group.default"
# resource "azurerm_resource_group" "default" {
# name     = "owasp-wrongsecrets"
# location = var.region
# }


resource "azurerm_kubernetes_cluster" "cluster" {
  name                = var.cluster_name
  location            = data.azurerm_resource_group.default.location
  resource_group_name = data.azurerm_resource_group.default.name
  dns_prefix          = "wrongsecrets"

  kubernetes_version = var.cluster_version

  api_server_authorized_ip_ranges = ["${data.http.ip.body}/32"]

  network_profile {
    network_plugin = "azure"
  }

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_A2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  role_based_access_control {
    enabled = true
  }
}
