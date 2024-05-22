#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to the cluster nodes" &&

kubectl label nodes local-dev cybnity.io/user-interfaces-area=true --overwrite &&

kubectl label nodes local-dev-m02 cybnity.io/domains-io-area=true --overwrite &&

kubectl label nodes local-dev-m03 cybnity.io/domains-area=true --overwrite &&

kubectl label nodes local-dev-m04 cybnity.io/infrastructure-services-area=true --overwrite &&

kubectl get nodes --show-labels
