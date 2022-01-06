output "key_vault_url" {
  value       = azurerm_key_vault.vault.vault_uri
  description = "Azure KeyVault URI for the Demo Container"
}

output "cluster_name" {
  value       = azurerm_kubernetes_cluster.cluster.name
  description = "AKS Cluster name"
}

output "resource_group" {
  value       = azurerm_kubernetes_cluster.cluster.resource_group_name
  description = "Resource group name"
}

output "aad_pod_identity_resource_id" {
  value       = azurerm_user_assigned_identity.aks_pod_identity.id
  description = "Resource ID for the Managed Identity for AAD Pod Identity"
}

output "aad_pod_identity_client_id" {
  value       = azurerm_user_assigned_identity.aks_pod_identity.client_id
  description = "Client ID for the Managed Identity for AAD Pod Identity"
}

output "aad_extra_pod_identity_resource_id" {
  value       = azurerm_user_assigned_identity.aks_extra_pod_identity.id
  description = "Resource ID for the Managed Identity for AAD Pod Identity"
}

output "aad_extra_pod_identity_client_id" {
  value       = azurerm_user_assigned_identity.aks_extra_pod_identity.client_id
  description = "Client ID for the Managed Identity for AAD Pod Identity"
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
