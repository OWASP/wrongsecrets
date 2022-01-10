apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: azure-wrongsecrets-vault
spec:
  provider: azure
  parameters:
    usePodIdentity: "true"
    tenantId: ${AZ_KEY_VAULT_TENANT_ID}
    keyvaultName: ${AZ_KEY_VAULT_NAME}
    objects: |
      array:
        - |
          objectName: wrongsecret
          objectAlias: wrongsecret
          objectType: secret
          objectVersion: ""
        - |
          objectName: wrongsecret-2
          objectAlias: wrongsecret-2
          objectType: secret
          objectVersion: ""
