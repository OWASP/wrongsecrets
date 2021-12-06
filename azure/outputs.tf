output "key_vault_url" {
  value       = azurerm_key_vault.vault.vault_uri
  description = "Azure KeyVault URI for the Demo Container"
}

output "cluster_name" {
  value = azurerm_kubernetes_cluster.cluster.name
}