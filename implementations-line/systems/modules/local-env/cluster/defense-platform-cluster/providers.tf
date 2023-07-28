
terraform {
  required_providers {
    kubernetes = {
      source = "hashicorp/kubernetes"
      version = ">= 2.11.0"
    }
  }
}

## ---- PROVIDER SETUP ----
# for help, see https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs/guides/getting-started#provider-setup
provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "minikube"
}

#provider "minikube" {
#  kubernetes_version = "v1.26.1"
#}
