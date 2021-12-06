apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: azure-wrongsecrets-vault
spec:
  provider: azure
  parameters:
    usePodIdentity: "true" # [OPTIONAL] if not provided, will default to "false"
    userAssignedIdentityID: "client_id" # [OPTIONAL available for version > 0.0.4] use the client id to specify which user assigned managed identity to use. If using a user assigned identity as the VM's managed identity, then specify the identity's client id. If empty, then defaults to use the system assigned identity on the VM
    keyvaultName: "owasp-wrongsecrets-vault" # the name of the KeyVault
    cloudEnvFileName: "" # [OPTIONAL available for version > 0.0.7] use to define path to file for populating azure environment
    objects: |
      array:
        - |
          objectName: wrongsecret
          objectAlias: WRONGSECRET           # [OPTIONAL available for version > 0.0.4] object alias
          objectType: secret              # object types: secret, key or cert. For Key Vault certificates, refer to https://azure.github.io/secrets-store-csi-driver-provider-azure/configurations/getting-certs-and-keys/ for the object type to use
          objectVersion: ""               # [OPTIONAL] object versions, default to latest if empty
        - |
          objectName: wrongsecret-2
          objectAlias: WRONGSECRET_2           # [OPTIONAL available for version > 0.0.4] object alias
          objectType: secret              # object types: secret, key or cert. For Key Vault certificates, refer to https://azure.github.io/secrets-store-csi-driver-provider-azure/configurations/getting-certs-and-keys/ for the object type to use
          objectVersion: ""               # [OPTIONAL] object versions, default to latest if empty
    tenantId: ${AZURE_KEY_VAULT_TENANT_ID} # the tenant ID of the KeyVault
