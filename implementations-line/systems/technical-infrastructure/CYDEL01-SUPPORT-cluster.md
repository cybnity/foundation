## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the Support cluster servers.

The services provided by each server (SUPPORT cluster node) are focus on:
- Primary or secondary providing of the Support Environment supporting all the CYDEL version's tools
- Management of other environments (e.g other Kubernetes clusters of CYDEL01)
- Hosting of Continuous Delivery tools and applications managing the detection and installation of CYBNITY software suite versions into other K8S clusters
- Management of application systems deployment life cycle (e.g continuous delivery chain) on other K8S cluster (e.g dev, test...)

# HARDWARE LAYER
Current hardware configuration of each cluster node is based on a Lenovo ThinkCentre M710q:
- CPU: Intel i5 6th Gen, 4 cores
- RAM: 32 GB
- Hard disks: NVMe SSD 256 GB (Operation System & Linux based applications, K8S applications data)
- 1 NIC 1Gbps

## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server into a __"ready for virtualization installation"__ state.

Current prepared server configurations are:
- FQDN: __sup1.cybnity.tech__
- FQDN: __sup2.cybnity.tech__
- FQDN: __sup3.cybnity.tech__

# VIRTUALIZATION LAYER
Each RKE2 virtualization system is implemented as Kubernetes layer hosting the CYBNITY support applications deployed into a __Support cluster__.

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
  sudo curl -sfL https://get.rke2.io |  sh -
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

# INFRASTRUCTURE SERVICES

## Storage

## Monitoring & Logging

## Networking

### Container Network Interface (CNI)
Canal solution is deployed as CNI plugin.

### External FQDN visibility
By default, pods deployed into the cluster can't reach external server based on DNS (e.g Internet server name; external network server based on a FQDN and/or dns hostname).

Creation of a CoreDNS configuration file allowing visibility of external machines (e.g Support cluster machine from the DEV cluster isolated network), extending the default coredns config file automatically created by the Support server during the RKE2 dynamic agent installation

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

Find here little bit informations for help to identify them about their dissemination.

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
        - cybdev01.cybnity.tech
        - cybdev02.cybnity.tech
        - sup.cybnity.tech
        - dev.cybnity.tech
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
