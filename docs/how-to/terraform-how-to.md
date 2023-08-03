## PURPOSE
Presentation of the assistance documentation regarding the usage of Terraform tool (e.g command line usefull) allowing to develop Infrastructure As Code (IaC), maintain and manage the .tf files defining the CYBNITY Kubernetes provisioning.

# TERRAFORM USAGE
Several Helm command lines are available to be executed from the `charts directory`. The Kubernetes instance should be started and active (e.g Minikube started).

## Tool Installation
<details><summary>K8S Providers</summary>
<p>
The Terraform Kubernetes provider is a plugin for Terraform that allows users to manage Kubernetes resources using Terraform. It enables Terraform to interact with the Kubernetes API to create, update, and delete resources in a Kubernetes cluster.

With the Terraform Kubernetes provider, users can define their Kubernetes resources in Terraform configuration files and manage them in a declarative manner, just like other infrastructure resources. This makes it possible to use Terraform to manage the complete lifecycle of a Kubernetes application, from initial deployment to ongoing maintenance.

The Terraform Kubernetes provider supports a wide range of Kubernetes resources, including pods, services, and deployment configurations. It also supports Kubernetes extensions, such as Custom Resource Definitions (CRDs), and can be used to manage both on-premises and cloud-based Kubernetes clusters.

- Install asdf plugin

On Mac OS (see [article]([https://medium.com/rahasak/terraform-kubernetes-integration-with-minikube-334c43151931) for help):

```shell
# Upgrade Terraform version
brew upgrade terraform

# Install asdf plugin
brew install asdf

# add asdf terraform plugin
asdf plugin-add terraform

# install terraform latest version via asdf
asdf install terraform latest

```

</p>
</details>
