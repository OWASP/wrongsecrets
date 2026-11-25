variable "region" {
  description = "The AWS region to use"
  type        = string
  default     = "eu-west-1"
}

variable "bucket_name" {
  description = "Name of the S3 backup bucket which holds the ed25519 private key of challenge 78"
  type        = string
  default     = "backupchallenge"
}

variable "bucket_suffix" {
  description = "Optional suffix appended to the bucket name, needed when the plain bucket name is already taken as S3 bucket names are globally unique"
  type        = string
  default     = ""
}

variable "object_key" {
  description = "Key of the object in the backup bucket which holds the ed25519 private key"
  type        = string
  default     = "backups/id_ed25519"
}

variable "tags" {
  description = "Tags to apply to the resources"
  type        = map(string)
  default = {
    "Application" = "wrongsecrets"
  }
}
