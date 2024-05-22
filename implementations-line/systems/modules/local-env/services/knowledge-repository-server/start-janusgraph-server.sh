#! /bin/bash

# Installation of latest release of JanusGraph services with Cassandra storage backend
helm install knr-system -f values-janusgraph.yaml oci://registry-1.docker.io/bitnamicharts/janusgraph
