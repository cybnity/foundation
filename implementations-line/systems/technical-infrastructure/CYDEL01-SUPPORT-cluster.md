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
2. RKE2 second server node installation with equals configuration complemented of primary server url (RKE2 conf)
3. RKE2 third server node installation with equals configuration complemented of primary server url (RKE2 conf)

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
> tls-san item can be an ip external public ip, a server hostname, a FQDN, a Load-Balancer ip address, or a Load-Balancer dns domain.

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

Edit file and change server property with FQDN of load-balancer deployed in front of the SUPPORT cluster:
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

## Cluster Nodes Availability Plan
Automatic poweroff and restart of cluster node can be managed via custom scheduling planified between each cluster node (for 24/24 availability security, a minimum of 2 nodes shall be always be in active state to ensure etc database sync).

### 8/24 hr - 2/7 days Availability Stop Plan
- __Servers Scheduling Stop Plan__ (controlled by Linux crontab service)

|Period|Task Time|Server Node|Comment            |Residual Accepted Risk|
|:-----|:--------|:----------|:-- ---------------|:---------------------|
|Daily |20:30    |sup3       |sup1, sup2 active  |Safe availability     |
|Daily |20:40    |sup2       |sup1 active        |Cluster unavailability|
|Daily |20:50    |sup1       |None active cluster|Interrupted services  |

#### On each SUPPORT cluster server
Defined crontab directives on each server in a safe way (with wait of existing process secure end before make the stop) via power off:
- Open and add command into crontab via command: `sudo crontab -e`
- Add line in file and save:
```
# Stop and poweroff server in a secure way (shutdown -P) each day at 20:30:00
30 20 * * * shutdown -P
```
- Crontab plan checking via command: `sudo crontab -l`

### 8/24 hr - 2/7 days Availability Start Plan
Controlled by BIOS setup, or via crontab on permanent available server (e.g sup1.cybnity.tech, sup2.cybnity.tech, sup3.cybnity.tech).

- __Servers Scheduling Start Plan__

|Period            |Task Time|Server Node     |Comment                         |Residual Accepted Risk|
|:-----------------|:--------|:---------------|:-------------------------------|:---------------------|
|Thursday, Friday  |08:45    |sup1            |sup1 active                     |Cluster unavailability|
|Thursday, Friday  |08:48    |sup2            |sup1, sup2 active               |Safe availability     |
|Thursday, Friday  |08:51    |sup3            |sup1, sup2, sup3 active         |Safe availability     |
|Manual Wake-On-Lan|         |sup1, sup2, sup3|Ordered WOL from HA proxy server|                      |

#### On server managing start plan (HA server)
Define WOL script executing WOL call (e.g by ha.cybnity.tech server) via `/usr/local/bin/CYDEL_support_cluster_start.sh` executable script) and crontab orchestration. See [CYDEL01-HA documentation](CYDEL01-HA.md) for more detail.

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

- Create the certificate and key files ([mandatory Secret resource names](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/resources/add-tls-secrets)) for K8S secret resource creation) usable by Rancher installer from the custom domain certificate and key files:
    __tls.crt__: custom domain server certificate file followed by any intermediate certificate(s)
    __tls.key__: custom domain certificate private key
