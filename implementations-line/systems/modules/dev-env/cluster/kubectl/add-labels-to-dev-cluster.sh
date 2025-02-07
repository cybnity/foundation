#! /bin/bash

# BE CAREFULL, MINIKUBE LOST LABELS ON NODE WHEN RESTARTED
echo "Add labels to unique cluster node" &&
sudo kubectl --kubeconfig=.kube/config label nodes dev cybnity.io/user-interfaces-area=true --overwrite &&
sudo kubectl --kubeconfig=.kube/config label nodes dev cybnity.io/domains-io-area=true --overwrite &&
sudo kubectl --kubeconfig=.kube/config label nodes dev cybnity.io/domains-area=true --overwrite &&
sudo kubectl --kubeconfig=.kube/config label nodes dev cybnity.io/infrastructure-services-area=true --overwrite &&

# Show assigned label on unique node
sudo kubectl --kubeconfig=.kube/config get nodes --show-labels
