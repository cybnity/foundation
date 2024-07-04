#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to 2 cluster nodes" &&

kubectl label nodes dev cybnity.io/user-interfaces-area=true --overwrite &&
kubectl label nodes dev cybnity.io/domains-io-area=true --overwrite &&
kubectl label nodes dev cybnity.io/domains-area=true --overwrite &&
kubectl label nodes dev cybnity.io/infrastructure-services-area=true --overwrite &&

# define specific label to TOOLING node
kubectl label nodes dev-m02 cybnity.io/support-tooling-infrastructure-area=yes --overwrite &&

kubectl get nodes --show-labels
