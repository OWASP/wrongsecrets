data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

locals {
  k8s_service_account_namespace = "default"
  k8s_service_account_name      = "default"
}

############
# Pod role #
############

resource "aws_iam_role" "irsa_role" {
  name = "wrongsecrets-secret-manager"

  assume_role_policy = data.aws_iam_policy_document.assume_role_with_oidc.json
}

data "aws_iam_policy_document" "assume_role_with_oidc" {
  statement {
    principals {
      type        = "Federated"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:oidc-provider/${replace(module.eks.cluster_oidc_issuer_url, "https://", "")}"]
    }
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]
  }

  statement {
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role_policy_attachment" "irsa_role_attachment" {
  role       = aws_iam_role.irsa_role.name
  policy_arn = aws_iam_policy.secret_manager.arn
}

resource "aws_iam_policy" "secret_manager" {
  name_prefix = "secret-manager"
  description = "EKS secret manager policy for cluster ${module.eks.cluster_id}"
  policy      = data.aws_iam_policy_document.secret_manager.json
}

data "aws_iam_policy_document" "secret_manager" {
  statement {
    sid    = "readsecrets"
    effect = "Allow"

    actions = [
      "secretsmanager:Describe*",
      "secretsmanager:Get*",
      "secretsmanager:List*",
      "secretsmanager:Update*",
      "secretsmanager:Rotate*",
      "secretsmanager:PutSecret*",
      "ssm:DescribeParameters",
      "ssm:GetParameter*"
    ]

    resources = ["*"]
  }
}


#############
# User role #
#############

resource "aws_iam_role" "user_role" {
  name = "cant-read-secrets"

  assume_role_policy = data.aws_iam_policy_document.user_assume_role.json
}

data "aws_iam_policy_document" "user_assume_role" {
  statement {
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role_policy_attachment" "user_role_attachment" {
  role       = aws_iam_role.user_role.name
  policy_arn = aws_iam_policy.secret_deny.arn
}

resource "aws_iam_policy" "secret_deny" {
  name_prefix = "secret-deny"
  description = "Deny secrets manager and SSM"
  policy      = data.aws_iam_policy_document.user_policy.json
}

data "aws_iam_policy_document" "user_policy" {
  statement {
    sid    = "cantreadsecrets"
    effect = "Deny"

    actions = [
      "secretsmanager:*",
      "ssm:*"
    ]

    resources = ["*"]
  }
  statement {
    sid    = "canreadiam"
    effect = "Allow"

    actions = [
      "iam:Get*",
      "iam:List*",
      "iam:Describe*",
      "sts:AssumeRole"
    ]
    resources = ["*"]
  }
}
