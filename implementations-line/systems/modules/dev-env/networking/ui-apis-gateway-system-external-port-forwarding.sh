#! /bin/bash
echo "Start the ui-apis-gateway-system external port forwarding processes"
# Access from external web browser on HTTP://127.0.0.1:8083/ to Web Reactive Frontend service instance running in ClusterIP mode on port 80 TCP
kubectl --namespace default port-forward service/ui-apis-gateway-system 8083:80
