## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment usable on a local development server (e.g workstation or server machine deployed into the local network of a development team).

# Cluster
The cluster folder include scripts simplifying creation of K8S cluster (e.g Minikube cluster) according several types:
- local-dev: medium resources allocation for developer workstation based on only 2 nodes defined into the K8S cluster
- local-env4: medium resources alloncation for integration test server based on 4 nodes (similar to production target isolated area where systems are deployed) defined into the K8S cluster

In case of cluster stop and restart on Minikube, the assigned labels (per node) are lost by default Minikube configuration.

Use the xxxx-labels.add.sh script dedicated to the used cluster for automatically re-assign the labels to cluster nodes.

# Networking
The networking folder include utility scripts simplifying the developers specific needs like:
- port forwarding and exposure out-of a started cluster, that allow unit tests of specific systems

# Services
The services folder includes utility scripts that simplify unit deployment of specific systems over Kubectl and/or Helm
