variable "region" {
  description = "The GCP region to use"
  type        = string
  default     = "eu-west4"
}

variable "project_id" {
  description = "project id"
  type        = string
}

variable "cluster_version" {
  description = "The GKE cluster version to use"
  type        = string
  default     = "1.25"
}

variable "cluster_name" {
  description = "The GKE cluster name"
  type        = string
  default     = "wrongsecrets-exercise-cluster"
}
