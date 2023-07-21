## PURPOSE
Presentation of the build and delivery chain files supporting the configuration of CYBNITY environments used during the project life cycle.

The Terraform project ensure the programmatic provisioning management of the infrastructure as IaC (Infrastructure As Code) project.

# CLUSTER MODULES
Contain each module (e.g applicative or technical service module that is proposed via a cluster) that can do a zero-downtime, rolling deployment.

One sub-directory is defined per deploying capability of a technical or applicative module.


# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a loab balancing module) as reusable and standalone deployable module.

One sub-directory is defined per reusable module name.


# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.
