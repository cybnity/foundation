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
Get new CYBNITY domain certificates bundles (including CA Root, intermediates crt and key files) usable for SUPPORT cluster (sup.cybnity.tech as Subject Alternative Name).

### Root CA
From a wilcard signed certificate (e.g including cybnity.tech, and *.cybnity.tech sub-domains as Subject Alternative Name extensions):
- __STAR_cybnity_tech.crt__ certificate (Root CA for domain dedicated to CYDEL infrastructure)
- support any sub-domains servers (2 levels only supported; for example support.cybnity.tech) authorized for SSL that include a DNS name equals to __support.cybnity.tech__ (SAN usable as unique entry point of cluster)

#### Update Ubuntu SSL certificate bundles
Add certificates (private domain, intermediate, CA root) to certificated store of OS:
- Copy each crt file of the domain bundle into the `/usr/local/share/ca-certificates` folder (e.g private domain crt file named STAR_cybnity_tech.crt)
- Update the Ubuntu certificates store via command `sudo update-ca-certificates` (auto-upgrade of `/etc/ca-certificates.conf` file)

To add future certificates to store, just copy the file (in PEM format) into the the __/user/local/share/ca-certificates__ folder and re-execute the store update command.

When Trust-Manager kubernetes application is installed on K8S, this OS certificates package is automatically taken as reference about supported CA certificates.

Add domain private key file to Ubuntu OS:
- Copy domain private key file into `/etc/ssl/private` folder.

#### Update RKE2 certificates usabled for cluster
If previous RKE2 version need to be uninstalled before defining the CA root, execute uninstall via command `/opt/rke2/bin/rke2-uninstall.sh`

