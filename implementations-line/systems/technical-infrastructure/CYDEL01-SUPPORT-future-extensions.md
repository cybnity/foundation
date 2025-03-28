## PURPOSE
This technical documentation presents the guidelines (e.g server installation instructions, configuration of system elements) allowing preparation, configuration and maintenant in operational state of the primary support server.

The services provided by the CYBSUP01 server are focus on:
- primary server of the Support Environment cluster supporting all the CYDEL version's tools
- management of other environments (e.g other Kubernetes clusters of CYDEL01)
- hosting of Continuous Delivery tools and applications managing the detection and installation of CYBNITY software suite versions into other K8S clusters
- management of application systems deployment life cycle (e.g continuous delivery chain) on other K8S cluster (e.g dev, test...)

# INFRASTRUCTURE SERVICES

## Storage

### MinIO
Distributes and S3-compatible storage system deployed on Linux OS for commond block-based data management between all CYDEL01 servers.

# APPLICATION SERVICES
## MinIO
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

#
[Back To Home](CYDEL01.md)
