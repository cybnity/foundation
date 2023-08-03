#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to the cluster nodes" &&

kubectl label nodes local-env2 cybnity.io/user-interfaces-area=true &&

kubectl label nodes local-env2 cybnity.io/infrastructure-services-area=true &&

kubectl label nodes local-env2-m02 cybnity.io/domains-io-area=true &&

kubectl label nodes local-env2-m02 cybnity.io/domains-area=true &&

kubectl get nodes --show-labels
