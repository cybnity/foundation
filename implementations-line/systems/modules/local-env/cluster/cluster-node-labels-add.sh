#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODES WHEN RESTARTED
echo "Add labels to the cluster nodes"

kubectl label nodes local-env4 cybnity.io/user-interfaces-area=true
kubectl label nodes local-env4-m02 cybnity.io/domains-io-area=true
kubectl label nodes local-env4-m03 cybnity.io/domains-area=true
kubectl label nodes local-env4-m04 cybnity.io/infrastructure-services-area=true

kubectl get nodes --show-labels
