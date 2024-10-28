## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary support server.

The services provided by the CYBSUP01 server are focus on:
- primary server of the Support Environment cluster supporting all the CYDEL version's tools
- management of other environments (e.g other Kubernetes clusters of CYDEL01)
- hosting of Continuous Delivery tools and applications managing the detection and installation of CYBNITY software suite versions into other K8S clusters
- management of application systems deployment life cycle (e.g continuous delivery chain) on other K8S cluster (e.g dev, test...)

# HARDWARE LAYER
Current hardware configuration is based on a Dell 5810 server:
- CPU: Intel Xeon E5-2695 v4, 18 cores
- RAM: 128 GB
- Hard disks:
  - NVMe SSD 512 GB (Operation System & Linux based applications)
  - 1 SATA Disk 1.5 TB (K8S applications data)
- NVIDIA GeFore GTX 1060 graphic card, 6Go
- 1 NIC 1Gbps: used for Wake-On-Lan (remote start of server)
- 2 x NIC 10Gbps:
  - 1 dedicated ot Kubernetes HA clustering
  - 1 not used

## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server into a __"ready for virtualization installation"__ state.

Current prepared server configuration is:
- hostname: __cybsup01__

# VIRTUALIZATION LAYER
RKE2 virtualization system is implemented as Kubernetes layer hosting the CYBNTY support applications deployed into a __Support cluster__.

## Helm
- Install Helm stable version via command:
```
  sudo snap install helm --classic
```

## RKE2 Kubernetes distribution
- Create tar installation directory via command:
```
  mkdir /opt/rke2
```

- Set environment variable to use during RKE2 script installation execution via command:
```
  export INSTALL_RKE2_METHOD="tar"
  export INSTALL_RKE2_CHANNEL="stable"
  export INSTALL_RKE2_TYPE="server"
  export INSTALL_RKE2_TAR_PREFIX="/opt/rke2"
```

- Execute RKE2 installation script via command:
```
  curl -sfL https://get.rke2.io |  sh -
```

- Add permanent path to rke2 binary (/opt/rke2/bin) and add it to PATH into file `/etc/environment`

- Enable and start the rke2-server service via commands:
```
  sudo systemctl enable rke2-server.service
  sudo systemctl start rke2-server.service
```

- Create symbolic link to RKE2 tools (kubectl, kubelet, crictl, ctr) via commands:
```
  sudo ln -s /var/lib/rancher/rke2/bin/kubectl /usr/local/bin/kubectl
  sudo ln -s /var/lib/rancher/rke2/bin/kubelet /usr/local/bin/kubelet
  sudo ln -s /var/lib/rancher/rke2/bin/crictl /usr/local/bin/crictl
  sudo ln -s /var/lib/rancher/rke2/bin/ctr /usr/local/bin/ctr
```

- RKE2 default __cluster configuration file is rke2.yaml__. Create symbolic link to the created kubeconfig file via commands:
```
  mkdir -p ~/.kube
  sudo ln -s /etc/rancher/rke2/rke2.yaml ~/.kube/config
```

- Verify the server node registration via command line:
```
  kubectl get nodes -o wide
```

