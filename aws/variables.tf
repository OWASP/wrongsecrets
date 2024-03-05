variable "region" {
  description = "The AWS region to use"
  type        = string
  default     = "eu-west-1"
}

variable "cluster_version" {
  description = "The EKS cluster version to use"
  type        = string
  default     = "1.29"
}

variable "cluster_name" {
  description = "The EKS cluster name"
  type        = string
  default     = "wrongsecrets-exercise-cluster"
}

variable "tags" {
  description = "List of tags to apply to resources"
  type        = map(string)
  default = {
    "Application" = "wrongsecrets"
  }
}
