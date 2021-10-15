resource "aws_secretsmanager_secret" "secret" {
  name = "wrongsecret"
}

resource "aws_secretsmanager_secret_policy" "name" {
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
