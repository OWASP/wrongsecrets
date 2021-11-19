apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: wrongsecrets-gcp-secretsmanager
spec:
  provider: gcp
  parameters:
    objects: |
      - resourceName: "projects/${PROJECT_ID}/secrets/wrongsecret-1/versions/latest"
        fileName: "wrongsecret"
      - resourceName: "projects/${PROJECT_ID}/secrets/wrongsecret-2/versions/latest"
        fileName: "wrongsecret-2"
