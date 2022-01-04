#########################
# Key vault challenge 1 #
#########################

resource "azurerm_key_vault" "vault" {
  name                = "owasp-wrongsecrets-vault"
  location            = data.azurerm_resource_group.default.location
  resource_group_name = data.azurerm_resource_group.default.name
  tenant_id           = data.azurerm_client_config.current.tenant_id
  sku_name            = "standard"
}

# Needed for user permissions
resource "azurerm_key_vault_access_policy" "user" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azurerm_client_config.current.object_id

  secret_permissions = [
    "get", "list", "set", "delete"
  ]
}

resource "azurerm_key_vault_access_policy" "identity_access" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_user_assigned_identity.aks_pod_identity.principal_id

  secret_permissions = [
    "get", "list"
  ]
}

resource "random_password" "password" {
  length           = 24
  special          = true
  override_special = "_%@"
}

resource "azurerm_key_vault_secret" "wrongsecret_1" {
  name         = "wrongsecret"
  value        = random_password.password.result
  key_vault_id = azurerm_key_vault.vault.id
}

#########################
# Key vault challenge 2 #
#########################

resource "azurerm_key_vault_secret" "wrongsecret_2" {
  name         = "wrongsecret-2"
  value        = "Hello from Terraform" # bootstrap something, not used in challenge
  key_vault_id = azurerm_key_vault.vault.id

  lifecycle {
    ignore_changes = [
      value
    ]
  }
}

#########################
# Key vault challenge 3 #
#########################

resource "azurerm_key_vault_secret" "wrongsecret_3" {
  name         = "wrongsecret-3"
  value        = "Hello from Terraform 2" # bootstrap something, not used in challenge
  key_vault_id = azurerm_key_vault.vault.id

  lifecycle {
    ignore_changes = [
      value
    ]
  }
}

resource "azurerm_key_vault_access_policy" "extra_identity_access" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_user_assigned_identity.aks_extra_pod_identity.principal_id

  secret_permissions = [
    "get", "list"
  ]
}