- Check Common Name and Subject Alternative Names on the custom certificate:
```
  # Show subject recognized in certificate
  openssl x509 -noout -subject -in tls.crt
```
- Add TLS secrets as secret via command:
```
  kubectl -n cattle-system create secret tls tls-rancher-ingress --cert=tls.crt --key=tls.key
```
- Create __cacerts.pem__ file including the custom domain CA trust chain (containing only root CA certificate or certificate chain from private CA), and create __tls-ca__ secret via:
```
  kubectl -n cattle-system create secret generic tls-ca --from-file=cacerts.pem
```
- Check that TLS resources are successfully available in the RKE2 cluster via command: `kubectl -n cattle-system create secret generic tls-ca --from-file=cacerts.pem`
- Copy additional custom intermediary certificates only (trust chain without custom domain certificate) in pem format into a file named __ca-additional.pem__ and use `kubectl` to create the __tls-ca-additional__ secret in the __cattle-system__ namespace via command:
```
  kubectl -n cattle-system create secret generic tls-ca-additional --from-file=ca-additional.pem=./ca-additional.pem
```
- For Rancher installation using default __strict mode__ (Rancher's agents only trust certificates generated by the Certificate Authority contained in the __cacerts__ setting. When the value is set to system-store, Rancher's agents trust any certificate generated by a public Certificate Authority contained in the operating system's trust store; see [Helm Chart options doc](https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/installation-references/helm-chart-options#advanced-options)):
  - __tls__ property value is equals to `external` when Load-Balancer is existing in front of SUPPORT cluster (else `ingress` value shall be set)
  ```
  sudo helm install rancher rancher-stable/rancher \
    --namespace cattle-system \
    --set hostname=rancher.cybnity.tech \
    --set bootstrapPassword=cybnity \
    --set ingress.tls.source=secret \
    --set privateCA=true \
    --set agentTLSMode=strict \
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
- Verify that Rancher server is successfully deployed via commands:
```
  kubectl get pods -n cattle-system
  kubectl -n cattle-system rollout status deploy/rancher

  # Check on each cluster node
  kubectl -n cattle-system get deploy rancher
```
- In case of deployment problem or pod in intermediary status, see description of pod via command:
```
  kubectl describe pod rancher-978c8d76d-jgr98 -n cattle-system
```
- From other machine, test and show HTTP header of forwarded request to Rancher url via command: `
```
  # Show header ouput
  curl -Lvso /dev/null https://rancher.cybnity.tech

  # Show detail of SSL process results
  curl -v https://rancher.cybnity.tech`
```
- Open browser on Rancher web UI via commands:
```
# See default Rancher web app password for admin account
kubectl get secret --namespace cattle-system bootstrap-secret -o go-template='{{.data.bootstrapPassword|base64decode}}{{ "\n" }}'

# See Rancher setup url
echo https://rancher.cybnity.tech/dashboard/?setup=$(kubectl get secret --namespace cattle-system bootstrap-secret -o go-template='{{.data.bootstrapPassword|base64decode}}')
```

#### Cluster shared issuer (TLS certificate for any *.cybnity.tech deployed application)
Types of shared elements managed in the Secret resources section of the SUPPORT cluster are:
- __tls-rancher-ingress__ including custom domain certificate and private key properties (e.g cybnity.tech custom domain)

- Creation of a Cluster Issuer for the applications (allow to issue certificates using CYBNITY custom domains certificate) defined in new manifest file stored in RKE2 static manifest of SUPPORT cluster primary server:
  - Creation of new `rke2-trust-cybnity-tech-issuer.yaml` (file into `/var/lib/rancher/rke2/server/manifests/` folder for automatic binding by RKE2) including:
  ```
    # SHARED CLUSTER ISSUER
    # Allow applications deployed into the SUPPORT cluster, to issue certificates using the CYBNITY.TECH custom domains certificate (Secret resource)
    apiVersion: cert-manager.io/v1
    kind: ClusterIssuer
    metadata:
      name: trust-cybnity-tech-issuer
    spec:
      ca:
        secretName: tls-rancher-ingress # Name of the custom domains certificate stored as cluster shared Secret
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
      - "*.cybnity.tech"
  ```
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
            sc1["tls-rancher-ingress"]
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

## CYBNITY software repository
- Add complementary helm charts repository (e.g allowing deployment of CYBNITY applications) to each cluster node's repository list via commands:
```
  sudo helm repo add cybnity https://cybnity.github.io/iac-helm-charts --force-update
```

## Continuous Delivery Tool (ArgoCD)
ArgoCD installation procedure is based on the Helm tool (see [ArgoCD-Helm documentation](https://github.com/argoproj/argo-helm/tree/main/charts/argo-cd) for more detail about parameters)

### DNS configuration
Add DNS entry for `argocd.cybnity.tech` hostname (mapping dedicated to ArgoCD global domain name) into the DNS server configuration, that is set HA proxy as clusterized ArgoCD application unique endpoint.

Check that propagated hostname from DNS is active via command: `ping argocd.cybnity.tech`

### Application deployment configuration
Creation of a __argocd-values.yaml__ file for configuration of the Argo CD deployment to execute including:
```
# ArgoCD HA mode with autoscaling
redis-ha:
  enabled: true

controller:
  replicas: 1

server:
  autoscaling:
    enabled: true
    minReplicas: 2

repoServer:
  autoscaling:
    enabled: true
    minReplicas: 2

applicationSet:
  replicas: 2

# Ingress configuration in a multiple ingress domains (e.g many support cluster's deployed applications)
global:
  domain: argocd.cybnity.tech
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

### ArgoCD application installation
Execute application deployment from SUPPORT primary server (sup1.cybnity.tech) that will manage the auto-sync of other cluster nodes.

- Add Argo repository into Helm repository of the machine managing the ArgoCD deployment, via command: `sudo helm repo add argo https://argoproj.github.io/argo-helm`
- Execute ArgoCD installation via command: `sudo helm install -f argocd-values.yaml argocd argo/argo-cd`

#### Default configuration data
The initial password for the admin account is auto-generated and stored as clear text in the field password in a secret named argocd-initial-admin-secret in your Argo CD installation namespace.

From Rancher web UI, search __argocd-initial-admin-secret__ Secret automatically created during the application deployment, and copy its password value reusable for authentication with __admin__ default account from the ArgoCD web UI.

#### Application check
- Verify started Argo CD application via web browser connection from https://argocd.cybnity.tech with __admin__ default account and default password (saved in __argocd-initial-admin-secret__ Secret object)
- From __User Info__ section, update the default password of admin account for a new one
- Remove default initial password secret object

- On SUPPORT primary server, install Argo CD CLI last version from Curl tool (see [CLI installation doc](https://argo-cd.readthedocs.io/en/stable/cli_installation/#download-latest-version)) via commands:
```
  curl -sSL -o argocd-linux-amd64 https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64

  sudo install -m 555 argocd-linux-amd64 /usr/local/bin/argocd

  rm argocd-linux-amd64
```

#
[Back To Home](CYDEL01.md)
