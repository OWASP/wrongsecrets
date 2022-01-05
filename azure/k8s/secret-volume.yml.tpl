apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
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
          objectAlias: WRONGSECRET
          objectType: secret
          objectVersion: ""
        - |
          objectName: wrongsecret-2
          objectAlias: WRONGSECRET_2
          objectType: secret
          objectVersion: ""
