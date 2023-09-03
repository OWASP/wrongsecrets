terraform {
  required_version = "~> 1.1"

  required_providers {
    aws = {
      version = "~> 5.15.0"
    }
    random = {
      version = "~> 3.5.1"
    }
    http = {
      version = "~> 3.4.0"
    }
  }
}
