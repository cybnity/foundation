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
|Halyard station|Docker instance of Ubuntu Linux 18.04+|- RAM: 12GB+<br>- CPU: 1+ core|Command-line administration tool of Spinnaker nodes|
|CD server|Spinnaker application, dockerized Ubuntu 18.04+|- CPU: 4+ cores per node<br>- RAM: 16GB+|CYBNITY Continuous Integration & Delivery tool into CYBNITY environments; 2 HA nodes recommended|
|CD datastore|MinIO, Docker under K8S|__per node__:<br>- CPU: 1+ core<br>- Storage: NVMe SSD<br>- Network: 100 Gbps with dual NICs|Persistence system (object store) of Spinnaker instances' data.<br>2 HA node recommended.<br>Persistence size need is based on Spinnaker storage need.<br>[Hardwware for MinIO deployment]([https://min.io/product/reference-hardware](https://blog.min.io/selecting-hardware-for-minio-deployment/))|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/)|
|Linux|Ubuntu LTS Linux server| |Operating System of single-node|
|Physical server| | |[CYDEL01 documentation](../../technical-infrastructure/CYDEL01-cybsup01.md)|

### CYDEL02
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Halyard station|Docker instance of Ubuntu 18.04+|- RAM: 12GB+<br>- CPU: 1+ core|Command-line administration tool of Spinnaker nodes|
|CD server|Spinnaker application, dockerized Ubuntu 18.04+|- CPU: 4+ cores per node<br>- RAM: 16GB+|CYBNITY Continuous Integration & Delivery tool into CYBNITY environments; 2 HA nodes recommended|
|CD datastore|MinIO, Docker under K8S|__per node__:<br>- CPU: 1+ core<br>- Storage: NVMe SSD<br>- Network: 100 Gbps with dual NICs|Persistence system (object store) of Spinnaker instances' data.<br>2 HA node recommended.<br>Persistence size need is based on Spinnaker storage need.<br>[Hardwware for MinIO deployment]([https://min.io/product/reference-hardware](https://blog.min.io/selecting-hardware-for-minio-deployment/))|
|RKE2 cluster|RKE2|- RAM: 8GB+<br>- CPU: 4+ cores|RKE2 Kubernetes platform.<br>Supported [Linux distributions](https://www.suse.com/suse-rke2/support-matrix/all-supported-versions/rke2-v1-30/)|
|Linux|SUSE Linux Enterprise Micro (Base OS, Podman, LIBVIRT/KVM, Cockpit, Salt-Minion)| |Operating System of single-node.<br>Cockpit Web server for Linux OS remote administration.<br>Podman as virtual containers management Command-Line tool|
|Harvester cluster|Harvester including Linux OS (Elemental SUSE Linux Enterprise Micro 5.4), KubeVirt, Longhorn, Grafana, Prometheus|__per node__:<br>- CPU: x86_64 (with hardware-assisted virtualization), 16 cores (minimum)<br>- RAM: 64GB (minimum)<br>- Storage: 500GB (minimum), SSD/NVMe<br>- Networking: 2 NICs, 1Gbps Ethernet (minimum)|Harvester HCI cluster virtualizing 2 physical machine nodes. [Hardware and network requirements doc](https://docs.harvesterhci.io/v1.3/install/requirements).<br>[KubeVirt](https://kubevirt.io/) (virtualization management using KVM on top of Kubernetes).<br>[Longhorn doc](https://longhorn.io/) providing distributed block storage.<br>[Grafana](https://grafana.com/) and [Prometheus](https://prometheus.io/) providing monitoring and logging|

## Usages
Support environment provide multiple tools and server applications that allow support and administration of other environments like:
- Spinnaker server for software Continuous Delivery activities management and automation
- Spinnaker Halyard for administration tool of Spinnaker configurations and instances
- MinIO object storage server S3-API compatible as persitence solution of Spinnaker servers (e.g pipelines, settings)
- RKE2 for Kubernetes containerization and clustering of resources as a private cloudified infrastructure
- Rancher server for administration of Harvester clusters and VMs
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
See [CYDEL01 documentation](../../technical-infrastructure/CYDEL01-cybsup01.md).

### Hardware resources sizing
| | NEED | CURRENT | SIZING STATUS |
|:--|:--:|:--:|:--:|
| CPU CORES | 16 | 18 | :white_check_mark: |
| RAM | 64GB | 128GB | :white_check_mark: |
| STORAGE SIZE | 500GB | 2TB | :white_check_mark: |
| NETWORK SPEED| 10Gpbs |1Gbps + 10Gbps + 10Gbps| :white_check_mark: |

## CYDEL02
### Harvester nodes (future)
Not already built and deployed as Harvester cluster (2 nodes) dedicated to support environment.

#
[Back To Home](../README.md)
