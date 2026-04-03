## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment.

# CLUSTER MODULES
Contain each module (e.g applicative or technical service module that is proposed via a cluster) that can do a zero-downtime, rolling deployment.

One sub-directory is defined per deploying capability of a technical or applicative module.

## Prerequisites
Optional additional resources can be required for simulation activities supporting the demonstration and validation activities.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|RKE2||||

## Usages

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable and standalone deployable module.

One sub-directory is defined per reusable module name.


# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.

#
[Back To Home](../README.md)
