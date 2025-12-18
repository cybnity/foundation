## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the High-Availability (HA) server exposing endpoints of K8S clusters (e.g Support cluster).

The services provided by the HA server are focus on:
- Load-balancing management of K8S clusters front endtpoint
- Scheduling tasks of clusters start/stop according to their availability plannings

# HARDWARE LAYER
Current hardware configuration of the node is based on a HP ProDesk 400 G3:
- CPU: Intel i5 7500T Gen, 4 cores
- RAM: 32 GB
- Hard disks: NVMe SSD 512 GB (Operation System & Linux based applications)
- 1 NIC 1Gbps

## BIOS
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server.

Current prepared server configurations is:
- FQDN: __ha.cybnity.tech__

# OPERATING SYSTEM LAYER

## System Backup
The automatic backup of essential system files and/or data folder which could need to be restored in case of system failure is managed via [RESTIC open source solution](https://restic.readthedocs.io/en/latest/index.html).

### Restic Installation
- Create restic configuration files folder into __/etc/restic__ folder via command: `sudo mkdir /etc/restic/ssh-keys`

- Create a dedicated server account onto remote server (e.g. FTP server) ensuring the backup files storage where Restic will push the backup snapshots

- Create an authentication private/public key pair (usable for future Restic SSH client authentications when executing SFTP connections to remote storage server) on the system allowing to manage authentication without usage of the FTP account interactive password mode via command: `sudo ssh-keygen`
  - Target storage of generated key pair file location shall be defined to __/etc/restic/ssh-keys/id_restic__
  - Passphrase entry does not need to be defined regarding generated private key (never shared/transmitted out of the server)
  - __id_restic__ (file containing the RSA private key when using SSH protocol version 1) and __id_restic.pub__ (file containing the RSA public key for authentication when you are using the SSH protocol version)

- Copy the generated public key into the remote server serving as storage server for snapshots
  - create a __.ssh__ folder into the remote server account home directory
  - create __authorized_keys__ file into the __.ssh__ folder
  - copy the __id_restic.pub__ public key file content into __.ssh/authorized_keys__ file into the remote server account home directory

- Install Restic tool on server via vommand: `sudo apt-get install restic`

### Restic Configuration
- Add environment variables into __/etc/environment__ that are required by Restic for remote server access via command: `sudo vi /etc/environment`
```
  RESTIC_PASSWORD="<<repository password>>"
  RESTIC_HOST="ha.cybnity.tech"

  ha123.?UPA73;
```

- Reload environment file to activate changed file, via command `export $(cat /etc/environment | xargs) && env`

- To avoid automatic close of SSH session by ftp server whe Restic is processing huge amounts of unchanged data, and sftp server connection via public key, add lines in __/etc/ssh/ssh_config__ file:
```
# SSH key and configuration for SFTP access without password into command line
# (e.g. nas.agnet host name shall be equals to <<nas server name or ip address>> targeted by Restic snapshot command)
Host restic-backup-host
    Hostname nas.agnet
    Port 22
    User <<an account login opened into the remote server receiving the backup snapshots>>
    PasswordAuthentication no
    IdentityFile /etc/restic/ssh-keys/id_restic
    IdentitiesOnly yes
    ServerAliveInterval 60
    ServerAliveCountMax 240
```

- Define files and/or folders which shall be excluded from each snapshot of the full system to be stored, into a configuration file named __restic-backup-excludes.txt__ (e.g. created into __/etc/restic__ folder) containing:
```
  # Exclude files or folder of RESTIC backup

  # Exclude all files and sub-directories from system potentially unmounted folders
  /dev
  /media
  /cdrom
  /mnt

  # virtual filesystems which reflect the state of the system and allow to change runtime parameters (never to backup)
  /proc
  /sys

  # dynamically created at boot (memory filesystem)
  /dev

  # other system folders
  /lost+found
  /run
  /lib
  /sbin
  /bin
  /tmp
  /swapfile

  # var backuped but except temp and logs
  /var/tmp
  /var/log
  /var/cache

  # optional boot
  /boot
```

### Initialization of backup repository in remote server
A repository dedicated to a full backup of the HA server is defined.

- Create the repository reserved into the FTP server for the system snapshots storage via command:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server init`

### Automation Scripts
- Create a backup server script file __/usr/local/bin/server_backup_start.sh__ :
```
  #!/bin/bash
  # Automation script for RESTIC backup of all the system based on exclusion configuration file
  #
  echo "SYSTEM BACKUP - Server snapshot started by RESTIC to NAS server"

  sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server backup --host ha.cybnity.tech --one-file-system / --exclude-file=/etc/restic/restic-backup-excludes.txt

  echo "Server snapshot saved into NAS server"

  echo "REPO CLEANING - Cleaning of previous snapshots older than one month"

  sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server forget --keep-within-weekly 1m --prune

  echo "Repository have been cleaning and only last weeklky snapshot are maintained available into the NAS server regarding the host ($RESTIC_HOST)"
```

- Make script as executable via command: `sudo chmod +x /usr/local/bin/server_backup_start.sh`

- Create symbolic link to script (allowing manual call from anywhere from command `sh startBackupServer`) via command: `sudo ln -s /usr/local/bin/server_backup_start.sh startBackupServer`

### Backup operations
Usage of Restic script and commands can be manually performed according to desired actions.

- To list previous existing snapshot versions (number based) stored into remote server:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server snapshots`

- To read contents of a snapshot version stored into the remote server:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server ls <<snapshot ID>>`

### Restoration operations
See [Restoring from backup documentation](https://restic.readthedocs.io/en/latest/050_restore.html).

- Restore a snapshot from its ID via command:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server restore <<snapshot ID>> --target <<destination folder>>`

- Restore __latest__ backup snapshot about specific host (e.g. __ha.agnet__) and path (e.g. __/__) via command:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server restore latest --target <<destination folder>> --path "/" --host ha.cybnity.tech`

### Deletion and cleaning operations
See [Removing backup snapshots documentation](https://restic.readthedocs.io/en/latest/060_forget.html).

- Remove a snapshot from its ID via command:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server forget <<snapshot ID>>`

- Additionally, it is required to remove data referenced by files in the deleted snapshot which is still stored in the repository. To cleanup unreferenced data, execute the __prune__ command according to:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server prune`

- Remove all previous snapshots and keep only latest weekly snaphots made within one month of the latest snapshot, and automatic prune directive execution via command:
`sudo restic -r sftp:restic-backup-host:restic-repo-ha.cybnity.tech-server forget --keep-within-weekly 1m --prune`

# INFRASTRUCTURE SERVICES

## Storage

## Monitoring & Logging

## Networking
### External FQDN visibility
Add __ha.cybnity.tech__ into DNS server configuration routing to the HA server.

## Security

## Server Availability Plan
Automatic poweroff and restart of server can be managed via custom scheduling.

### 8/24 hr - Daily Availability Stop Plan
- __Server Scheduling Stop Plan__ (controlled by Linux crontab service)

|Period|Task Time|Server Node|Comment            |Residual Accepted Risk|
|:-----|:--------|:----------|:------------------|:---------------------|
|Daily |21:00    |ha         |None active cluster|Interrupted services  |

Defined crontab directive on server in a safe way (with wait of existing process secure end before make the stop) via power off:
- Open and add command into crontab via command: `sudo crontab -e`
- Add line in file and save:
```
  # HA server auto-stop script every day at 21:00:00 (after SUPPORT cluster servers stopped)
  0 21 * * * shutdown -P
```
- Crontab plan checking via command: `sudo crontab -l`

### 8/24 hr - 2/7 days Availability Start Plan
Controlled by BIOS setup and/or Wake On-Lan remote call.

- __Server Scheduling Start Plan__

|Period            |Task Time|Server Node     |Comment                       |Residual Accepted Risk|
|:-----------------|:--------|:---------------|:-----------------------------|:---------------------|
|Thursday, Friday  |08:35    |ha              |Clusters load-balancing active|                      |
|Manual Wake-On-Lan|         |ha              |                              |                      |

- In server's BIOS setup (power management section), add start directive for poweron action performed each Thursday and Friday at 08:35:00.
> [!NOTE]
> Timezone is UTC into NIOS by default. For example, 07:35:00 value shall be set for alignment with Europe Paris timezone when HA server shall be started at 08:35:00 in France.

## Server Backup Plan
Automatic full system data backup and remote on remote support server (e.g. NAS support server) automatically managed over crontab.

### 2/7 days - Daily Backup Start Plan
- __Server Backup Plan__ (controlled by Linux crontab service)

|Period|Task Time|Server Node|Comment                                                    |Residual Accepted Risk                |
|:-----|:--------|:----------|:----------------------------------------------------------|:-------------------------------------|
|Daily |19:00    |ha         |Full system snaphot and storage to remote repository server|Deleted previous snapshots > 1 month  |

Defined crontab directive on server via Restic backup script:
- Open and add command into crontab via command: `sudo crontab -e`
- Add line in file and save:
```
  # DNS serber auto-backup via Restic process to QNAP Nas Server at
  # every Thursday and Friday (weekly day of NAS Server running between 08:35 and 21:00)
  0 19 * * 4,5 /usr/local/bin/server_backup_start.sh
```

# APPLICATION SERVICES
## Automation scripts
Any custom application script shall be stored into `/usr/local/bin`.

## CYDEL Clusters Availability Plans Management
Define WOL script executing WOL call via creation of a `/usr/local/bin/CYDEL_support_cluster_start.sh` executable script executing WOL call to clusters machines' network mac addresses.

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
- Edit each automated WOL calls script (e.g per SUP, DEV, QLA cluster) that shall include calls to common WOL script:
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
- Make each script executable via command: `sudo chmod +x /usr/local/bin/*.sh`

- Add crontab line (via command `sudo crontab -e`) ensuring scheduling start plan for the periods when clusters start plans via WOLs shall be executed:
```
  # Start SUPPORT servers over Wake-On-Lan script call every Thursday and Friday at 08:45:00
  45 8 * * 4,5 /usr/local/bin/CYDEL_support_cluster_start.sh

  # Start DEV server over Wake-On-Lan script call every Thursday and Friday at 09:00:00
  0 9 * * 4,5 /usr/local/bin/CYDEL_dev_cluster_start.sh
```

## NGINX installation
- Install NGINX open source version as documented by the [NGINX installation doc](https://docs.nginx.com/nginx/admin-guide/installing-nginx/installing-nginx-open-source/#installing-prebuilt-ubuntu-packages)
- Check started default NGINX service via command:
```
  sudo systemctl status nginx
```
- Add automatic restart of NGINX service (managed by daemon) via new file into `/etc/systemd/system/nginx.service.d/restart.conf` including:
```
  [Service]
  Restart=on-failure
  RestartSec=5s
```
- Reload systemd for the changes to take effect via command:
```
  sudo systemctl daemon-reload
```
- Check existing referenced restart.conf file as `Drop-In` property of __nginx.service__ via command:
```
  sudo systemctl status nginx
  # Check existing Drop-In value targeting the /etc/systemd/system/nginx.service.d restart.conf
```

### Load-balancing configuration for *.cybnity.tech clusters
By default, NGINX managed web sites zones are defined via configuration files stored according to:
- `/etc/nginx/nginx.conf`: main NGINX configuration file
- `/etc/nginx/sites-available/` folder: store potential configurations for virtual hosts. each fiel per distinct website actived or not
- `/etc/nginx/sites-enabled/` folder: contain symbolic links to the configurations of sites-available used by NGINX (only linked are active)

By default `/etc/nginx/conf.d` folder contains general configurations applied to all sites. Via __include__ directive, any conf file is automatically evaluated and integrated by the main NGINX configuration.

### NGINX commands supported:
- Check NGINX configuration errors: `sudo nginx -t`
- Check for configuration errors: `sudo service nginx configtest`
- Reload configuration without restarting the server: `sudo nginx -s reload`
- Fully restart of NGINX with server interruption: `sudo systemctl restart nginx`

### NGINX logs
By default, logs about access and errors relative to NGINX configuration and runtime are in `/var/log/nginx/` folder:
- acces: access.log
- erro: error.log

## Websites configuration
### SSL configuration
- Add cybnity.tech wildcard certificate and private key for reuse by NGINX according to:
  - Add __STAR_cybnity_tech.pem__ public certificate into `/etc/nginx/ssl/cybnity.tech/certs`
  - Add __star_cybnity_tech_private.key__ file (private key) into `/etc/nginx/ssl/cybnity.tech/private`

### Web sites configuration zones
A dedicated sub-folder per website (e.g CYDEL01 cluster) is created into the `/etc/nginx/sites-available/` folder.

Edit the existing `/etc/nginx/nginx.conf` default file, and replace the default included routing directive to `/etc/nginx/conf.d/*.conf`, via a default routing directive to enabled websites:
```
http {
    ...

    # Load website configurations
    include /etc/nginx/sites-enabled/*;
    #include /etc/nginx/conf.d/*.conf; # default static local NGINX website page deactived
}
```

### Rancher application website (sup.cybnity.tech)
- Add some configuration parameters into the `/etc/nginx/nginx.conf` for support of RKE2 and Rancher website endpoint managed by SUPPORT K8S cluster servers:
```
  user  nginx;

  # --- RANCHER RKE2 config parameters
  #worker_processes  auto; # default configuration replaced
  worker_processes 4;
  worker_rlimit_nofile 40000;

  error_log  /var/log/nginx/error.log notice;
  pid        /var/run/nginx.pid;

  events {
      worker_connections  8192;
  }
```
- Create the configuration file defining the load-balancing service regarding endpoint of SUPPORT cluster is created as `/etc/nginx/sites-available/sup.cybnity.tech`, including:

```
# HTTP PROXY MODE
    # Log configuration (tutorial at https://betterstack.com/community/guides/logging/how-to-view-and-configure-nginx-access-and-error-logs/)
    # Custom log formatting definition SHALL BE MIGRATED TO DEFAULT nginx.conf FILE WHEN MULTIPLE ENABLED SITES REUSE THIS SAME FORMAT
    log_format custom '$remote_addr - $remote_user [$time_local]  $status '
                  '"$host" "$request" $body_bytes_sent "$http_referer" '
                  '"$http_user_agent" "$http_x_forwarded_for"';

    # Apply cusotmized log format (define in nginx.conf default file
    access_log /var/log/nginx/support_access.log custom;
    # Disabling via 'off' parameter (e.g access_log off;)

    # Severity level (debug, info, notice, warm, error, crit, alert, emerg)
    error_log /var/log/nginx/support_debug_error.log debug; # DURING CONFIG DEV: highly detailed messages primarily used for troubleshooting and development (+ info, notice, warm, error, crit, alert, emerg)
    error_log /var/log/nginx/support_error.log error; # DURING OPERATING: actual arrors encountered during processing (+ crit, alert, emerg)
    error_log /var/log/nginx/support_alert_error.log alert; # DURING OPERATING + INCIDENT NOTIFICATION: errors that demand immediate action (+ emerg)

    # UPSTREAM - Allow to define a group of servers able to respond to request (load-balancing, fail-over)
    upstream support_servers_https { # Defined virtual server (group named) running on NGINX
        # IP Hash – The server to which a request is sent is determined from the client IP address. In this case, either the first three octets of the IPv4 address or the whole IPv6 address are used to calculate the hash value. The method guarantees that requests from the same address get to the same server unless it is not available.
        ip_hash; # User's session persistence consistency for routing to the same server

        # Backend servers private IP addresses or hostname
        # Passive TCP health checks:
        # - fail_timeout: qty of connection attempts must fail for the server to be considered unavailable (e.g default value is 10s)
        # - max_fails: qty of failed attempts that happen during the specified time for NGINX consider the server unavailable (e.g default value is 1)
        #
        #   So if 5 connection attempt times out or fails at least once in a 30-second period, NGINX marks the server as unavailable for 30 seconds
        #
        # Active TCP health checks for continuous test of upstream servers for responsiveness that avoid servers that have failed. If a connection to the server cannot be established, the health check fails, and the server is considered unhealthy
        # Shared memory zone (for counters and connections) shall be defined for support_servers_http
        zone support_servers_http 64k;

        server sup1.cybnity.tech:443 max_fails=5 fail_timeout=30s;
        server sup2.cybnity.tech:443 max_fails=5 fail_timeout=30s;
        server sup3.cybnity.tech:443 max_fails=5 fail_timeout=30s;
    }

    map $http_upgrade $connection_upgrade {
        default Upgrade;
        ''      close;
    }

    # RANCHER SERVER - All options and directives relative to the Rancher application
    server {
        # APPLICATION SERVER CONTEXT
        # Defined domain name, referencing the upstream servers and headers that should be passed to the backend
        listen 443 ssl; # Assigned specific ip address resolving the potential issue of SSL protocol behaviour when connection is established before the browser sends an HTTP request and NGINX does not know the name of the requested server
        server_name rancher.cybnity.tech;

        #--- HARDENING OF SSL CONFIGURATION ---
        ssl_session_cache shared:SSL:20m; # Shared cache size defined to 20MB
        keepalive_timeout 70; # Default cache timeout is 5min, and is increased for multi-core system with 20MB shared session cache
        ssl_session_timeout 1h; # Cache lifetime of 1h (10m for 10 minutes)

        # When not applied *.cybnity.tech wilcard SSL certificate paths (public cert, and private key) are defined in global configuration (nginx.conf)
        # SSL wildcard certificate configuration relative to all cybnity.tech sub-domains and enabled servers
        ssl_certificate         /etc/nginx/ssl/cybnity.tech/certs/STAR_cybnity_tech.pem; # Specific wilcard certificate or chained certificates
        ssl_certificate_key     /etc/nginx/ssl/cybnity.tech/private/star_cybnity_tech_private.key; # Reserved NGINX read-only access
        #ssl_trusted_certificate /etc/nginx/ssl/cybnity.tech/certs/ca-certs.pem;

        # Instruct all supporting web browsers to use only HTTPS
        add_header Strict-Transport-Security "max-age=31536000";

        # Location directive per url /<path> to support (e.g /api, /static/ for root /path/to/static/files). Can be multiple for same server
        # Define where Rancher requests will be forwarded
        location / {
                # Configuraton of NGINX for Rancher is documented at https://ranchermanager.docs.rancher.com/getting-started/installation-and-upgrade/installation-references/helm-chart-options#example-nginx-config

                # proxy_pass directive forwards HTTP requests (request URI based) to a specific network address (upstream) as backend exposed endpoint
                proxy_pass https://support_servers_https;

                # proxy_set_header directive is used to pass vital information about the request to the upstream servers
                proxy_set_header Host $host;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection $connection_upgrade;

                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; # Required when NGINX reverse proxy transmit a request to backend server
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-Port $server_port;
                proxy_http_version 1.1;

                # This allows the ability for the execute shell window to remain open for up to 15 minutes. Without this parameter, the default is 1 minute and will automatically close.
                proxy_read_timeout 900s;
                proxy_buffering off;

                proxy_cache_bypass  $http_upgrade;
        }
    }

    # Rancher UI web application
    server {
        listen 80;
        server_name rancher.cybnity.tech;
        return 301 https://$server_name$request_uri;
    }
```

- Create a symbolic link from `sites-available` to `sites-enabled` for activate the web zone, via command:
```
  sudo ln -s /etc/nginx/sites-available/sup.cybnity.tech /etc/nginx/sites-enabled/
```
- Check valid HTTPS route over the NGINX to SUPPORT cluster via command `curl -vIL https://rancher.cybnity.tech` that show detail of request and HTTPS flow steps.
- Check the automatix forwaring of HTTP request to HTTPS request via command `curl -vIL http://rancher.cybnity.tech`.
- Check access to Rancher application deployed into the SUPPORT cluster over a web browser call to url `https://rancher.cybnity.tech`, confirming the  activated load-balancing of application call between SUPPORT cluster serves serving the Rancher web application url.

#
[Back To Home](CYDEL01.md)
