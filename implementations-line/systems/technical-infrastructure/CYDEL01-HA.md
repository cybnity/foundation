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

## BIOS & operating system layer
See [Ubuntu-installation](CYDEL01-ubuntu-installation.md) procedure to prepare a server.

Current prepared server configurations is:
- FQDN: __ha.cybnity.tech__

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

Defined crontab directives on server in a safe way (with wait of existing process secure end before make the stop) via power off:
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
> timezone is UTC into NIOS by default. For example, 07:35:00 value shall be set for alignment with Europe Paris timezone when HA server shall be started at 08:35:00 in France.

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
    log_format custom '$remote_addr - $remote_user [$time_local]  $status '
                  '"$host" "$request" $body_bytes_sent "$http_referer" '
                  '"$http_user_agent" "$http_x_forwarded_for"';
    access_log /var/log/nginx/support_access.log custom;
    # Disabling via 'off' parameter (e.g access_log off;)

    # Severity level (debug, info, notice, warm, error, crit, alert, emerg)
    error_log /var/log/nginx/support_debug_error.log debug; # DURING CONFIG DEV: highly detailed messages primarily used for troubleshooting and development (+ info, notice, warm, error, crit, alert, emerg)
    error_log /var/log/nginx/support_error.log error; # DURING OPERATING: actual arrors encountered during processing (+ crit, alert, emerg)
    error_log /var/log/nginx/support_alert_error.log alert; # DURING OPERATING + INCIDENT NOTIFICATION: errors that demand immediate action (+ emerg)

    # UPSTREAM - Allow to define a group of servers able to respond to request (load-balancing, fail-over)
    upstream support_servers_https { # Defined virtual server (group named) running on NGINX
        zone support_servers_http 32k;
        ip_hash; # User's session persistence consistency for routing to the same server

        # Backend servers private IP addresses or hostname
        server sup1.cybnity.tech:443;
        server sup2.cybnity.tech:443;
        server sup3.cybnity.tech:443;
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
