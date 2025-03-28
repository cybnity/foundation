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
Cluster folder includes shell scripts that help to create a cluster according to resource available on a computer used as centralized tools server allowing transversal control of other environments (e.g dev, qa, uat...).

## Prerequisites
### CYDEL01
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|CD server|Fleet application, dockerized Ubuntu 18.04+|- CPU: 4+ cores per node<br>- RAM: 16GB+|CYBNITY Continuous Integration & Delivery tool into CYBNITY environments; 2 HA nodes recommended|
|CD datastore|MinIO, Docker under K8S|__per node__:<br>- CPU: 1+ core<br>- Storage: NVMe SSD<br>- Network: 100 Gbps with dual NICs|Persistence system (object store) of Fleet instances' data.<br>2 HA node recommended.<br>Persistence size need is based on cluster storage need.<br>[Hardwware for MinIO deployment]([https://min.io/product/reference-hardware](https://blog.min.io/selecting-hardware-for-minio-deployment/))|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/); [Longhorn doc](https://longhorn.io/) providing distributed block storage|
|Linux|Ubuntu LTS Linux server| |Operating System of single-node|
|MinIO|Linux| |Block storage clustering solution for data blocks management over S3 API|
|Physical server| | |[CYDEL01-CYBSUP01 documentation](../../technical-infrastructure/CYDEL01-cybsup01.md)|

## Usages
Support environment provide multiple tools and server applications that allow support and administration of other environments like:
- Rancher Fleet server for software Continuous Delivery activities management and automation
- MinIO object storage server S3-API compatible as persitence solution of continuous delivery solution (e.g pipelines, settings)
- RKE2 for Kubernetes containerization and clustering of resources as a private cloudified infrastructure
- Rancher server for administration of Kubernetes clusters and VMs
- Virtualization of physical resources defining a private infrastructure layer

### RKE2
The [RKE2 is Rancher's Kubernetes distribution](https://docs.rke2.io/) that focuses on security.

# NETWORKING MODULES
Contain each module managing connectivity and/or traffic (e.g a load balancing module) as reusable and standalone deployable module.

Reusable scripts are available into sub-folder.

One sub-directory is defined per reusable module name.

# SERVICES MODULES
Contain each module specifically for supporting the CYBNITY other infrastructures, environments and application systems.

One sub-directory is defined per support tool/server deployable as a Kubernetes service.

# HARDWARE INFRASTRUCTURE
Current support environment is built over a set of physical resources.

## CYDEL01
See [CYDEL01-CYBSUP01 documentation](../../technical-infrastructure/CYDEL01-cybsup01.md).

### Hardware resources sizing
| | NEED | CURRENT | SIZING STATUS |
|:--|:--:|:--:|:--:|
| CPU CORES | 16 | 18 | :white_check_mark: |
| RAM | 64GB | 128GB | :white_check_mark: |
| STORAGE SIZE | 500GB | 2TB | :white_check_mark: |
| NETWORK SPEED| 10Gpbs |1Gbps + 10Gbps + 10Gbps| :white_check_mark: |

#
[Back To Home](../README.md)
