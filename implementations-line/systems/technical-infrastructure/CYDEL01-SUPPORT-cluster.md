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

See [RKE High Availability documentation](https://docs.rke2.io/install/ha).

See [guide for Rancher cluster setup](https://ranchermanager.docs.rancher.com/how-to-guides/new-user-guides/kubernetes-cluster-setup/rke2-for-rancher).

The global installation process between nodes followed is:
1. RKE2 Server/Master node installation


# VIRTUALIZATION NODE INSTALLATION
These tasks shall be executed on each cluster node.

## Helm installation
- Install Helm stable version via command:
```
  sudo snap install helm --classic
```

## RKE2 Kubernetes distribution installation
- Switch to root user via command: `su root`

### Controlled location of RKE2 installation
- Create tar installation directory via command: `sudo mkdir /opt/rke2`
- As root user, add environment variables (into `/etc/environment` file) used by the RKE2 script installation execution:
```
INSTALL_RKE2_METHOD="tar"
INSTALL_RKE2_CHANNEL="stable"
INSTALL_RKE2_TYPE="server"
INSTALL_RKE2_TAR_PREFIX="/opt/rke2"
```
- Reload environment file to activate changed file, via command:
```
  set -a; . /etc/environment; set +a;
```
- Check the defined static/permanent environment variables controlling RKE2 installation via command: `printenv`

### RKE2 installation
- Execute RKE2 installation script via command:
```
  sudo curl -sfL https://get.rke2.io | sh -
```
- Add permanent path to rke2 binary (`/opt/rke2/bin`) with adding into __$PATH__ variable of file `/etc/environment`
- Add __KUBECONFIG__ environment variable:
```
  KUBECONFIG="/etc/rancher/rke2/rke2.yaml"
```
- Reload environment file to activate changed file, via command:
```
  set -a; . /etc/environment; set +a;
```
- Create `/etc/rancher/rke2/` folder for definition of custom configuration read during server launch
- Create the RKE2 config file at `/etc/rancher/rke2/config.yaml` (see [help doc](https://docs.rke2.io/install/configuration)) including random 16 character token value, FQDN server names of SUPPORT cluster, automatic backup snapshot scheduling...:
```
  token: support-cluster-shared-secret-token-value
  write-kubeconfig-mode: "0644"
  tls-san:
    - sup1.cybnity.tech
    - sup2.cybnity.tech
    - sup3.cybnity.tech
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

> [!IMPORTANT]
> __On servers other than first Master node__
The first cluster server node establishes the secret token that other server or agent nodes will register with when connecting to the cluster.
Add additional __server__ property to the config.yaml file as `server: https://sup1.cybnity.tech:9345`

> [!NOTE]
> tls-san item can be an ip external public ip, a server hostname, a FQDNS, a Load-Balancer ip address, or a Load-Balancer dns domain.

- Read generated node token which could be equals to origignal value, or new generated value in case of cluster reset via command:
```
# Get and check the node-token generated on primary server
cat /var/lib/rancher/rke2/server/node-token

# Replace token value into config.yaml with primary node's node-token full value
```
- Change the permission allowed to the Kubernetes configuration files to minimise accessibility to other users via command:
```
sudo chmod 600 /etc/rancher/rke2/*.yaml
```
- Enable (symbolic link creation) and start each node's rke2-server service (from sup1 to sup3 server node) via commands:
```
  sudo systemctl enable rke2-server.service
  sudo systemctl start rke2-server.service

  # Check run status
  systemctl status rke2-server

  # Show logs
  journalctl -u rke2-server.service
```
- Control that cluster is functional (and each node is running control-plane, etcd and master roles) via command: `/var/lib/rancher/rke2/bin/kubectl get nodes --kubeconfig /etc/rancher/rke2/rke2.yaml`
- Test and check the health of the cluster pods via command: `/var/lib/rancher/rke2/bin/kubectl --kubeconfig /etc/rancher/rke2/rke2.yaml get pods --all-namespaces`
- Create symbolic links to RKE2 tools (kubectl, kubelet, crictl, ctr) via commands:
```
  sudo ln -s /var/lib/rancher/rke2/bin/kubectl /usr/local/bin/kubectl
  sudo ln -s /var/lib/rancher/rke2/bin/kubelet /usr/local/bin/kubelet
  sudo ln -s /var/lib/rancher/rke2/bin/crictl /usr/local/bin/crictl
  sudo ln -s /var/lib/rancher/rke2/bin/ctr /usr/local/bin/ctr
```
- Test symbolic link via command:
```
  # Check information about cluster nodes
  kubectl get nodes -o wide
```
- As default user account, create symbolic link to the created kubeconfig file (rke2.yaml default cluster configuration file) via commands:
```
  mkdir -p ~/.kube
  sudo ln -s /etc/rancher/rke2/rke2.yaml ~/.kube/config
```
- Copy __rke2.yaml__ file (allowing cluster [access from outside](https://docs.rke2.io/cluster_access documentation)) in other machine that would like to access and connect to RKE2 instance executed on the server
- CIS Hardening via [Kernel parameters](https://docs.rke2.io/security/hardening_guide#kernel-parameters) definition:
  - When `/usr/local/share/rke2/rke2-cis-sysctl.conf` is not existing, create it:
  ```
    vm.panic_on_oom=0
    vm.overcommit_memory=1
    kernel.panic=10
    kernel.panic_on_oops=1
  ```

  - Make copy on shared directory as rke2 conf
  ```
    sudo cp -f /usr/local/share/rke2/rke2-cis-sysctl.conf /etc/sysctl.d/60-rke2-cis.conf

    # Apply variables to Kernel
    sudo systemctl restart systemd-sysctl

    # Check restarted systemd
    sudo systemctl status systemd-sysctl
  ```
- Optional containerd registry
  - If need, create `registries.yaml` into `/etc/rancher/rke2/` (see [documentation](https://docs.rke2.io/install/private_registry)) usable by the server (e.g cybnity docker registry) including Personal Access Token used for private registries authentication

## Cluster configuration file backup
When you installed RKE2 on each Rancher server node, a kubeconfig file was created on the node at `/etc/rancher/rke2/rke2.yaml`.

__This file contains credentials for full access to the cluster__, and it should be saved in a secure location (e.g backup server). See [HA Rancher documentation](https://ranchermanager.docs.rancher.com/how-to-guides/new-user-guides/kubernetes-cluster-setup/rke2-for-rancher).

### Cluster remote access
Make a copy of the `/etc/rancher/rke2/rke2.yaml` including access certificate data, accessible to K8S client or remote management tool.

Edit file and change server property with FQDNS of load-balancer deployed in front of the SUPPORT cluster:
```
  server: [LOAD-BALANCER-DNS]:6443 # Edit this line
```

## RKE2 cluster RESET & cleanup
When cluster restart is in failure about etc DB problem (e.g consequently to config.yaml change), that need to reset the cluster:
- Clean cluster nodes via commands:
```
  # Stop any RKE2 process
  sudo rke2-killall.sh

  # DB cleanup
  sudo rm -rf /var/lib/rancher/rke2/server/db

  # Start RKE2 server
  sudo systemctl start rke2-server.service

  # Get and check equals node-token generated OF PRIMARY SERVER
  cat /var/lib/rancher/rke2/server/node-token
  cat /var/lib/rancher/rke2/server/token
```
- Replace token into __config.yaml__ in each other node with current value of primary server (complet value of token file)
- Restart each node

## Kubernetes resources
### Dynamic storage provisioning installation
For allow dynamic provisioning  (e.g usable by Rancher application), a storage class can be defined.

# INFRASTRUCTURE SERVICES

## Storage
### Applications default storage area
Definition of dedicated storage area on server, for applications deployed into the cluster:
- Create a sub-directory of `/srv` dedicated to the Kubernetes resources (e.g Persistent Volumes) via command: `sudo mkdir -p /srv/k8s/data`
- __From root $HOME directory__, create a symbolic link simplying the identification of K8S data storage base path via command:
```
  # Allow direct access to RKE2 dedicated disk area reserved for K8S data (e.g under data folder, for PersistentVolumes creation)
  sudo ln -s /srv/k8s/data .kube/data
```

## Monitoring & Logging

## Networking

### Container Network Interface (CNI)
Canal solution is deployed as CNI plugin.

### External FQDN visibility
By default, pods deployed into the cluster can't reach external server based on DNS (e.g Internet server name; external network server based on a FQDN and/or dns hostname).

When existing DNS server allowing external access to a cluster server node via hostname.domain_name (e.g sup1.cybnity.tech,, sup2.cybnity.tech, sup3.cybnity.tech), the pods deployed into the cluster can find any other external resource from  their FQDNs.

When none external DNS server is managing the server hostname and domain identification, a CoreDNS configuration file can be created allowing visibility of external machines (e.g Support cluster machine from the DEV cluster isolated network), and extending the default coredns config file automatically created by the Support server during the RKE2 dynamic agent installation.

## Security
## Time
Network Time Protocol (NTP) package shall be installed. This prevents errors with certificate validation that can occur when the time is not synchronized between the client and server. Timedate is by default installed on Ubuntu in place of previous ntpd tool, check that autosync of timezone from Internet is active via command:
```
  # Check autosync time
  timedatectl
```
  - If ntpd is need, install it via command:
  ```
    sudo apt install ntp
  ```

## Cluster Nodes Availability Plan
Automatic poweroff and restart of cluster node can be managed via custom scheduling planified between each cluster node (for 24/24 availability security, a minimum of 2 nodes shall be always be in active state to ensure etc database sync).

### 8/24 hr - 3/7 days Availability Stop Plan
- __Servers Scheduling Stop Plan__ (controlled by Linux crontab service)
|Period|Task Time|Server Node|Comment            |Residual Accepted Risk|
|:--   |:--      |:--        |:--                |:--                   |
|Daily |20:30    |sup3       |sup1, sup2 active  |Safe vailability      |
|Daily |20:40    |sup2       |sup1 active        |Risk of unavailability|
|Daily |20:50    |sup1       |None active cluster|Interrupted services  |

#### On each server
Defined crontab directives on each server in a safe way (with wait of existing process secure end before make the stop) via power off:
- Open and add command into crontab via command: `sudo crontab -e`
- Add line in file and save:
```
# Stop and poweroff server in a secure way (shutdown -P) each day at 20:30:00
30 20 * * * shutdown -P
```
- Crontab plan checking via command: `sudo crontab -l`

### 8/24 hr - 3/7 days Availability Start Plan
Controlled by BIOS setup, or via crontab on permanent available server (e.g ha.cybnity.tech).

- __Servers Scheduling Start Plan__
|Period            |Task Time|Server Node     |Comment                         |Residual Accepted Risk|
|:--               |:--      |:--             |:--                             |:--                   |
|Thursday, Friday  |08:45    |sup1            |sup1 active                     |Risk of unavailability|
|Thursday, Friday  |08:48    |sup2            |sup1, sup2 active               |Safe availability     |
|Thursday, Friday  |08:51    |sup3            |sup1, sup2, sup3 active         |Safe availability     |
|Manual Wake-On-Lan|         |sup1, sup2, sup3|Ordered WOL from HA proxy server|                      |

### On server managing start plan
Define WOL script executing WOL call (e.g from ha.cybnity.tech server) via `/usr/local/bin/CYDEL_support_cluster_start.sh` executable script):

- Edit common WOL call and server availability script including:
```
  #!/bin/bash
  # Automatic server start via Wake-On-Lan magic packet send and check of server status
  # Argument os script call:
  # - $1 server logical name (e.g sup1.cybnity.tech)
  # - $2 mac address number (e.g 6c:4b:90:16:4e:4c) recipient of magic packet

  VAR=`ping -s 1 -c 2 $1 > /dev/null; echo $?`
  if [ $VAR -eq 0 ];then
  echo -e "$1 is UP as on $(date)"
  elif [ $VAR -eq 1 ];then
  wakeonlan $2 | echo "$1 not turned on. WOL packet sent at $(date +%H:%M)"
  sleep 3m | echo "Waiting 3 Minutes"
  PING=`ping -s 1 -c 4 $1 > /dev/null; echo $?`
  if [ $PING -eq 0 ];then
  echo "$1 is UP as on $(date +%H:%M)"
  else
  echo "$1 not turned on - Please Check Network Connections"
  fi
  fi
```
- Edit automated WOL calls script including:
```
  #!/bin/bash
  # Automation script for ordered start of SUPPORT cluster servers

  # Check and call WOL on sup1.cybnity.tech
  /usr/local/bin/server_wol_start.sh sup1.cybnity.tech 6c:4b:90:16:4e:4c

  # Check and call WOL on sup2.cybnity.tech
  /usr/local/bin/server_wol_start.sh sup2.cybnity.tech 6c:4b:90:19:0a:81

  # Check and call WOL on sup3.cybnity.tech
  /usr/local/bin/server_wol_start.sh sup3.cybnity.tech 6c:4b:90:19:21:22
```
- Make each script executable via command: `sudo chmod +x /usr/local/bin/CYDEL_support_cluster_start.sh`
- Add crontab line ensuring scheduling start plan for the periods when Support cluster WOLs shall be executed:
```
# Start SUPPORT servers over Wake-On-Lan script call every Thursday and Friday at 08:45:00
45 8 * * 4,5 root bash /usr/local/bin/CYDEL_support_cluster_start.sh
```

### Rancher Backup
Automated backup solution ensuring auto-save of Rancher instance into a scheduled approach is implemented by Rancher Backup service (to file versions allowing restoration in case of Rancher container disaster).

# APPLICATION SERVICES
## Kubernetes clusters management solution (Rancher)
Rancher application is deployed for management of any K8S cluster of CYDEL01 infrastructure.

See [Rancher technical documentation](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster) for help.
### On each cluster node
- Create a folder dedicated to the Rancher audit logs via command: `sudo mkdir -p /srv/k8s/audit/rancher`

### On primary cluster node
> [!NOTE]
> Any kubernetes comman executed on the primary node will generated configuration and application elements synchronizing automatically to other cluster nodes.

- Add helm charts to node repository list via command:
```
  sudo helm repo add rancher-stable https://releases.rancher.com/server-charts/stable
```
- Namespace creation into the support cluster via command:
```
sudo kubectl create namespace cattle-system
```
- [Delete potential old version created certificates](https://ranchermanager.docs.rancher.com/v2.6/troubleshooting/other-troubleshooting-tips/expired-webhook-certificate-rotation) that will expire after one year via commands:
```
  kubectl delete secret -n cattle-system cattle-webhook-tls
  kubectl delete mutatingwebhookconfigurations.admissionregistration.k8s.io --ignore-not-found=true rancher.cattle.io
  kubectl delete pod -n cattle-system -l app=rancher-webhook
```

[Installation of Rancher](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/install-upgrade-on-a-kubernetes-cluster#5-install-rancher-with-helm-and-your-chosen-certificate-option) over Helm with Rancher-generated (when ingress.tls.source parameter is not defined) certificates. See [Rancher Helm chart options doc](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/installation-references/helm-chart-options) regarding the common options.

- Create the certificate and key files usable by Rancher installer from the custom domain certificate and key files:
    tls.crt = custom domain server certificate file followed by any intermediate certificate(s)
    tls.key = custom domain certificate private key
- Check Common Name and Subject Alternative Names on the custom certificate:
  ```
  # Show subject recognized in certificate
  openssl x509 -noout -subject -in tls.crt
  ```
- Add TLS secrets (see [Rancher TLS Secrets documentation](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/resources/add-tls-secrets)) as secret via command:
  ```
  kubectl -n cattle-system create secret tls tls-rancher-ingress --cert=tls.crt --key=tls.key
  ```
- Create cacerts.pem file including the custom domain CA trust chain (containing only root CA certificate or certificate chain from private CA), and create `tls-ca` secret via:
  ```
  kubectl -n cattle-system create secret generic tls-ca --from-file=cacerts.pem
  ```
- For Rancher installation using default "system-store", allowing Rancher agent nodes trust any certificate generated by a public Certificate Authority contained in the Operating System's trust store, and including those signed by authorities:
  ```
  sudo helm install rancher rancher-stable/rancher \
    --namespace cattle-system \
    --set hostname=rancher.cybnity.tech \
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
