resource "google_compute_network" "vpc" {
  name                    = "${var.project_id}-vpc"
  auto_create_subnetworks = "false"
}

resource "google_compute_subnetwork" "node_subnet" {
  name          = "${var.project_id}-subnet"
  region        = var.region
  network       = google_compute_network.vpc.name
  ip_cidr_range = "10.10.0.0/24"
}

resource "google_compute_subnetwork" "master_subnet" {
  name          = "${var.project_id}-master-subnet"
  region        = var.region
  network       = google_compute_network.vpc.name
  ip_cidr_range = "10.32.0.0/28"
}
