## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary development server.

The DEV cluster is dedicated to run of a version of CYBNITY software components which is not already released (e.g staging version, feature branch version).

The services provided by the CYBDEV01 server are focus on:
- primary server of the Dev Environment cluster
- detection of sub-resources sizing required by any change of CYBNITY applications
- unit test and/or integration development activities (e.g link to other external systems that integration development is in progress)

# HARDWARE LAYER
Current hardware configuration is based on servers set:
- CYDEV01
  - server type: Hewlett-Packard Z640
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
- CYDEV02
  - server type: Hewlett-Packard Z640
  - CPU: 2 x Intel Xeon E5-2673 v4,
  - RAM: 256 GB
  - Hard disks:
  
## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server into a __"ready for virtualization installation"__ state.

Current prepared server configuration is:
- hostname: __cybdev01__

# VIRTUALIZATION LAYER
RKE2 virtualization system is implemented as Kubernetes layer hosting the CYBNITY staging versions deployed into a __Dev cluster__.

## Helm
- Install Helm stable version via command:
```
  sudo snap install helm --classic
```

## RKE2 Kubernetes distribution
### RKE2 environment preparation
- Create tar installation directory via command:
```
  sudo mkdir /opt/rke2
```

- Create root user when not already existing via commands:
```
  # check existing root user
  su root

  # modify root password if not defined
  sudo passwd root

  # switch from current account to the root user account
  su root
```

### Private Certificates
Get new CYBNITY domain certificates bundles (including CA Root, intermediates crd files) usable for DEV cluster.

#### Update Ubuntu SSL certs bundles
Add certificates (private domain, intermediate, CA root) to certificated store of OS:
- Copy each crt file of the domain bundle into the `/usr/local/share/ca-certificates` folder (only private domain crt file original name shall be renamed for cybnity-domains.crt)
- Update the Ubuntu certificates store via command `sudo update-ca-certificates` (auto-upgrade of `/etc/ca-certificates.conf` file)

Add domain private key to OS:
- Copy domain private key file into `/etc/ssl/private` folder.

#### Update RKE2 certificates usabled for cluster
If previous RKE2 version need to be uninstalled before defining the CA root, execute uninstall via command `/opt/rke2/bin/rke2-uninstall.sh`

