## PURPOSE
Directory of contents supporting the provisioning of infrastructure's resources dedicated to a specific environment.

# SETTING

## VOLUME PERMISSIONS
When a cluster is started without as a standard user account (and not via sudo command), a problem can arrive during deployment of CYBNITY Defense Platform helm charts regarding the type of user used by K8S (e.g minikube default basis account that was initialized during the ministart command execution).

There are to mounting moments:

- minikube mounting the directory with minikube mount volumne being mounted in Kubernetes;
- minikube mount sets up the directory in the VM with the UID and GID provided as parameters, with the default being docker user and group.

When the volume is being mounted in the Pod as a directory, it gets mounted with the exact same UID and GID as the host one!

The potential problem appear into the K8s is about some permission denied on node including volumes (e.g sub-charts dedicated to postgreSQL installation).

This is a long-term issue that prevents a non-root user to write to a container when mounting a hostPath PersistentVolume in Minikube. There are two common workarounds:
- Simply use the root user during the start of minikube (e.g via sudo command)
- or [configure a Security Context for a Pod or Container](https://kubernetes.io/docs/tasks/configure-pod-container/security-context/) using runAsUser, runAsGroup and fsGroup. You can find a detailed info with an example in the link provided.
