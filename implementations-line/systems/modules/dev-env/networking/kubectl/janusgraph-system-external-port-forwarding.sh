#! /bin/bash
echo "Start the janusgraph-system external port forwarding processes"
# Access from external Janusgraph client over janusgraph-system:8182 to graph service instance running in ClusterIP mode on port 8182 TCP
kubectl --namespace default port-forward service/janusgraph-system 8182:8182