See [RKE2 security certificates](https://docs.rke2.io/security/certificates) documentation for help.
- Create `/var/lib/rancher/rke2/server/tls` folder and copy into it:
  - custom domain certificate (e.g 2299159002.crt defining the SSL certificate for domain and/or sub-domains) as `root-ca.pem` renamed file
  - private key of the custom domain (e.g 2299159002-domain.key defining the private key of SSL certificate) as `root-ca.key` renamed file
- Generate custom CA certs and keys via command `curl -sL https://github.com/k3s-io/k3s/raw/master/contrib/util/generate-custom-ca-certs.sh | PRODUCT=rke2 bash -` and check text shown regarding the database updates applied with success (e.g new entries made) about CA certificate generation completed.
- Create folder `mkdir -p /opt/rke2/server/tls`
  - Copy current service-account signing key considerated like current via `cp /var/lib/rancher/rke2/server/tls/service.key /opt/rke2/server/tls`, to avoid token lost during future custom CA certificate rotation

### RKE2 Installation
- As root user, set environment variable to use during RKE2 script installation execution via command:
```
  export INSTALL_RKE2_METHOD="tar"
  export INSTALL_RKE2_CHANNEL="stable"
  export INSTALL_RKE2_TYPE="server"
  export INSTALL_RKE2_TAR_PREFIX="/opt/rke2"

  # check defined environment variables
  printenv
```

- As root user, execute RKE2 installation script via command:
```
  curl -sfL https://get.rke2.io |  sh -
```

- Add permanent path to rke2 binary (`/opt/rke2/bin`) with adding into $PATH variable of file `/etc/environment`, and add KUBECONFIG environment variable:
```
  KUBECONFIG="/etc/rancher/rke2/rke2.yaml"
```

- Reload environment file to activate changed file, via command:
```
  set -a; . /etc/environment; set +a;
```

- Execute rotation to load the updated certs into the datastore via `rke2 certificate rotate-ca --path=/opt/rke2/server`... and RKE can be started

- Enable (symbolic link creation) and start the rke2-server service via commands:
```
  sudo systemctl enable rke2-server.service
  sudo systemctl start rke2-server.service

  # If Cluster Certificate Authority data has been updated via ca-rotate success result, rke2 must be restarted
  sudo systemctl restart rke2-server
```

- Create symbolic links to RKE2 tools (kubectl, kubelet, crictl, ctr) via commands:
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

- Copy KUBECONFIG file for cluster access from outside (https://docs.rke2.io/cluster_access documentation)

- Create `/etc/rancher/rke2/config.yaml` (https://docs.rke2.io/install/configuration) including:
```
  write-kubeconfig-mode: "0644"
  tls-san:
    # hostnames
    - "cybdev01.cybnity.tech"
    - "cybdev01.local"
  node-label:
    - "environment=dev"
  debug: true

  # database configuration
  # active snapshots
  etcd-disable-snapshots: false

  # snapshot interval time every 5 hours
  etcd-snapshot-schedule-cron: "0 */5 * * *"
  etcd-snapshot-retention: 3
```

- Change the permission allowed to the Kubernetes configuration files to minimise accessibility to other users via command:
```
sudo chmod 600 /etc/rancher/rke2/*.yaml
```

- Verify the server node registration via command line:
```
  # restart cluster based on config.yaml
  sudo systemctl restart rke2-server.service

  # check information about cluster nodes
  kubectl get nodes -o wide
```

- CIS Hardening
  Kernel parameters (https://docs.rke2.io/security/hardening_guide#kernel-parameters)
  - when `/usr/local/share/rke2/rke2-cis-sysctl.conf` is not existing, create it:
  ```
    vm.panic_on_oom=0
    vm.overcommit_memory=1
    kernel.panic=10
    kernel.panic_on_oops=1
  ```

  - make copy on shared directory as rke2 conf
  ```
  sudo cp -f /usr/local/share/rke2/rke2-cis-sysctl.conf /etc/sysctl.d/60-rke2-cis.conf
  sudo systemctl restart systemd-sysctl
  ```

- Optional containerd registry
  - If need, create `registries.yaml` into `/etc/rancher/rke2/` (as documented at https://docs.rke2.io/install/containerd_registry_configuration) usable by the server (e.g cybnity docker registry) including Personal Access Token used for private registries authentication

- Check started rke2-server service via commands:
```
systemctl status rke2-server

# show logs
journalctl -u rke2-server.service
```

## Kubernetes resources

### Base data storage area
- Create a sub-directory of `/srv` dedicated to the Kubernetes resources (e.g Persistent Volumes) via commands:
  ```
  sudo mkdir /srv/k8s
  sudo mkdir /srv/k8s/data
  ```

# INFRASTRUCTURE SERVICES

## Storage

## Monitoring & Logging

## Networking
### Container Network Interface (CNI)
Canal solution is deployed as CNI plugin.

### Remote control by Rancher (CYBSUP01)
Server used like OS and container runtime shall support additional requirements.

- Check that none firewall rule is actived on the node which block communication withing the K8S cluster. Since Kubernetes v1.19, firewall must be turned off, because it conflicts with the Kubernetes networking plugins.

### Authorized cluster endpoint support for RKE2 cluster control by Rancher
See [Rancher authorized cluster endpoint documentation](https://ranchermanager.docs.rancher.com/how-to-guides/new-user-guides/kubernetes-clusters-in-rancher-setup/register-existing-clusters#authorized-cluster-endpoint-support-for-rke2-and-k3s-clusters) for help.

As root user, execute the additional configuration documented procedure:
- Create `/var/lib/rancher/rke2/kube-api-authn-webhook.yaml` file with cluster properties defined
- Add `kube-apiserver-arg` to `/etc/rancher/rke2/config.yaml` file
- Stop and start the rke2-server
- When DEV cluster is imported for remote control by Rancher (executed on CYBDEV01), enable for Authorized Endpoint with entry of FQDN (e.g cybdev01.cybnity.tech) and certificate information (e.g cybnity domains certificate)

## Security

## Server auto-stop
Add a crontab directive to stop the server in a safe way (with wait of existing process secure end before make the stop) with power off:
- open and add command into crontab via command: `sudo crontab -e`
- add line in file and save as:
```
# stop and poweroff in secure way (shutdown -P) the server each day at 20:30:00
30 20 * * * shutdown -P
```

- check crontab plan via command: `sudo crontab -l`

## Time
Network Time Protocol (NTP) package shall be installed. This prevents errors with certificate validation that can occur when the time is not synchronized between the client and server. Timedate is by default installed on Ubuntu in place of previous ntpd tool, check that autosync of timezone from Internet is active via command:
```
  # check autosync time
  timedatectl
```
  - If ntpd is need, install it via command:
  ```
    sudo apt install ntp
  ```

## Cluster remote management
On Rancher:
- Create a new cluster (e.g named DEV) into a Rancher application executed on SUP server (e.g CYBSUP01) from Cluster Management section
- In Member Roles section, add user authorized to cluster management

On cluster eligible to remote management:
- Set the cluster-admin privileges for Rancher user via the command proposed by Rancher Registration section
- From cluster to control, execute the registration command proposed by Rancher (e.g via kubectl on existing DEV cluster) to import it into Rancher management system

# APPLICATION SERVICES

## CYBNITY software repository
- Add complementary helm charts (e.g allowing deployment of CYBNITY applications) to node repository list via commands:
```
  sudo helm repo add cybnity https://cybnity.github.io/iac-helm-charts --force-update
```

#
[Back To Home](CYDEL01.md)
