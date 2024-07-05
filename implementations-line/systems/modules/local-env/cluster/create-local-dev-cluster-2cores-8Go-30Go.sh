#! /bin/bash
# CLUSTER PROFILE dedicated to a physical server configuration
# For example:
# - Intel Core i7 unique CPU (2 cores, 2 threads)
#
# TARGET INFRASTRUCTURE PLATFORM (usage: standalone applications development)
# Nodes: 1 (see https://learnk8s.io/kubernetes-node-size for help about unique vs multiples nodes)
# CPUs: 2 (50% of real available vCPUs capacity)
# RAM: 8Go (50% allocated to virtualized platform according to the real available physical memory)
# Storage: 30Go (according to available free storage)

# Create a minikube profile (allowed memory and cpu are defined PER NODE)
minikube start --driver=hyperkit --container-runtime=docker --profile local-dev --nodes 1 --cpus 2 --disk-size '30g' --memory '8g' &&

# Activate optionnal modules
minikube -p local-dev addons enable ingress
minikube -p local-dev addons enable metrics-server

# WHEN CLUSTER INCLUDING ONLY ONE NODE : Export docker host and Docker daemon into the shell context variables
minikube docker-env

# Define new created profile as default
minikube profile local-dev

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to cluster unique node" &&

# Define application node labels
kubectl label nodes local-dev cybnity.io/user-interfaces-area=true &&
kubectl label nodes local-dev cybnity.io/domains-io-area=true &&
kubectl label nodes local-dev cybnity.io/domains-area=true &&
kubectl label nodes local-dev cybnity.io/infrastructure-services-area=true &&

kubectl get nodes --show-labels &&

# Update version of kubectl
minikube kubectl -- get pods -A

# Installation of CustomResourceDefinition resources is recommended by kubectl and not from Helm chart avoiding an automatic CRDs instances deletion when CYBNITY Platform environment is upgraded or deleted from Helm CLI
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.11.1/cert-manager.crds.yaml

# Monitor the progress of started system pods for the kube-system namespace
kubectl get pods --namespace=kube-system
