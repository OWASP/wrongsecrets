apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: azure-wrongsecrets-vault
spec:
  provider: azure
  parameters:
    usePodIdentity: "true"
    tenantId: ${AZURE_KEY_VAULT_TENANT_ID}
    objects: |
      array:
        - |
          objectName: wrongsecret
          objectAlias: WRONGSECRET
          objectType: secret
          objectVersion: ""
        - |
          objectName: wrongsecret-2
          objectAlias: WRONGSECRET_2
          objectType: secret
          objectVersion: ""
