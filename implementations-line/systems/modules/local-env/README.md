## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment usable on a local development server (e.g workstation or server machine deployed into the local network of a development team).

# PREREQUISITES
Presentation of the requirements relative to the installation and/or operating of systems that are used into the deployed context.

Any type of prerequisite can be defined by a selected tool according to its version (e.g defined by its open source project documentation), or can be specifically identified by CYBNITY for the kind of usage implemented (e.g limited to a specific environment size defined per IT activity).

This catalog of identified requirements allow to help the definition of resources (e.g minimum CPU, minimum memory size, ideal storage size) implemented at the infrastructure level (e.g Platform As A Service, or Infrastructure As A Service layer) and considered as part of the CYBNITY deployment view.

# CLUSTER MODULES
Contain each module (e.g applicative or technical service module that is proposed via a cluster) that can do a zero-downtime, rolling deployment.

One sub-directory is defined per deploying capability of a technical or applicative module.

## Utility scripts
Cluster folder includes shell scripts that help to create minikube cluster according to resource available on a computer used as centralized development server.

One type of cluster can be build over command line executions:
- Cluster of 1 node dedicated for installation of __any CYBNITY system as standalone component__.

BE CAREFULL: simultanuous installation of all components (e.g cybnity-platform full set on a workstation in parallel of an Software IDE) is __requiring a minimum quantity of RAM__, that shall be more supported by the usage of the [dev-env section](../dev-env) sized for centralized environment.

## Prerequisites
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Minikube||||

## Usages
The cluster folder include scripts simplifying creation of K8S cluster (e.g Minikube cluster) according several types:
- local-dev: medium resources allocation for developer workstation based on only 1 node defined into a K8S cluster.

In case of cluster stop and restart on Minikube, the assigned labels (per node) are lost by default Minikube configuration.

Use the add-labels-to-local-cluster.sh script dedicated to the used cluster for automatically re-assign the labels to cluster nodes.

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable and standalone deployable module.

One sub-directory is defined per reusable module name.

The networking folder include utility scripts simplifying the developers specific needs like:
- port forwarding and exposure out-of a started cluster, that allow unit tests of specific systems.

# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.

The services folder includes utility scripts that simplify unit deployment of specific systems over Kubectl and/or Helm.

#
[Back To Home](../README.md)
