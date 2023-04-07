#! /bin/bash
echo "Add labels to the cluster nodes"

kubectl label nodes local-dev layer=user-interfaces-area
kubectl label nodes local-dev-m02 layer=domains-io-area
kubectl label nodes local-dev-m03 layer=domains-area
kubectl label nodes local-dev-m04 layer=infrastructure-services-area

kubectl get nodes --show-labels
