# Running OWASP WrongSecrets on UpCloud Kubernetes

This guide explains how to deploy **OWASP WrongSecrets** on an existing
UpCloud Managed Kubernetes cluster.

The setup uses standard Kubernetes manifests and does not rely on
provider-specific features, making it easy to adapt or extend.

---

## Prerequisites

- An active UpCloud account
- A running UpCloud Managed Kubernetes cluster
- `kubectl` configured to access the cluster
- Internet access to pull container images

---

## Notes

- The deployment uses the public Docker Hub image `owasp/wrongsecrets`.
- Image pulling depends on cluster network configuration.  
  If pods remain in `ImagePullBackOff`, verify that the cluster
  has outbound internet access and can pull images from Docker Hub.
  
---
## Deployment

Clone the WrongSecrets repository and navigate to the UpCloud folder:

```bash
git clone https://github.com/OWASP/wrongsecrets.git
cd wrongsecrets/upcloud
