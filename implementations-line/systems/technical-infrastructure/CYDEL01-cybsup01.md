## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary support server.

The services provided by the CYBSUP01 server are focus on:
- primary server of the Support Environment cluster supporting all the CYDEL version's tools
- management of other environments (e.g other Kubernetes clusters)
- management of application systems deployment (e.g continuous delivery chain) on other environments (e.g dev, test...)

# HARDWARE LAYER

# NETWORKING LAYER

# OPERATING SYSTEM LAYER

## Ubuntu Linux Server
- Installation of Ubuntu LTS server version (e.g from bootable USB stick)
  - define boot and OS dedicated partitions for OS directories and mounting points
  - define independent datadisk dedicated to the K8S resources storage with a mounting point at `/srv`

- Swap (all nodes) disabling (change consistent after a reboot with fstb file modification) to enhance Kubernetes performance via command:

```
  sudo swapoff -a
  #sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
  sudo sed -i '/ swap / s/^/#/' /etc/fstab
```

- Add Kernel parameters (all nodes)
  - to load the required modules on all nodes (set up IP bridge for nodes to communicate over the network) via command:
```
  sudo tee /etc/modules-load.d/containerd.conf <<EOF
  overlay
  br_netfilter
  EOF
  sudo modprobe overlay
  sudo modprobe br_netfilter
```

  - configure critical kernel parameters for Kubernetes via command:
```
  sudo tee /etc/sysctl.d/kubernetes.conf <<EOF
  net.bridge.bridge-nf-call-ip6tables = 1
  net.bridge.bridge-nf-call-iptables = 1
  net.ipv4.ip_forward = 1
  EOF
```

  - reload the changes via command `sudo sysctl --system`

- AppArmor configuration (https://discourse.ubuntu.com/t/ubuntu-24-04-lts-noble-numbat-release-notes/39890#security-improvements)

- server hostname change
  - change default server's hostname defined during the standard SUSE installation by another one according to the server role (e.g "cybsup01" about CYBNITY Support's server 1) via command line `sudo hostnamectl set-hostname cybsup01`

  - to make change without closing the terminal via command `exec bash`

- update of /etc/hosts file check (e.g DHCP mode from network system) or static ip address
  Ensure your system can resolve its hostname by updating the /etc/hosts file with the IP address and the new hostname via command:
```
  sudo vi /etc/hosts

  # Add a line: server-desired-hostname
```

- Ubuntu version update and upgrade via command line:
```
  sudo apt-get update && sudo apt-get upgrade -y
  sudo apt update && sudo apt -y full-upgrade

```

## Intel 10-Gigabit X540-AT2 card drivers
Only to install when network card were not detected/configured during the origin Linux installation.

- download drivers files via command lines:
```
   curl -O https://downloadmirror.intel.com/832293/ixgbe-5.21.5.tar.gz
   curl -O https://downloadmirror.intel.com/832293/intel-public-key-ixgbe-ko.zip
   curl -O https://downloadmirror.intel.com/832296/ixgbevf-4.20.5.tar.gz
   curl -O https://downloadmirror.intel.com/832296/intel-public-key-ixgbevf-ko.zip
```

- execute driver installation via command lines:
```
   transactional-update -i pkg install ixgbe-5.21.5.tar.gz
   transactional-update -i pkg install intel-public-key-ixgbe-ko.zip
```
- reboot system
- check visibility of detected network cards via command `ip a`

# VIRTUALIZATION LAYER

## Helm
- install Helm stable version via command:
```
  sudo snap install helm --classic
```

## RKE2 Kubernetes distribution
- create tar installation directory via command:
```
  mkdir /opt/rke2
```

- set environment variable to use during RKE2 script installation execution via command:
```
  export INSTALL_RKE2_METHOD="tar"
  export INSTALL_RKE2_CHANNEL="stable"
  export INSTALL_RKE2_TYPE="server"
  export INSTALL_RKE2_TAR_PREFIX="/opt/rke2"
```

- execute RKE2 installation script via command:
```
  curl -sfL https://get.rke2.io |  sh -
```

- add permanent path to rke2 binary (/opt/rke2/bin) and add it to PATH into file `/etc/environment`

- enable and start the rke2-server service via commands:
```
  sudo systemctl enable rke2-server.service
  sudo systemctl start rke2-server.service
```

- create symbolic link to RKE2 tools (kubectl, kubelet, crictl, ctr) via commands:
```
  sudo ln -s /var/lib/rancher/rke2/bin/kubectl /usr/local/bin/kubectl
  sudo ln -s /var/lib/rancher/rke2/bin/kubelet /usr/local/bin/kubelet
  sudo ln -s /var/lib/rancher/rke2/bin/crictl /usr/local/bin/crictl
  sudo ln -s /var/lib/rancher/rke2/bin/ctr /usr/local/bin/ctr
```

- create symbolic link to the created kubeconfig file via commands:
```
  mkdir -p ~/.kube
  sudo ln -s /etc/rancher/rke2/rke2.yaml ~/.kube/config
```

- verify the server node registration via command line:
```
  kubectl get nodes -o wide
```

- copy KUBECONFIG for cluster access (https://docs.rke2.io/cluster_access documentation)

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

## Kubernetes resources

### Base data storage resource (Persistent Volume)
- create a sub-directory of `/srv` dedicated to the Kubernetes resources (e.g Persistent Volumes) via commands:
  ```
  mkdir /srv/k8s
  mkdir /srv/k8s/pvc
  ```

- __from root $HOME directory__, create a symbolic link simplying the identification of K8S data storage base path via command:
  ```
  # Allow direct access to RKE2 dedicated disk area reserved for K8S data (e.g under pvc folder)
  sudo ln -s /srv/k8s/pvc .kube/pvc
  ```

- create new PV resource file (automatic applied resource during RKE2 start) regarding base persistent volume via command:
  `vi /var/lib/rancher/rke2/server/manifests/rke2-base-pv.yaml`

  - file containing (see for help [best practices for PV](https://www.loft.sh/blog/kubernetes-persistent-volumes-examples-and-best-practices)) according to storage capacity available:
  ```
  apiVersion: v1
  kind: PersistentVolume
  metadata:
    name: base-pv
  spec:
    capacity:
      storage: 900Gi
    volumeMode: Filesystem
    accessModes:
      - ReadWriteOnce
    persistentVolumeReclaimPolicy: Retain
    storageClassName: standard
    hostPath:
      path: "/srv/k8s/pvc"
  ```

  - check the automatically created resource via command: `kubectl get pv`

# INFRASTRUCTURE SERVICES

## MONITORING & LOGGING

## SECURITY

# APPLICATION SERVICES

## Helm repositories
- add needed helm charts to node repository list via commands:
```
  helm repo add rancher-stable https://releases.rancher.com/server-charts/stable
  helm repo add jetstack https://charts.jetstack.io
  helm repo add cybnity https://cybnity.github.io/iac-helm-charts
```

## KUBERNETES MANAGEMENT (Rancher)

## SYSTEMS CONTINOUS DELIVERY (Spinnaker)


#
[Back To Home](CYDEL01.md)
