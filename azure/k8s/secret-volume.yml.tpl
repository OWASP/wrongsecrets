apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: azure-wrongsecrets-vault
spec:
  provider: azure
  parameters:
    usePodIdentity: "true" # [OPTIONAL] if not provided, will default to "false"
    tenantId: ${AZURE_KEY_VAULT_TENANT_ID} # the tenant ID of the KeyVault
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