- Copy KUBECONFIG for cluster access (https://docs.rke2.io/cluster_access documentation)

- create conig.yaml (https://docs.rke2.io/install/configuration)

- CIS Hardening
  - Kernel parameters (https://docs.rke2.io/security/hardening_guide#kernel-parameters)
  ```
  sudo cp -f /usr/local/share/rke2/rke2-cis-sysctl.conf /etc/sysctl.d/60-rke2-cis.conf
  sudo systemctl restart systemd-sysctl
  ```

- Containerd registry
  - create registries.yaml into /etc/rancher/rke2/ (as documented at https://docs.rke2.io/install/containerd_registry_configuration) usable by the server (e.g cybnity docker registry) including Personal Access Token used for private registries authentication

- Snapshot configuration
  - add configuration lines in `/etc/rancher/rke2/config.yaml`:
  ```
  # Database configuration
  # active snapshots
  etcd-disable-snapshots: false
  # snapshot interval time every 5 hours
  etcd-snapshot-schedule-cron: "0*/5 * * *"
  etcd-snapshot-retention: 3
  ```

  - restart service via command:
  `systemctl start rke2-server`

- Check started rke2-server service via commands:
```
systemctl status rke2-server

# show logs
journalctl -u rke2-server.service
```

## Kubernetes resources

### Base data storage resource (Persistent Volume)
- Create a sub-directory of `/srv` dedicated to the Kubernetes resources (e.g Persistent Volumes) via commands:
  ```
  mkdir /srv/k8s
  mkdir /srv/k8s/data
  ```

- __From root $HOME directory__, create a symbolic link simplying the identification of K8S data storage base path via command:
  ```
  # Allow direct access to RKE2 dedicated disk area reserved for K8S data (e.g under data folder, for PersistentVolumes creation)
  sudo ln -s /srv/k8s/data .kube/data
  ```

- Create new PV resource file (automatic applied resource during RKE2 start) regarding base persistent volume via command:
  `vi /var/lib/rancher/rke2/server/manifests/rke2-base-pv.yaml`

  - file containing (see for help [best practices for PV](https://www.loft.sh/blog/kubernetes-persistent-volumes-examples-and-best-practices)) according to storage capacity available:
  ```
  apiVersion: v1
  kind: PersistentVolume
  metadata:
    name: base-pv
  spec:
    capacity:
      storage: 50Gi
    volumeMode: Filesystem
    accessModes:
      - ReadWriteOnce
    persistentVolumeReclaimPolicy: Retain
    hostPath:
      path: "/srv/k8s/data"
  ```

  - Check the automatically created resource via command: `kubectl get pv`

# INFRASTRUCTURE SERVICES

## Storage
### Longhorn
Distributed block storage system deployed for containers data management.

## Monitoring & Logging

## Networking
### Container Network Interface (CNI)
Canal solution is deployed as CNI plugin.

## Security
### Rancher Backup
Automated backup solution ensuring auto-save of Rancher instance into a scheduled approach, to file versions allowing restoration in case of Rancher container disaster.

## Server auto-stop
Add a crontab directive to stop the server in a safe way (with wait of existing process secure end before make the stop) with power off:
- open and add command into crontab via command: `sudo crontab -e`
- add line in file and save as:
```
# stop and poweroff in secure way (shutdown -P) the server each day at 20:30:00
30 20 * * * shutdown -P
```

- check crontab pla via command: `sudo crontab -l`

# APPLICATION SERVICES

## Kubernetes management (Rancher)
- Add needed helm charts to node repository list via command:
```
  helm repo add rancher-stable https://releases.rancher.com/server-charts/stable
```

### Cert-Manager
- As [documented](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/other-installation-methods/rancher-behind-an-http-proxy/install-rancher#install-cert-manager), add cert-manager Helm repository and install cert-manager with CRDs via command:
```
  helm repo add jetstack https://charts.jetstack.io --force-update
  kubectl create namespace cert-manager
  helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.16.1 \
  --set crds.enabled=true
```

- valid cert-manager deployment via commands:
```
kubectl rollout status deployment -n cert-manager cert-manager
kubectl rollout status deployment -n cert-manager cert-manager-webhook
```

See [cert-manager documentation](https://cert-manager.io/docs/installation/helm/) for help.

### Kubernetes clusters management (Rancher)
Ranche instance is deployed for management of any K8S cluster of CYDEL01 infrastructure.

See [Rancher technical documentation](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster) for help.

- Namespace creation into the support cluster via command:
```
sudo kubectl create namespace cattle-system
```

- [delete certain version created certificates](https://ranchermanager.docs.rancher.com/v2.6/troubleshooting/other-troubleshooting-tips/expired-webhook-certificate-rotation) that will expire after on year via commands:
```
kubectl delete secret -n cattle-system cattle-webhook-tls
kubectl delete mutatingwebhookconfigurations.admissionregistration.k8s.io --ignore-not-found=true rancher.cattle.io
kubectl delete pod -n cattle-system -l app=rancher-webhook
```

- [Installation of Rancher](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster#5-install-rancher-with-helm-and-your-chosen-certificate-option) over Helm with Rancher-generated certificates
```
helm install rancher rancher-stable/rancher \
  --namespace cattle-system \
  --set hostname=cybsup01 \
  --set bootstrapPassword=admin
```

- Follow command results to read the web url and to open the web console

- Verify that Rancher server is successfully deployed via commands:
```
kubectl -n cattle-system rollout status deploy/rancher
kubectl -n cattle-system get deploy rancher
```

## CYBNITY software repository
- Add complementary helm charts (e.g allowing deployment of CYBNITY applications) to node repository list via commands:
```
  helm repo add cybnity https://cybnity.github.io/iac-helm-charts
```

## Systems continuous delivery (Spinnaker)


#
[Back To Home](CYDEL01.md)
