## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary development server.

The DEV cluster is dedicated to run of a version of CYBNITY software components which is not already released (e.g staging version, feature branch version).

The services provided by the CYBDEV01 server are focus on:
- primary server of the Dev Environment cluster
- detection of sub-resources sizing required by any change of CYBNITY applications
- unit test and/or integration development activities (e.g link to other external systems that integration development is in progress)

# HARDWARE LAYER
Current hardware configuration is based on a Hewlett-Packard Z640 server:
- CPU: 2 x Intel Xeon E5-2690 v4, 28 cores
- RAM: 128 GB
- Hard disks:
  - NVMe SSD 512 GB (Operation System & Linux based applications)
  - 1 SATA Disk 3 TB (K8S applications data)
- NVIDIA Quadro P4000 graphic card, 8Go
- 1 NIC 1Gbps: used for Wake-On-Lan (remote start of server)
- 2 x NIC 10Gbps:
  - 1 dedicated ot Kubernetes HA clustering
  - 1 not used

## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server into a __"ready for virtualization installation"__ state.

Current prepared server configuration is:
- hostname: __cybdev01__

# VIRTUALIZATION LAYER
RKE2 virtualization system is implemented as Kubernetes layer hosting the CYBNTY staging versions deployed into a __Dev cluster__.

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

# APPLICATION SERVICES

## CYBNITY software repository
- Add complementary helm charts (e.g allowing deployment of CYBNITY applications) to node repository list via commands:
```
  helm repo add cybnity https://cybnity.github.io/iac-helm-charts
```

## Systems continuous delivery (Spinnaker)


#
[Back To Home](CYDEL01.md)
