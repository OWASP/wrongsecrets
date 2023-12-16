output "key_vault_url" {
  value       = azurerm_key_vault.vault.vault_uri
  description = "Azure KeyVault URI for the Demo Container"
}

output "cluster_name" {
  value       = azurerm_kubernetes_cluster.cluster.name
  description = "AKS Cluster name"
}

output "oidc_issuer_url" {
  value       = azurerm_kubernetes_cluster.cluster.oidc_issuer_url
  description = "AKS Cluster OIDC Issuer URL"
}


output "app_client_id" {
  value = azuread_application.app.application_id
}

output "resource_group" {
  value       = azurerm_kubernetes_cluster.cluster.resource_group_name
  description = "Resource group name"
}

output "vault_uri" {
  value       = azurerm_key_vault.vault.vault_uri
  description = "Vault URI"
}

output "vault_name" {
  value       = azurerm_key_vault.vault.name
  description = "Vault name"
}

output "tenant_id" {
  value       = data.azurerm_client_config.current.tenant_id
  description = "Azure tenant ID"
}
