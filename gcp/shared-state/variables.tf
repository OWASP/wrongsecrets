variable "project_id" {
  description = "The GCP project id to use"
  type        = string
  default     = "owasp-wrongsecrets"
}

variable "region" {
  description = "The GCP region to use"
  type        = string
  default     = "europe-west4"
}
