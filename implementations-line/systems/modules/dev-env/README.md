## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment.

Usages: hosting environment as shared resources dedicated to deployment of CYBNITY software suite versions currently developed by CYBNITY developers.

# PREREQUISITES
Presentation of the requirements relative to the installation and/or operating of systems that are used into the deployed context.

Any type of prerequisite can be defined by a selected tool according to its version (e.g defined by its open source project documentation), or can be specifically identified by CYBNITY for the kind of usage implemented (e.g limited to a specific environment size defined per IT activity).

This catalog of identified requirements allow to help the definition of resources (e.g minimum CPU, minimum memory size, ideal storage size) implemented at the infrastructure level (e.g Platform As A Service, or Infrastructure As A Service layer) and considered as part of the CYBNITY deployment view.

# DEPLOYMENT MODULES
Deployment modules are configurations allowing the automation of modules installation and deployment onto clusters.

Reusable source codes and scripts are available into:
- __Fleet__ [dedicated repository](https://github.com/cybnity/fleet-cd): [repository of contents](https://fleet.rancher.io/gitrepo-content) (considered as Fleet source codes) used by [Rancher Fleet solution](https://fleet.rancher.io/) for installation and deployment management of a CYBNITY environment.

# CLUSTER MODULES
Several cluster configurations are defined as modules (e.g applicative or technical service module that is proposed via a cluster) that can do a zero-downtime, and/or rolling deployment.

Reusable scripts are available into sub-folder: [cluster](cluster).

One sub-directory is defined per technology including:
- __Kubectl__ [folder](cluster/kubectl): common utility scripts based on kubectl Kubernetes client, usable on several types of K8S clusterized infrastructure (e.g Minikube, RKE2)
- __Minikube__ [folder](cluster/minikube): specialized utility scripts based on Minikube tool, usable on Minikube infrastructure
- __RKE2__ [folder](cluster/rke2): specialized utility scripts based on RKE2 tool, usable on RKE2 infrastructure

## Minikube clusters
Components allowing build and deployment of CYBNITY environment running onto Minikube technology stack installed.

### Prerequisites
- Minikube container installed on a server or a workstation as unique cluster.

### Utility scripts
Cluster sub-folder includes shell scripts that help to create Minikube cluster according to a computer used as centralized development server.

Each type of cluster based on unique server which can be built with support of [Minikube cluster scripts](cluster/minikube):
- __Cluster of 1 logical node__ dedicated for installation of any CYBNITY application systems
- __Cluster of 4 logical nodes__ dedicated to receive CYBNITY application systems deployed by the global Helm project relative to cybnity-platform (see IAC Helm Charts repository)

## RKE2 clusters
Components allowing build and deployment of CYBNITY environment running onto RKE2 technology stack installed.

### Prerequisites
- [RKE2 Rancher's Kubernetes distribution](https://docs.rke2.io/) container installed on unique or multiple servers

### CYDEL01 infrastructure
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/)|
|Linux|Ubuntu LTS Linux server| |Operating System per node|

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable for deployed cluster modules.

Reusable scripts are available into sub-folder: [networking](networking).

One sub-directory is defined per technology including:
- __Kubectl__ [folder](networking/kubectl): common utility scripts based on kubectl tool and usable on several types of K8S cluster module (e.g Minikube, or RKE2 cluster)

# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.

# HARDWARE INFRASTRUCTURE
## CYDEL01
Infrastructure implemented resources:
- DEV cluster models:
  - [CYDEL01-CYBDEV01 documentation](../../technical-infrastructure/CYDEL01-cybdev01.md)
  - [CYDEL01-CYBDEV02 documentation](../../technical-infrastructure/CYDEL01-cybdev02.md)

#
[Back To Home](../README.md)
