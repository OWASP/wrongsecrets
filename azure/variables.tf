variable "region" {
  description = "The Azure region to use"
  type        = string
  default     = "West Europe"
}

variable "cluster_version" {
  description = "The AKS cluster version to use"
  type        = string
  default     = "1.20"
}

variable "cluster_name" {
  description = "The AKS cluster name"
  type        = string
  default     = "wrongsecrets-exercise-cluster"
}
