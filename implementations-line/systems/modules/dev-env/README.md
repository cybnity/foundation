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
Cluster sub-folder includes shell scripts that help to create minikube cluster according to resource available on a computer used as centralized development server.

Several types of clusters can be build over command lines executions:
- Cluster of 4 nodes dedicated to receive only CYBNITY application systems deployed by the global Helm project relative to cybnity-platform (see IAC Helm Charts repository);
- Cluster of 1 node dedicated for installation of any CYBNITY application systems.

## Prerequisites
### CYDDEL01
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/)|
|Linux|Ubuntu LTS Linux server| |Operating System of single-node|
|Physical server| | |[CYDEL01 documentation](../../technical-infrastructure/CYDEL01-cybdev01.md)|

### CYDEL02
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/)|

## Usages
Hosting environment as shared resources dedicated to deployment of CYBNITY software suite versions currently developed by CYBNITY developers.

### RKE2
The [RKE2 is Rancher's Kubernetes distribution](https://docs.rke2.io/) that focuses on security.

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable and standalone deployable module.

Reusable scripts are available into sub-folder: [networking/kubectl](networking/kubectl).

One sub-directory is defined per reusable module name.

# SERVICES MODULES
Contain each module specifically for deploying the CYBNITY containerized application component.

One sub-directory is defined per CYBNITY application deployable as a Kubernetes service.

# HARDWARE INFRASTRUCTURE
Current support environment is built over a set of physical resources.

## CYDEL01
See [CYDEL01 documentation](../../technical-infrastructure/CYDEL01-cybdev01.md).

### Hardware resources sizing
| | NEED | CURRENT | SIZING STATUS |
|:--|:--:|:--:|:--:|
| CPU CORES | 16 | 28 | :white_check_mark: |
| RAM | 128GB | 128GB | :white_check_mark: |
| STORAGE SIZE | 500GB | 3.5TB | :white_check_mark: |
| NETWORK SPEED| 10Gpbs |1Gbps + 10Gbps + 10Gbps| :white_check_mark: |

#
[Back To Home](../README.md)
