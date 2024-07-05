variable "region" {
  description = "The Azure region to use"
  type        = string
  default     = "East US"
}

variable "cluster_version" {
  description = "The AKS cluster version to use"
  type        = string
  default     = "1.30"
}

variable "cluster_name" {
  description = "The AKS cluster name"
  type        = string
  default     = "wrongsecrets-exercise-cluster"
}
