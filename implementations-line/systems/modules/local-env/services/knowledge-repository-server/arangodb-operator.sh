#! /bin/bash

# Installation of latest release of ArangoDB using Kubectl

# See https://arangodb.github.io/kube-arangodb/ for help

# --- Community Edition --- on Kubernetes default active profile ---

# --- ARANGOD DEPLOYMENTS ---
# Install from kubectrl the operator and basic CRDs resources
# Installation of CustomResourceDefinition resources is recommended by kubectl and not from Helm chart avoiding an automatic CRDs instances deletion when CYBNITY Platform environment is upgraded or deleted from Helm CLI
#kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/1.2.40/manifests/arango-crd.yaml
#kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/1.2.40/manifests/arango-deployment.yaml
# To use `ArangoLocalStorage`, also run
#kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/1.2.40/manifests/arango-storage.yaml
# To use `ArangoDeploymentReplication`, also run
#kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/1.2.40/manifests/arango-deployment-replication.yaml


# or install latest release from Helm the operator and basic CRDs resources
# use --generate-name parameter in place of logical name when dynamic name shall be automatically generated
# To use `ArangoLocalStorage`, set field `operator.features.storage` to true
helm install knr-system https://github.com/arangodb/kube-arangodb/releases/download/1.2.40/kube-arangodb-1.2.40.tgz --set "operator.features.storage=true"

# See https://arangodb.github.io/kube-arangodb/docs/using-the-operator.html#example-deployment-using-minikube for help
# Deploy a cluster of ArangoDB resource into the cluster (CRD > Arango Deployment > Objects)
#minikube kubectl -- apply -f arangodb-cluster.yaml

# or deploy/apply the single server of ArangoDB resource into the cluster (CRD > Arango Deployment > Objects)
minikube kubectl -- apply -f arangodb-single-server.yaml

# Inspect the current status of the deployment
minikube kubectl -- describe ArangoDeployment single-server

# Inspect the pods created for the deployment
minikube kubectl -- get pods --selector=arango_deployment=single-server

# Delettion of deployment
#kubectl delete -f arangodb-single-server.yaml

