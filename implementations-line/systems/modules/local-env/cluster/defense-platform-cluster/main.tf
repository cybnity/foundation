## ---- KUBERNETES NAMESPACE DEFINITION ----
resource "kubernetes_namespace" "cybnity-local-env" {
    metadata {
        name = "cybnity-local-env"
    }
}

resource "kubernetes_nodes" "ui_layer" {
  metadata {
    labels = {
      layer = "user-interfaces-area"
    }
  }
}

resource "kubernetes_node" "domain_io_layer" {
  metadata {
    labels = {
      layer = "domains-io-area"
    }
  }
}

resource "kubernetes_node" "domains_layer" {
  metadata {
    labels = {
      layer = "domains-area"
    }
  }
}

resource "kubernetes_node" "infrastructure_layer" {
  metadata {
    labels = {
      layer = "infrastructure-services-area"
    }
  }
}
