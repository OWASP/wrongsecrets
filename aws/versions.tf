terraform {
  required_version = "~> 1.1"

  required_providers {
    aws = {
      version = "~> 4.1"
    }
    random = {
      version = "~> 3.0"
    }
    http = {
      version = "~> 2.1"
    }
  }
}
