## PURPOSE
Presentation of the build and delivery chain files supporting the configuration of CYBNITY environments used during the project life cycle.

The modularization project ensure the programmatic provisioning management of the infrastructure as IaC (Infrastructure As Code) project (e.g via Terraform, and/or Helm).

# ENVIRONMENTS SPECIFIC COMPONENTS
Some additional and specific components and/or Kubernetes service can be added automatically during the creation of an environment according to its target usages.

## TOOLS
When technical tools (e.g continuous delivery solution) are included into an environment provisioning process, they are installed and operated into an independent K8S node.

The assignment of components to tools node is ensured by affinity based on label `cybnity.io/support-tooling-infrastructure-area=yes`.

#
[Back To Home](../README.md)
