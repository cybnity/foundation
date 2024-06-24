#! /bin/bash
# CLUSTER PROFILE dedicated to a physical server supported by an Intel Xeon E5-2697v2 unique CPU (12 cores, 24 threads)
# supportable 24 vCPUs (24 threads x 1 CPU)
#
# TARGET INFRASTRUCTURE PLATFORM (usage: centralized applications development)
# Nodes: 1 (see https://learnk8s.io/kubernetes-node-size for help about unique vs multiples nodes)
# RAM: 12 (50% of real available vCPUs capacity)
# RAM: 64Go (50% allocated to virtualized platform according to the real available physical memory)
# Storage: 100Go (according to available free storage)

# Create a minikube profile (allowed memory and cpu are defined PER NODE)
minikube start --driver=hyperkit --container-runtime=docker --profile dev --nodes 1 --cpus 12 --disk-size '100g' --memory '64g' &&

# WHEN CLUSTER INCLUDING ONLY ONE NODE : Export docker host and Docker daemon into the shell context variables
minikube docker-env

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to the cluster" &&

kubectl label nodes dev cybnity.io/user-interfaces-area=true &&

kubectl label nodes dev cybnity.io/domains-io-area=true &&

kubectl label nodes dev cybnity.io/domains-area=true &&

kubectl label nodes dev cybnity.io/infrastructure-services-area=true &&

kubectl get nodes --show-labels &&

# Installation of CustomResourceDefinition resources is recommended by kubectl and not from Helm chart avoiding an automatic CRDs instances deletion when CYBNITY Platform environment is upgraded or deleted from Helm CLI
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.11.1/cert-manager.crds.yaml
