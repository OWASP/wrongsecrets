#!/usr/bin/env sh

# terraform
curl -sqLo /tmp/terraform.zip "https://releases.hashicorp.com/terraform/1.4.6/terraform_1.4.6_linux_$(dpkg --print-architecture).zip"
unzip /tmp/terraform.zip -d /tmp/
/tmp/terraform init -backend=false
/tmp/terraform providers lock -platform=linux_amd64 -platform=linux_arm64 -platform=darwin_amd64 -platform=darwin_arm64

# terraform-docs
curl -sqLo /tmp/terraform-docs.tar.gz "https://terraform-docs.io/dl/v0.16.0/terraform-docs-v0.16.0-$(uname)-$(dpkg --print-architecture).tar.gz"
tar -xzf /tmp/terraform-docs.tar.gz -C /tmp/
chmod +x /tmp/terraform-docs
/tmp/terraform-docs .
