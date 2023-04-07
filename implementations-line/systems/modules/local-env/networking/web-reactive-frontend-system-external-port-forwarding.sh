#! /bin/bash
echo "Start the web-reactive-frontend-system external port forwarding processes"
# Access from external web browser on HTTP://127.0.0.1:8081/ to Web Reactive Frontend service instance running in ClusterIP mode on port 80 TCP
kubectl --namespace default port-forward service/web-reactive-frontend-system 8081:80
