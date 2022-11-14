apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: wrongsecrets-gcp-secretsmanager
spec:
  provider: gcp
  parameters:
    secrets: |
      - resourceName: "projects/${spring.cloud.gcp.project-id}/secrets/wrongsecret-1/versions/latest"
        fileName: "wrongsecret"
      - resourceName: "projects/${spring.cloud.gcp.project-id}/secrets/wrongsecret-2/versions/latest"
        fileName: "wrongsecret-2"