See [RKE2 security certificates](https://docs.rke2.io/security/certificates) documentation for help.
- Create `/var/lib/rancher/rke2/server/tls` folder and copy into:
  - custom domain certificate (e.g STAR_cybnity_tech.crt defining the SSL certificate for domain and/or sub-domains) as `root-ca.pem` renamed file
  - private key of the custom domain (e.g private.key file of custom domain SSL certificate) as `root-ca.key` renamed file
- Generate custom CA certs and keys via command `curl -sL https://github.com/k3s-io/k3s/raw/master/contrib/util/generate-custom-ca-certs.sh | PRODUCT=rke2 bash -` and check text shown regarding the database updates applied with success (e.g new entries made) about CA certificate generation completed.
  - As recommended by command result, store in a secure area the root and intermediate certificate files (.pem, .crt, .key) and remove them from __/var/lib/rancher/rke2/server/tls__ folder
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

- As root user, Execute RKE2 installation script via command:
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

- Copy KUBECONFIG file for cluster access from outside (https://docs.rke2.io/cluster_access documentation) in other machine that would like to access and connect to RKE2 instance

- Create `/etc/rancher/rke2/config.yaml` (https://docs.rke2.io/install/configuration) including:
```
  write-kubeconfig-mode: "0644"
  tls-san:
    # hostnames
    - "cybsup01.cybnity.tech"
    - "sup.cybnity.tech"
    - "cd.cybnity.tech"
    - "cybsup01.local"
  node-label:
    - "environment=support"
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
sudo systemctl status rke2-server

# show logs
journalctl -u rke2-server.service
```

## Kubernetes resources

### Base data storage area (Persistent Volume)
- Create a sub-directory of `/srv` dedicated to the Kubernetes resources (e.g Persistent Volumes) via commands:
  ```
  sudo mkdir /srv/k8s
  sudo mkdir /srv/k8s/data
  ```

- __From root $HOME directory__, create a symbolic link simplying the identification of K8S data storage base path via command:
  ```
  # Allow direct access to RKE2 dedicated disk area reserved for K8S data (e.g under data folder, for PersistentVolumes creation)
  sudo ln -s /srv/k8s/data .kube/data
  ```

- Create new PV resource file (automatic applied resource during RKE2 start) regarding base persistent volume via command:
  `sudo vi /var/lib/rancher/rke2/server/manifests/rke2-base-pv.yaml`

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
Distributed block storage system deployed in Kubernetes RKE2 for containers data management.

### MinIO
Distributes and S3-compatible storage system deployed on Linux OS for commond block-based data management between all CYDEL01 servers.

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

## Java Runtime
Default openJDK java runtime is automatically installed on Ubuntu (defined by distribution version).

During Halyard tool update, a more young version of Java runtime can be required that allow Halyard run.

## Installation of Java alternative version on Linux OS
- Identify available versions of Java from Ubuntu referential via command:
```
apt search openjdk | grep -E 'openjdk-.*-jdk/'
```
- Based on available jdk versions provided by Ubuntu referential, install Halyard minimum required Java version via command:
```
sudo apt install openjdk-21-jdk
```
- Verify version of compiler and console installed via commands:
```
java --version
javac --version
```
- When multiple Java versions have been installed, a switch of current default activated version and installed versions can be managed via command:
```
# For Java runtime environment switch (and select version to use)
sudo update-alternatives --config java

# For Java Compiler switch (and select version to use)
sudo update-alternatives --config javac
```

- Identify where active Java is installed via commands:
```
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))

# Add permanent JAVA_HOME variable and with adding into $PATH variable of file `/etc/environment` ($JAVA_HOME/bin added $PATH)

# Reload environment variables
set -a; . /etc/environment; set +a;
```

## Custom Resources
Multiple dedicated resources are managed into the cluster because required by applications deployed into the cluster.

Find here little bit informations for help to identify them about their disseminitation.

### Secrets
Several types of key elements are managed in the Secret resources section:
- __rke2-ingress-nginx-admission__ and __rke2-serving__ including self-signed certificates and private keys generated by RKE2 for usage by Ingress components
- __star-cybnity-tech-tls__ and __server__ including custom domain certificate and private key properties (e.g cybnity.tech custom domain)
- __tls-ca__ including public certificate of custom domain provider (e.g Sectigo RSA domain validation secure server CA certificate)
- __tls-ca-additionnal__ including PEM formatted set of certificates relative to intermediary organizations (e.g authorithy validation server) involved into the custom domain certificate delivery (e.g list of ordered intermediate certificates)
- __tls-rancher-ingress__ including exposed domain certificates (e.g cybnity.tech + intermediate certificates) and private key usable for SSL implementation by a Rancher Ingress Controller
- __tls-rancher__ including dynamic generated self-signed certificate by Rancher application
- __tls-rancher-internal__ and __tls-rancher-internal-ca__ including dynamic generated self-signed certificates used by Rancher application

# APPLICATION SERVICES

## Kubernetes management (Rancher)
- Add needed helm charts to node repository list via command:
```
  sudo helm repo add rancher-stable https://releases.rancher.com/server-charts/stable
```

### Kubernetes clusters management (Rancher)
Ranche instance is deployed for management of any K8S cluster of CYDEL01 infrastructure.

See [Rancher technical documentation](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster) for help.

- Namespace creation into the support cluster via command:
```
sudo kubectl create namespace cattle-system
```

- [delete certain version created certificates](https://ranchermanager.docs.rancher.com/v2.6/troubleshooting/other-troubleshooting-tips/expired-webhook-certificate-rotation) that will expire after one year via commands:
```
kubectl delete secret -n cattle-system cattle-webhook-tls
kubectl delete mutatingwebhookconfigurations.admissionregistration.k8s.io --ignore-not-found=true rancher.cattle.io
kubectl delete pod -n cattle-system -l app=rancher-webhook
```

- [Installation of Rancher](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster#5-install-rancher-with-helm-and-your-chosen-certificate-option) over Helm with Rancher-generated (when ingress.tls.source parameter is not defined) certificates. See [Rancher Helm chart options doc](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/installation-references/helm-chart-options) regarding the common options.
  - Create the certificate and key files usable by Rancher installer from the custom domain certificate and key files:
    tls.crt = custom domain server certificate file followed by any intermediate certificate(s)
    tls.key = custom domain certificate private key
  - Check Common Name and Subject Alternative Names on the cusom certificate:
  ```
  # Show subject recognized in certificate
  openssl x509 -noout -subject -in tls.crt

  ```
  - Add TLS secrets as secret via:
  ```
  kubectl -n cattle-system create secret tls tls-rancher-ingress --cert=tls.crt --key=tls.key
  ```

  - Create cacerts.pem file including the custom domain CA trust chain (containing only root CA certificate or certificate chain from private CA), and create `tls-ca` secret via:
  ```
  kubectl -n cattle-system create secret generic tls-ca --from-file=cacerts.pem
  ```

  - Create a folder dedicated to the Rancher audti logs on `/srv/k8s/audit/rancher`

  - For installation (using default "system-store" allowing Rancher agent nodes trust any certificate generated by a public Certificate Authority contained in the Operating System's trust store including those signed by authorities) custom domain certificate:
  ```
  sudo helm install rancher rancher-stable/rancher \
    --namespace cattle-system \
    --set hostname=cybsup01.cybnity.tech \
    --set bootstrapPassword=cybnity \
    --set ingress.tls.source=secret \
    --set privateCA=true \
    --set agentTLSMode=system-store \
    --set tls=ingress \
    --set auditLog.level=3 \
    --set auditLog.destination=hostPath \
    --set auditLog.hostPath=/srv/k8s/audit/rancher \
    --set auditLog.maxAge=10 \
    --set auditLog.maxBackup=10 \
    --set auditLog.maxSize=100 \
    --set debug=true \
    --set additionalTrustedCAs=true
  ```

- Copy additional custom intermediary certificates only (trust chain without custom domains certificate) in pem format into a file named `ca-additional.pem` and use `kubectl` to create the `tls-ca-additional` secret in the `cattle-system` namespace via:
```
kubectl -n cattle-system create secret generic tls-ca-additional --from-file=ca-additional.pem=./ca-additional.pem
```

- Verify that Rancher server is successfully deployed via commands:
```
kubectl -n cattle-system rollout status deploy/rancher
kubectl -n cattle-system get deploy rancher
```

- Custom certificate provisioning as Secret usable across the cluster
  - When need, extract a public key to PEM format from authority CA via command:
    ```
    # Extract public key of CA certificate (Sectigo authority) as PEM format file
    openssl x509 -pubkey -noout < SectigoRSADomainValidationSecureServerCA.crt > root-ca-public.key

    # Create PEM version of key usable for creation of a K8S secret based on Authority Certificate (e.g SectigoRSADomainValidationSecureServerCA.crt)
    #openssl rsa -in any-certificate-private.key -out key-pem-formatted-version.pem

    ```

  - Creation of K8S secret resource including the private key and custom domains certificate (as star-cybnity-tech-tls) via commands:
    ```
    # Create Secret resource in cluster
    kubectl -n cattle-system create secret tls star-cybnity-tech-tls --cert=/path/to/STAR_cybnity_tech.crt --key=private.key

    # Verify the secret created
    kubectl -n cattle-system get secret star-cybnity-tech-tls
    ```

    - Creation of a Cluster Issuer for the cluster (allow to issue certificates using CYBNITY custom domains certificate) defined in new manifest file stored in RKE2
      - Creation of new `rke2-trust-cybnity-tech-issuer.yaml` file containing:
      ```
      # Allow to issue certificates using the CYBNITY.TECH custom domains certificate (Secret resource)
      apiVersion: cert-manager.io/v1
      kind: ClusterIssuer
      metadata:
        name: trust-cybnity-tech-issuer
      spec:
        ca:
          secretName: star-cybnity-tech-tls # Name of the custom domains certificate stored as Secret
      ---
      # CYBNITY.TECH sub-domains certificate
      apiVersion: cert-manager.io/v1
      kind: Certificate
      metadata:
        name: trust-cybnity-tech
        namespace: kube-system
      spec:
        isCA: false
        commonName: trust-cybnity-tech
        secretName: trust-cybnity-tech-secret
        issuerRef:
          name: trust-cybnity-tech-issuer
          kind: ClusterIssuer
        dnsNames:
        - cybnity.tech
        - cybsup01.cybnity.tech
        - sup.cybnity.tech
      ```
      - Apply resource for instantiation in cluster via command `kubectl apply -f rke2-trust-cybnity-tech-issuer.yaml` and verify good creation of ClusterIssuer and Certificate into the cluster
      - Remove created objects in cluster, and move the manifest file into `/var/lib/rancher/rke2/server/manifests/` for automatic binding by RKE2
      - Minimize accessibility regarding permissions via command `sudo chmod 600 /var/lib/rancher/rke2/server/manifests/rke2-trust-cybnity-tech-issuer.yaml`

      ```mermaid
      %%{
        init: {
          'theme': 'base',
          'themeVariables': {
              'background': '#ffffff',
              'fontFamily': 'arial',
              'fontSize': '12px',
              'primaryColor': '#fff',
              'primaryTextColor': '#0e2a43',
              'primaryBorderColor': '#0e2a43',
              'secondaryColor': '#fff',
              'secondaryTextColor': '#fff',
              'secondaryBorderColor': '#fff',
              'tertiaryColor': '#fff',
              'tertiaryTextColor': '#fff',
              'tertiaryBorderColor': '#fff',
              'lineColor': '#0e2a43',
              'titleColor': '#fff',
              'textColor': '#fff',
              'lineColor': '#0e2a43',
              'nodeTextColor': '#fff',
              'nodeBorder': '#0e2a43',
              'noteTextColor': '#fff',
              'noteBorderColor': '#fff'
          },
          'flowchart': { 'curve': 'monotoneY' }
        }
      }%%
      graph LR
        subgraph cluster["Cluster"]
          subgraph clusterIssuer["#60;#60;Cluster Issuer#62;#62;"]
            issuer1["trust-cybnity-tech-issuer"]
          end
          subgraph certificate["#60;#60;Certificate#62;#62;"]
            cert1["trust-cybnity-tech"]
          end
          subgraph secret["#60;#60;Secret#62;#62;#10;"]
            sc1["star-cybnity-tech-tls"]
          end
        end

        certificate -. "verified via" .-> clusterIssuer
        certificate -. "using secret values" .-> secret

        classDef mediumfill fill:#3a5572, stroke:#3a5572, color:#fff

        classDef red fill:#e5302a, stroke:#e5302a, color:#fff, stroke-width:3px
        classDef medium fill:#fff, stroke:#3a5572, color:#3a5572
        classDef mediumdot fill:#fff, stroke:#3a5572, color:#3a5572, stroke-dasharray: 5 5
        classDef reddot fill:#fff, stroke:#e5302a, color:#e5302a, stroke-dasharray: 5 5, stroke-width:3px
        classDef dark fill:#0e2a43, stroke:#fff, color:#fff
        classDef internalconfig fill:#0e2a43, stroke:#fff, color:#fff

        class issuer1,cert1,sc1 medium;
        class cluster,clusterIssuer,certificate,secret mediumdot;
      ```

- Follow command results to read the web url allowing to change the default admin password from a web browser

## CYBNITY software repository
- Add complementary helm charts (e.g allowing deployment of CYBNITY applications) to node repository list via commands:
```
  sudo helm repo add cybnity https://cybnity.github.io/iac-helm-charts --force-update
```

## Systems continuous delivery (Spinnaker)
### MinIO
Object storage solution that provides an Amazon Web Services S3-compatible API and supports all core S3 features, and that is installed in Linux OS to be used in a a same approach than an external S3 cloud system by the Spinnaker solution.
- Prerequisites
  - Create minio folder dedicated to received applications into `sudo mkdir /etc/minio`
  - Set read, write and execution permissions on folder via command: `chmod -R 755 /etc/minio` for root
- Installation of MinIO server on Linux OS layer from the `/etc/minio` directory
  ```
  # Download minio Debian packages
  wget https://dl.min.io/server/minio/release/linux-amd64/archive/minio_20241107005220.0.0_amd64.deb -O minio.deb

  # Installation of minio package on Linux
  sudo dpkg -i minio.deb

  # Check installation success
  ls /usr/local/bin/minio

  # Show minio executable version
  minio -v

  # Like DPKG does not manage the dependencies of MinIO, execute potential dependencies update via apt-get
  apt-get -f install
  ```

- Verify that systemd service file have been created in `/usr/lib/systemd/system/minio.service`

- Create DNS entry about initial (and future if multi-nodes of MinIO) node in case of multi-node distributed deployment
  Create DNS hostname mapping dedicated to MiniIO nodes (see [sequential hostnames doc](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-multi-node-multi-drive.html)) into the `/etc/hosts` file with lines to support non-sequential hostnames or IP addresses:
  ```
  # MinIO sequential hostnames configuration
  192.168.60.18 minio1.cybnity.tech
  #192.X.xx.x minio2.cybnity.tech
  #192.Y.yy.y minio3.cybnity.tech
  ```

- Define configuration of disk to use by MinIO
  - Identify the types of filesystem assigned on disks and mounted ares via command `parted -l` or `lsblk -f`
  - MinIO recommend selection of disks:
    - Direct-Attached Storage (DAS) like NVMe of SSD or primary or "hot" data
    - XFS formatted drives for storage
  - Mount and create folder dedicated to MinIO managed data (e.g in __/srv/miniodisk1__ folder) when none dedicated disk is available for MinIO

- Create the minio-user user and group defined by the `/usr/lib/systemd/system/minio.service` configuration file via command:
  ```
  # Create MinIO group
  groupadd -r minio-user

  # Create MinIO user account
  useradd -M -r -g minio-user minio-user
  ```

- Assign ownership of each mounting point and storage are to MinIO user account
  ```
  # Assign disk area to minio-user
  chown -R minio-user:minio-user /srv/miniodisk1

  #chown minio-user:minio-user /mnt/disk1 /mnt/disk2 /mnt/disk3
  ```

- Create the service environment file at `/etc/default/minio`, as source of all environment variables __used by MinIO and the minio.service__ file, that include:
  ```
  # Enable Virtual host-style requests to the MinIO deployment
  # Set the value of the FQDN for MinIO to accept incoming virtual host requests
  MINIO_DOMAIN=cybnity.tech

  # Set the hosts and volumes that MinIO uses at startup
  # The command uses MinIO expansion notation {x...y} to denote a
  # sequential series.
  #
  # Specify "https" instead of "http" to enable TLS.
  #
  # The following example covers four MinIO hosts
  # with 4 drives each at the specified hostname and drive locations.
  # The command includes the port that each MinIO server listens on
  # (default 9000)
  #MINIO_VOLUMES="https://minio{1...4}.cybnity.tech:9000/mnt/disk{1...4}/minio"

  # Primary server is only defined in this first deployed node (that shall be updated to target othe nodes of the clusterized MinIO infrastructure)
  MINIO_VOLUMES="/srv/miniodisk1/minio"

  # Set all MinIO server options
  # By default, the MinIO server evaluate the user account sub-directory (${HOME}/.minio/certs) to find keys and certificates enabling TLS. The evaluated sub-directory can be changed via --certs-dir or -s argument added into the MINIO_OPTS value (e.g "--certs-dir /opt/minio/certs").
  #
  # The following explicitly sets the MinIO Console listen address to
  # port 9001 on all network interfaces. The default behavior is dynamic
  # port selection.
  MINIO_OPTS="--console-address :9001"

  # Set the root username. This user has unrestricted permissions to
  # perform S3 and administrative API operations on any resource in the
  # deployment.
  #
  # Defer to your organizations requirements for superadmin user name.
  MINIO_ROOT_USER=minioadmin

  # Set the root password
  #
  # Use a long, random, unique string that meets your organizations
  # requirements for passwords.
  MINIO_ROOT_PASSWORD=minio-secret-key-CHANGE-ME
  ```

- TLS configuration
  MinIO (systemd-managed server process) enables TLS automatically upon detecting a valid x509 certificate (.crt) and private key (.key) in the `/home/minio-user/.minio/certs` directory.
  - Create `/home/minio-user` home directory of minio user account __used by minio.service configuration__
  - Create `/home/minio-user/.minio/certs` and `/home/minio-user/.minio/certs/CAs` sub-folders
  - Add TLS certificates (custom domain public certificates including intermediare certificates as __public.crt__, and custom domain private key as __private.key__ file) in `/home/minio-user/.minio/certs` sub-folder according to the type of MiniIO deployment reached:
    - https://cybnity.tech (__default domain__ TLS certificates): files in ${HOME}/.minio/certs sub-directory
    - https://minio1.cybnity.tech (specific sub-folder per SAN domain when dedicated certificates shall be used  per hostname with multiple Subject Alternative Names revealing hostname to any client which inspects the server certificate): files in ${HOME}/.minio/certs/__minio1.cybnity.tech__ sub-directory (sub-folder name is equals to MiniIO server hostname)
      - To activate TLS access from __cybsup01.cybnity.tech__ url :
        - Create sub-folder named `/home/minio-user/.minio/certs/cybsup01-cybnity.tech` dedicated to SAN supported sub-domain (server hostname)
        - Copy custom domain certificate and private key files into the sub-folder (that will be used by MiniIO web server for TLS session relative to url path)
  - Add CA certificates into the `/home/minio-user/.minio/certs/CAs` sub-folder (self-signed, internal, private certificates, and public CAs with intermediate certificates)
  - Assign ownership and rights of all `/home/minio-user` contents via `chown -R minio-user:minio-user /home/minio-user` to minio-user account

    > Place TLS and CA certificates in the __/home/mini-user/.minio/__ sub-directories of each MinIO hosts when server or client uses certificates signed by an unknown Certificate Authority (self-signed or internal CA). Else MinIO rejects invalid certificates (untrusted, expired or malformed)

- Run the MinIO server process via command:
```
sudo systemctl start minio.service
```

- Check that MiniIO service is online and functional via command:
```
sudo systemctl status minio.service
journalctl -f -u minio.service
```

- Enable automatic start of the process via command:
```
sudo systemctl enable minio.service
```

- Verify access to web console over `https://cybsup01.cybnity.tech:9001` and that open session is valide TLS

### External storage
Define storage provider for persisting the application settings and configured pipelines.

Select [MinIO](https://spinnaker.io/docs/setup/install/storage/minio/) S3-compatible object store that is locally hosted.

From MinIO web console, create an access key reusable by other application to integrate with MinIO storage solution.

## Continuous Delivery Tool (ArgoCD)
ArgoCD installation procedure is based on the Helm tool (see [Argo-Helm documentation](https://github.com/argoproj/argo-helm/tree/main/charts/argo-cd) for more detail about parameters)

### Application deployment configuration
- When hostname is not provided by network DNS server, add DNS entry for DNS hostname mapping dedicated to ArgoCD global domain name into the `/etc/hosts` file:
  ```
  # ArgoCD hostnames configuration
  192.168.60.18 cd.cybnity.tech
  ```
- Creation of a __values.yaml__ file for configuration of the Argo CD deployment to execute including:
```
global:
  domain: cd.cybnity.tech
server:
  ingress:
    enabled: true
    # Plug to RKE2 nginx common domain names entrypoints proxy
    ingressClassName: nginx
    annotations:
      cert-manager.io/cluster-issuer: "trust-cybnity-tech-issuer"
      nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
    tls: true
```
### Application installation
- Add repository via command: `helm repo add argo https://argoproj.github.io/argo-helm`
- Execute ArgoCD installation via command: `helm install -f values.yaml argocd argo/argo-cd`
- Install Argo CD CLI (see [CLI installation doc](https://argo-cd.readthedocs.io/en/stable/cli_installation/#download-latest-version))
  - Update admin default password as defined by Chapter 4
  - Remove default initial password secret object
- Verify started Argo CD application via web browser connection from https://cd.cybnity.tech, with __admin__ default account and default password (saved in __argocd-initial-admin-secret__ Secret object)

#
[Back To Home](CYDEL01.md)
