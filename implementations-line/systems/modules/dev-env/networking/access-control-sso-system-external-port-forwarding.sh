#! /bin/bash
echo "Start the access-control-sso-system external port forwarding processes"
# Access from external web browser on HTTP://127.0.0.1:8080/ to keycloak running in LoadBalancer mode on port 80 TCP
kubectl port-forward --namespace default service/access-control-sso-system 8080:80
