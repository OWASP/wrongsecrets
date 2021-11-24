###############################
# Secrets manager challenge 1 #
###############################

resource "aws_secretsmanager_secret" "secret" {
  name                    = "wrongsecret"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "secret" {
  secret_id     = aws_secretsmanager_secret.secret.id
  secret_string = random_password.password.result
}

resource "random_password" "password" {
  length           = 24
  special          = true
  override_special = "_%@"
}

resource "aws_secretsmanager_secret_policy" "policy" {
  block_public_policy = true
  secret_arn          = aws_secretsmanager_secret.secret.arn

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "EnableAllPermissions",
      "Effect": "Allow",
      "Principal": {
        "AWS": "${aws_iam_role.irsa_role.arn}"
      },
      "Action": "secretsmanager:*",
      "Resource": "*"
    }
  ]
}
POLICY

}


###############################
# Secrets manager challenge 2 #
###############################

resource "aws_secretsmanager_secret" "secret_2" {
  name                    = "wrongsecret-2"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_policy" "policy_2" {
  block_public_policy = true
  secret_arn          = aws_secretsmanager_secret.secret_2.arn

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "EnableAllPermissions",
      "Effect": "Allow",
      "Principal": {
        "AWS": "${aws_iam_role.irsa_role.arn}"
      },
      "Action": "secretsmanager:*",
      "Resource": "*"
    }
  ]
}
POLICY

}

###############################
# Parameter store challenge 1 #
###############################

resource "random_password" "password2" {
  length           = 24
  special          = true
  override_special = "_%@"
}

resource "aws_ssm_parameter" "secret" {
  name        = "wrongsecretvalue"
  description = "A secret "
  type        = "SecureString"
  value       = random_password.password2.result # Bootstrap something, not used in challenge

  lifecycle {
    ignore_changes = [
      value
    ]
  }
}
