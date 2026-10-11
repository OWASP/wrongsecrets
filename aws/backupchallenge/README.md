# Backup challenge bucket (challenge 78)

This folder provisions the S3 bucket for challenge 78 of WrongSecrets: a "backup" bucket that is readable by anybody and that holds a dedicated, non-production ed25519 private key.

The public exposure is kept to the minimum the challenge needs:

-   anonymous `s3:GetObject` on the single key object only,
-   no public `s3:ListBucket`, no public write and no public delete,
-   ACLs are disabled through `BucketOwnerEnforced`, while the public policy is attached through `aws_s3_bucket_public_access_block` with `block_public_acls` and `ignore_public_acls` kept enabled.

## Pre-requisites

-   Terraform CLI - [Installation](https://developer.hashicorp.com/terraform/install)
-   AWS CLI - [Installation](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html), configured with credentials that may create S3 buckets.

## Usage

1.  Create the bucket:

```bash
cd aws/backupchallenge
terraform init
terraform apply
```

S3 bucket names are globally unique, while this setup requests the name `backupchallenge`. If that name is already taken in S3, add a suffix (for example your AWS account id):

```bash
terraform apply -var="bucket_suffix=$(aws sts get-caller-identity --query Account --output text)"
```

2.  Upload the fixture key with the AWS CLI:

```bash
./upload-backup-key.sh
./upload-backup-key.sh backupchallenge-123456789012
```

The script uploads `ed25519_backup_key`, a dedicated non-production key which only exists for this exercise, to `s3://<bucket>/backups/id_ed25519`. It never grants write or delete access to anybody.

3.  Let the application validate against the very same key. The challenge reads it from the `challenge78_backup_key` property, so set that property (for instance through the `CHALLENGE78_BACKUP_KEY` environment variable or a mounted secret) to the contents of `ed25519_backup_key`. The value must never be hardcoded in the java sources.

Please note that the target AWS account must not enforce the account level Block Public Access setting for public bucket policies, otherwise Terraform will refuse to attach the read-only policy.

## Cleanup

```bash
terraform destroy
```

<!-- BEGIN_TF_DOCS -->
## Resources

| Name | Type |
|------|------|
| [aws_s3_bucket.backup](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) | resource |
| [aws_s3_bucket_ownership_controls.backup](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_ownership_controls) | resource |
| [aws_s3_bucket_policy.public_read_backup_key](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_policy) | resource |
| [aws_s3_bucket_public_access_block.backup](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_public_access_block) | resource |
| [aws_s3_bucket_server_side_encryption_configuration.backup](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_server_side_encryption_configuration) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_bucket_name"></a> [bucket\_name](#input\_bucket\_name) | Name of the S3 backup bucket which holds the ed25519 private key of challenge 78 | `string` | `"backupchallenge"` | no |
| <a name="input_bucket_suffix"></a> [bucket\_suffix](#input\_bucket\_suffix) | Optional suffix appended to the bucket name, needed when the plain bucket name is already taken as S3 bucket names are globally unique | `string` | `""` | no |
| <a name="input_object_key"></a> [object\_key](#input\_object\_key) | Key of the object in the backup bucket which holds the ed25519 private key | `string` | `"backups/id_ed25519"` | no |
| <a name="input_region"></a> [region](#input\_region) | The AWS region to use | `string` | `"eu-west-1"` | no |
| <a name="input_tags"></a> [tags](#input\_tags) | Tags to apply to the resources | `map(string)` | <pre>{<br/>  "Application": "wrongsecrets"<br/>}</pre> | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_backup_key_object_arn"></a> [backup\_key\_object\_arn](#output\_backup\_key\_object\_arn) | ARN of the object which holds the ed25519 private key |
| <a name="output_backup_key_object_url"></a> [backup\_key\_object\_url](#output\_backup\_key\_object\_url) | Unauthenticated HTTPS URL which serves the ed25519 private key |
| <a name="output_bucket_name"></a> [bucket\_name](#output\_bucket\_name) | Name of the publicly readable backup bucket |
<!-- END_TF_DOCS -->
