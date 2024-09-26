#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODE WHEN RESTARTED
echo "Add labels to the cluster node" &&

kubectl label nodes local-dev cybnity.io/user-interfaces-area=true --overwrite &&

kubectl label nodes local-dev cybnity.io/domains-io-area=true --overwrite &&

kubectl label nodes local-dev cybnity.io/domains-area=true --overwrite &&

kubectl label nodes local-dev cybnity.io/infrastructure-services-area=true --overwrite &&

kubectl get nodes --show-labels
