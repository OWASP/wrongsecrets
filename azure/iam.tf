resource "azurerm_user_assigned_identity" "aks_pod_identity" {
  resource_group_name = azurerm_resource_group.default.name
  location            = azurerm_resource_group.default.location
  name                = "wrongsecrets-identity"
}

resource "azurerm_user_assigned_identity" "aks_extra_pod_identity" {
  resource_group_name = azurerm_resource_group.default.name
  location            = azurerm_resource_group.default.location
  name                = "wrongsecrets-extra-identity"
}

# Role assignments
# Details: https://github.com/Azure/aad-pod-identity/blob/master/website/content/en/docs/Getting%20started/role-assignment.md
resource "azurerm_role_assignment" "aks_identity_operator" {
  scope                = azurerm_user_assigned_identity.aks_pod_identity.id
  role_definition_name = "Managed Identity Operator"
  principal_id         = azurerm_kubernetes_cluster.cluster.kubelet_identity[0].object_id
}

resource "azurerm_role_assignment" "aks_extra_identity_operator" {
  scope                = azurerm_user_assigned_identity.aks_extra_pod_identity.id
  role_definition_name = "Managed Identity Operator"
  principal_id         = azurerm_kubernetes_cluster.cluster.kubelet_identity[0].object_id
}

resource "azurerm_role_assignment" "aks_vm_contributor" {
  scope                = "/subscriptions/${data.azurerm_client_config.current.subscription_id}/resourcegroups/${azurerm_kubernetes_cluster.cluster.node_resource_group}"
  role_definition_name = "Virtual Machine Contributor"
  principal_id         = azurerm_kubernetes_cluster.cluster.kubelet_identity[0].object_id
}
