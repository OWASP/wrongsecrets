resource "google_service_account" "wrongsecrets_cluster" {
  account_id   = "wrongsecrets-sa"
  display_name = "WrongSecrets Cluster Service Account"
}

resource "google_service_account_iam_member" "wrongsecret_pod_sa" {
  service_account_id = google_service_account.wrongsecrets_cluster.id
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[default/vault]"
}
