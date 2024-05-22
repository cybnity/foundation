#! /bin/bash

# Create a minikube profile (allowed memory and cpu are defined PER NODE)
minikube start --driver=hyperkit --container-runtime=docker --profile local-dev --nodes 4 --cpus 2 --disk-size '10g' --memory '8g' &&

# WHEN CLUSTER INCLUDING ONLY ONE UNIQUE NODE : Export docker host and Docker daemon into the shell context variables
#minikube docker-env

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to a cluster based on 4 nodes" &&

kubectl label nodes local-dev cybnity.io/user-interfaces-area=true &&

kubectl label nodes local-dev-m02 cybnity.io/domains-io-area=true &&

kubectl label nodes local-dev-m03 cybnity.io/domains-area=true &&

kubectl label nodes local-dev-m04 cybnity.io/infrastructure-services-area=true &&

kubectl get nodes --show-labels &&

# Installation of CustomResourceDefinition resources is recommended by kubectl and not from Helm chart avoiding an automatic CRDs instances deletion when CYBNITY Platform environment is upgraded or deleted from Helm CLI
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.11.1/cert-manager.crds.yaml
