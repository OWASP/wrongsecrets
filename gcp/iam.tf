resource "google_service_account" "wrongsecrets_workload" {
  account_id   = "wrongsecrets-workload-sa"
  display_name = "WrongSecrets Cluster Service Account"
}

resource "google_iam_workload_identity_pool" "pool" {
  provider                  = google-beta
  workload_identity_pool_id = var.project_id
  project                   = var.project_id
}

resource "google_service_account_iam_member" "wrongsecret_pod_sa" {
  service_account_id = google_service_account.wrongsecrets_workload.id
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[default/vault]"
  depends_on = [
    google_iam_workload_identity_pool.pool,
    google_container_cluster.gke
  ]
}


locals {
  gke_cluster_roles = ["roles/logging.logWriter", "roles/monitoring.metricWriter", "roles/monitoring.viewer"]

}

resource "google_service_account" "wrongsecrets_cluster" {
  account_id   = "wrongsecrets-cluster-sa"
  display_name = "WrongSecrets Cluster Service Account"
}

resource "google_project_iam_member" "wrongsecrets_cluster_sa_roles" {
  project = var.project_id
  count   = length(local.gke_cluster_roles)
  role    = local.gke_cluster_roles[count.index]
  member  = "serviceAccount:${google_service_account.wrongsecrets_cluster.email}"
}
