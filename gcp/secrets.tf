##############################
# Secret manager challenge 1 #
##############################

resource "google_secret_manager_secret" "wrongsecret_1" {
  secret_id = "wrongsecret-1"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_iam_member" "wrongsecret_1_member" {
  project   = google_secret_manager_secret.wrongsecret_1.project
  secret_id = google_secret_manager_secret.wrongsecret_1.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.wrongsecrets_workload.email}"
}

resource "random_password" "password" {
  length           = 24
  special          = true
  override_special = "_%@"
}

resource "google_secret_manager_secret_version" "secret_version_basic" {
  secret = google_secret_manager_secret.wrongsecret_1.id

  secret_data = random_password.password.result
}


###############################
# Secret manager challenge 2 #
###############################

resource "google_secret_manager_secret" "wrongsecret_2" {
  secret_id = "wrongsecret-2"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_iam_member" "wrongsecret_2_member" {
  project   = google_secret_manager_secret.wrongsecret_2.project
  secret_id = google_secret_manager_secret.wrongsecret_2.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.wrongsecrets_workload.email}"
}

###############################
# Secret manager challenge 3 #
###############################

resource "google_secret_manager_secret" "wrongsecret_3" {
  secret_id = "wrongsecret-3"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_iam_member" "wrongsecret_3_member" {
  project   = google_secret_manager_secret.wrongsecret_3.project
  secret_id = google_secret_manager_secret.wrongsecret_3.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.wrongsecrets_workload.email}"
}
