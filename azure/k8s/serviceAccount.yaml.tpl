apiVersion: v1
kind: ServiceAccount
metadata:
  name: wrongsecrets-sa
  labels:
    azure.workload.identity/use: "true" # Represents the service account is to be used for workload identity, see https://azure.github.io/azure-workload-identity/docs/topics/service-account-labels-and-annotations.html
  annotations:
    azure.workload.identity/client-id: ${AZ_AD_APP_CLIENT_ID}
    azure.workload.identity/tenant-id: ${AZURE_TENANT_ID}
    azure.workload.identity/service-account-token-expiration: "86400" # Token is valid for 1 day
