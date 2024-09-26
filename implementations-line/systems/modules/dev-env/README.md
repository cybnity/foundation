## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment.

# PREREQUISITES
Presentation of the requirements relative to the installation and/or operating of systems that are used into the deployed context.

Any type of prerequisite can be defined by a selected tool according to its version (e.g defined by its open source project documentation), or can be specifically identified by CYBNITY for the kind of usage implemented (e.g limited to a specific environment size defined per IT activity).

This catalog of identified requirements allow to help the definition of resources (e.g minimum CPU, minimum memory size, ideal storage size) implemented at the infrastructure level (e.g Platform As A Service, or Infrastructure As A Service layer) and considered as part of the CYBNITY deployment view.

# CLUSTER MODULES
Contain each module (e.g applicative or technical service module that is proposed via a cluster) that can do a zero-downtime, rolling deployment.

One sub-directory is defined per deploying capability of a technical or applicative module.

## Utility scripts
Cluster folder includes shell scripts that help to create minikube cluster according to resource available on a computer used as centralized development server.

Several types of clusters can be build over command lines executions:
- Cluster of 4 nodes dedicated to receive only CYBNITY application systems deployed by the global Helm project relative to cybnity-platform (see IAC Helm Charts repository);
- Cluster of 1 node dedicated for installation of any CYBNITY application systems.

## Prerequisites
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|[Rancher Desktop](https://rancherdesktop.io/)|MacOs| |[cluster folder](cluster)|
|Minikube|MacOS, Linux, or Windows|__dev (1 node)__:<br>- RAM: 128Go<br>- Storage: 200Go<br>__dev-env4 (4 app nodes)__:<br>- RAM: 128Go<br>- Storage: 200Go|[cluster folder](cluster)|

## Usages
The cluster folder include scripts simplifying creation of K8S cluster (e.g Minikube cluster) according several types:
- dev: medium resources allocation for developer workstation based on only 1 node defined into a K8S cluster;
- dev-env4: medium resources alloncation for integration test server based on 4 nodes (similar to production target isolated area where systems are deployed) defined into a K8S cluster.

In case of cluster stop and restart on Minikube, the assigned labels (per node) are lost by default Minikube configuration.

Use the add-labels-to-xxxx-cluster.sh script dedicated to the used cluster for automatically re-assign the labels to cluster nodes.

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable and standalone deployable module.

One sub-directory is defined per reusable module name.

# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.

#
[Back To Home](../README.md)
