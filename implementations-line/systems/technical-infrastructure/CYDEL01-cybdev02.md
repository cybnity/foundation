## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary development server.

The DEV cluster is dedicated to run of a version of CYBNITY software components which is not already released (e.g staging version, feature branch version).

The services provided by the CYBDEV02 server are focus on:
- primary server of the Dev Environment cluster
- detection of sub-resources sizing required by any change of CYBNITY applications
- unit test and/or integration development activities (e.g link to other external systems that integration development is in progress)

# HARDWARE LAYER
Current hardware configuration is based on server:
- server type: Hewlett-Packard Z640
- CPU: 2 x Intel Xeon E5-2673 v4, 20 cores
- RAM: 256 GB
- Hard disks:
  - NVMe SSD 512 GB (Operation System & Linux based applications)
  - 1 SATA Disk 1 TB (K8S applications data)
- ??NVIDIA Quadro P4000 graphic card, 8Go
- 1 NIC 1Gbps: used for Wake-On-Lan (remote start of server)
- 2 x NIC 10Gbps:
  - 1 dedicated ot Kubernetes HA clustering
  - 1 not used
  
## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server into a __"ready for virtualization installation"__ state.

Current prepared server configuration is:
- hostname: __cybdev02__

# VIRTUALIZATION LAYER
RKE2 virtualization system is implemented as Kubernetes layer hosting the CYBNITY staging versions deployed into a __Dev cluster__.

## Docker
- Docker engine shall be installed on server as documented on [Docker for Ubuntu documentation](https://docs.docker.com/engine/install/ubuntu/).

- Execute Linux post-installation steps for Docker Engine as [documented](https://docs.docker.com/engine/install/linux-postinstall/) allowing Docker usage as a non-root user.

- Add default CYBDEV02 user account into the docker group, and/or member roles referenced into in the DEV Cluster configuration under Rancher.

- Configure Docker to start on boot with systemd

## Server ports
Open traffic on server allowing any discussion between CYBDEV02 and CYBSUP02 like documented on [RKE doc](https://rke.docs.rancher.com/os#ports).

## RKE2 Kubernetes distribution

### Private Certificates
Get CYBNITY domain certificates bundle (including CA Root, intermediates crt and key files) usable for DEV cluster (cybdev02.cybnity.tech as Subject Alternative Name).

#### Update Ubuntu SSL certificate bundles
Add certificates (private domain, intermediate, CA root) to certificated store of OS:

- Copy each crt file of the domain bundle into the `/usr/local/share/ca-certificates` folder (e.g private domain crt file named STAR_cybnity_tech.crt)
- Update the Ubuntu certificates store via command `sudo update-ca-certificates` (auto-upgrade of `/etc/ca-certificates.conf` file)

To add future certificates to store, just copy the file (in PEM format) into the the `/user/local/share/ca-certificates` folder and re-execute the store update command.

When Trust-Manager kubernetes application is installed on K8S, this OS certificates package is automatically taken as reference about supported CA certificates.

- Add domain private key file to Ubuntu OS via copy of domain private key file into `/etc/ssl/private` folder.

#### Update RKE2 certificates usabled for cluster
If previous RKE2 version need to be uninstalled before defining the CA root, execute uninstall via command `/opt/rke2/bin/rke2-uninstall.sh`

See [RKE2 security certificates](https://docs.rke2.io/security/certificates) documentation for help.
- Create `/var/lib/rancher/rke2/server/tls` folder and copy into:
  - Custom domain certificate (e.g STAR_cybnity_tech.crt defining the SSL certificate for domain and/or sub-domains) as `root-ca.pem` renamed file
  - Private key of the custom domain (e.g private.key file of custom domain SSL certificate) as `root-ca.key` renamed file.
- Generate custom CA certs and keys via command `curl -sL https://github.com/k3s-io/k3s/raw/master/contrib/util/generate-custom-ca-certs.sh | PRODUCT=rke2 bash -` and check text shown regarding the database updates applied with success (e.g new entries made) about CA certificate generation completed.
  - As recommended by command result, store in a secure area the root and intermediate certificate files (.pem, .crt, .key) and remove them from __/var/lib/rancher/rke2/server/tls__ folder.
- Backup a copy of current __service-account signing key__ considerated like current via `cp /var/lib/rancher/rke2/server/tls/service.key /backup-folder`, to avoid token lost during future custom CA certificate rotation.

### Agent installation
RKE2 agent node is automatically installed on the server via DEV Cluster managed on Rancher (CYBSUP01 server).
From URL call relative to created DEV cluster, the installation procedure is automatically managing the deployment of all RKE2 component required for runtime.

- Check the started rke2-agent service via commands:
```
systemctl status rancher-system-agent

# show logs in real-time
journalctl -u rancher-system-agent -f
```

# INFRASTRUCTURE SERVICES

## Storage

## Monitoring & Logging

## Networking
- Update of __/etc/hosts__ file check (e.g DHCP mode from network system) or static ip address.
  Ensure your system can resolve its hostname by updating the /etc/hosts file with the IP address and the new hostname via command:
```
  sudo vi /etc/hosts

  # Add a line per server-desired-hostname (FQDN)
  127.0.1.1 cybdev02

  # Local K8s application extended hostnames
  192.168.30.13 cybdev02.cybnity.tech
  192.168.60.18 cybsup01.cybnity.tech

  # Cluster exposed API Endpoint FQDN (configuration managed in Rancher)
  192.168.30.13 dev.cybnity.tech

```

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

## Kubernetes tools usage
When a K8S client is used, the automatic installed RKE2 CLI is hosted in __/var/lib/rancher/rke2/bin__ folder, and the kubeconfig location (automatically installed in __/etc/rancher/rke2__ folder) shall be identified during any CLI command execution.

For example:
```
# Show started nodes
/var/lib/rancher/rke2/bin/kubectl --kubeconfig=/etc/rancher/rke2/rke2.yaml get nodes

# Show deployed pods
/var/lib/rancher/rke2/bin/kubectl --kubeconfig=/etc/rancher/rke2/rke2.yaml get nodes -A

# Show errors relative to pods instantiation
/var/lib/rancher/rke2/bin/kubectl --kubeconfig=/etc/rancher/rke2/rke2.yaml get pods -v=10
```

# APPLICATION SERVICES

#
[Back To Home](CYDEL01.md)
