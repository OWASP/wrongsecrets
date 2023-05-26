apiVersion: "aadpodidentity.k8s.io/v1"
kind: AzureIdentity
metadata:
  name: wrongsecrets-pod-id
spec:
  type: 0
  resourceID: ${AZ_POD_RESOURCE_ID}
  clientID: ${AZ_POD_CLIENT_ID}
---
apiVersion: "aadpodidentity.k8s.io/v1"
kind: AzureIdentityBinding
metadata:
  name: wrongsecrets-podid-binding
spec:
  azureIdentity: wrongsecrets-pod-id
  selector: wrongsecrets-pod-id
---
apiVersion: "aadpodidentity.k8s.io/v1"
kind: AzureIdentity
metadata:
  name: separate-workload-pod-id
spec:
  type: 0
  resourceID: ${AZ_EXTRA_POD_RESOURCE_ID}
  clientID: ${AZ_EXTRA_POD_CLIENT_ID}
---
apiVersion: "aadpodidentity.k8s.io/v1"
kind: AzureIdentityBinding
metadata:
  name: wrongsecrets-extra-podid-binding
spec:
  azureIdentity: separate-workload-pod-id
  selector: separate-workload-pod-id
