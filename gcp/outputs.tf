output "region" {
  value       = var.region
  description = "GCloud Region"
}

output "project_id" {
  value       = var.project_id
  description = "GCloud Project ID"
}

output "kubernetes_cluster_name" {
  value       = google_container_cluster.gke.name
  description = "GKE Cluster Name"
}

output "kubernetes_cluster_host" {
  value       = google_container_cluster.gke.endpoint
  description = "GKE Cluster Host"
}

output "gke_config" {
  value       = "gcloud container clusters get-credentials --project ${var.project_id} --zone ${var.region} ${var.cluster_name}"
  description = "config string for the cluster credentials"
}
