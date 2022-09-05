locals {
  gke_cluster_roles = toset(["roles/logging.logWriter", "roles/monitoring.metricWriter", "roles/monitoring.viewer"])
}

resource "google_service_account" "wrongsecrets_workload" {
  account_id   = "wrongsecrets-workload-sa"
  display_name = "WrongSecrets Workload Service Account"
}

resource "random_integer" "int" {
  min = 10000
  max = 99999
}


resource "google_iam_workload_identity_pool" "pool" {
  provider                  = google-beta
  workload_identity_pool_id = "${var.project_id}-${random_integer.int.result}"
  project                   = var.project_id
  display_name              = "WrongSecrets"
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

# Bonus IAM member for GCP Secret Manager challenge 3. Can be used in a pod.
resource "google_service_account_iam_member" "wrongsecret_wrong_pod_sa" {
  service_account_id = google_service_account.wrongsecrets_workload.id
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[default/default]"
  depends_on = [
    google_iam_workload_identity_pool.pool,
    google_container_cluster.gke
  ]
}

resource "google_service_account" "wrongsecrets_cluster" {
  account_id   = "wrongsecrets-cluster-sa"
  display_name = "WrongSecrets Cluster Service Account"
}

resource "google_project_iam_member" "wrongsecrets_cluster_sa_roles" {
  for_each = local.gke_cluster_roles

  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.wrongsecrets_cluster.email}"
}
