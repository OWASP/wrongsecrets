output "cluster_endpoint" {
  description = "Endpoint for EKS control plane."
  value       = module.eks.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "Security group ids attached to the cluster control plane."
  value       = module.eks.cluster_security_group_id
}

output "irsa_role" {
  description = "The role ARN used in the IRSA setup"
  value       = aws_iam_role.irsa_role.arn
}

output "secrets_manager_secret_name" {
  description = "The name of the secrets manager secret"
  value       = aws_secretsmanager_secret.secret.name
}

output "cluster_id" {
  description = "The id of the cluster"
  value       = module.eks.cluster_id
}
