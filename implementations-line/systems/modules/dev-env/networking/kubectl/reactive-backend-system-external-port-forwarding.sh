#! /bin/bash
echo "Start the reactive-backend-system external port forwarding processes"
# Access from external web browser on HTTP://127.0.0.1:8082/ to Reactive Backend service instance running in ClusterIP mode on port 80 TCP
kubectl --namespace default port-forward service/reactive-backend-system 8082:80
