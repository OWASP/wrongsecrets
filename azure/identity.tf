############################################################################################################################
## Here we need to create an Azure AD Application + a Service Principal and federate the application with the OIDC Issuer ##
## so that Azure AD can exchange a token issued to the pod with a token that can be used to access other Azure resources. ##
############################################################################################################################


locals {
  namespace_name = "default"
  ## This should match the name of the service account created by helm chart
  service_account_name = "wrongsecrets-sa"
}

## Azure AD application that represents the app
resource "azuread_application" "app" {
  display_name = "sp-wrongsecrets"
}

resource "azuread_service_principal" "app" {
  client_id                    = azuread_application.app.client_id
  app_role_assignment_required = false
}

resource "azuread_service_principal_password" "app" {
  service_principal_id = azuread_service_principal.app.id
}

## Azure AD federated identity used to federate kubernetes with Azure AD
resource "azuread_application_federated_identity_credential" "app" {
  application_id = azuread_application.app.application_id
  display_name   = "fed-identity-app-wrongsecrets"
  description    = "The federated identity used to federate K8s with Azure AD with the app service running in k8s wrongsecrets"
  audiences      = ["api://AzureADTokenExchange"]
  issuer         = azurerm_kubernetes_cluster.cluster.oidc_issuer_url
  subject        = "system:serviceaccount:${local.namespace_name}:${local.service_account_name}"
}
