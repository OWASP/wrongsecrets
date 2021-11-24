provider "google" {
  project = var.project_id
  region  = var.region
}


provider "random" {}

provider "http" {}

data "http" "ip" {
  url = "http://ipecho.net/plain"
}


resource "google_container_cluster" "gke" {
  name               = var.cluster_name
  location           = var.region
  initial_node_count = 1

  min_master_version = var.cluster_version

  network    = google_compute_network.vpc.name
  subnetwork = google_compute_subnetwork.node_subnet.name

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  node_config {
    # Google recommends custom service accounts that have cloud-platform scope and permissions granted via IAM Roles.
    service_account = google_service_account.wrongsecrets_cluster.email
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
    labels = {
      application = "wrongsecrets"
    }
    tags = ["wrongsecrets"]
  }

  master_authorized_networks_config {
    cidr_blocks {
      cidr_block   = "${data.http.ip.body}/32"
      display_name = "user origin"
    }
  }

  timeouts {
    create = "30m"
    update = "40m"
  }
}
