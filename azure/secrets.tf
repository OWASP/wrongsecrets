#########################
# Key vault challenge 1 #
#########################

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

resource "azurerm_key_vault" "vault" {
  name                = "wrongsecrets-${random_string.suffix.result}-${random_integer.suffix.result}"
  location            = azurerm_resource_group.default.location
  resource_group_name = azurerm_resource_group.default.name
  tenant_id           = data.azurerm_client_config.current.tenant_id
  sku_name            = "standard"

  soft_delete_retention_days = 7
  purge_protection_enabled   = false
}

# Needed for user permissions
resource "azurerm_key_vault_access_policy" "user" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azurerm_client_config.current.object_id

  secret_permissions = [
    "Get", "List", "Set", "Delete"
  ]
}

resource "azurerm_key_vault_access_policy" "identity_access" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_user_assigned_identity.aks_pod_identity.principal_id

  secret_permissions = [
    "Get", "List"
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

  depends_on = [
    azurerm_key_vault_access_policy.user
  ]
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

  depends_on = [
    azurerm_key_vault_access_policy.user
  ]
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

  depends_on = [
    azurerm_key_vault_access_policy.user
  ]
}

# With Azure key vault, you grant access per vault instead of per secret. Below is a bad idea if these workloads should
# be separated
resource "azurerm_key_vault_access_policy" "extra_identity_access" {
  key_vault_id = azurerm_key_vault.vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azurerm_user_assigned_identity.aks_extra_pod_identity.principal_id

  secret_permissions = [
    "Get", "List"
  ]
}
