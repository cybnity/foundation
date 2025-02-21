## PURPOSE
This technical documentation presents the installation protocol of a Linux Ubuntu distribution on a server supporting a CYDEL01 infrastructure.

Basic BIOS and OS layer's elements are presented with detail of all technical operations to execute for prepare a server into a __"ready for virtualization layer installation"__ state.

# HARDWARE LAYER

## BIOS
- System configuration
  - USB configuration:
    - Enable USB 3.0 (xHCI) on super speed ports: disable (avoid potential conflict with Wake-On-Lan ability via PCI NIC card)
- Power management planification defining auto-start of server according to project work time periods
  - Auto On Time: selected days and time when server shall be automatically started
  - AC recovery: last power state
  - Deep sleep control: disable
  - Wake On LAN: LAN only
  - Block sleep (S3 state): disabled

# OPERATING SYSTEM LAYER

## Linux Kernel
Based on default version defined by the Ubuntu LTS server version installed.

## AppArmor
Installed by default by Ubuntu Linux server LTS version. See [AppArmor configuration doc](https://discourse.ubuntu.com/t/ubuntu-24-04-lts-noble-numbat-release-notes/39890#security-improvements) for help.

## Ubuntu Linux Server
- Installation of Ubuntu LTS server version (e.g from bootable USB stick)
  - define boot and OS dedicated partitions for OS directories and mounting points
  - define K8S resources storage with a mounting point at `/srv`
    - when unique datadisk is available and used for OS and K8S resources, format it with `ext4` filesystem
    - when 2nd independent datadisk is available and can be dedicated to the `/srv` mounting point, format it with `btrfs` filesystem
  - set installation of Open SSH server (allowing remote connection over LAN network)
  - install third-party drivers (e.g nvidia detected card driver)

After installation, storage layout and filesystem layout shall be shown (via `lsblk -f` command) as:

| NAME | FSTYPE | TYPE | RO | MOUNTPOINTS |
|:--|:--|:--|:--|:--|
| sda | btrfs | disk | 0 | /srv |
| nvme0n1 | | disk | 0 | |
| nvme0n1p1 | vfat| part | 0 | /boot/efi |
| nvme0n1p2 | ext4 | part | 0 | /boot |
| nvme0n1p3 | LVM2_member| part | 0 | |
| - ubuntu--vg-ubuntu--lv | ext4 | lvm | / |

### Swapping
- Swap (all nodes) disabling (change consistent after a reboot with fstb file modification) to enhance Kubernetes performance via command:
```
  sudo swapoff -a
  #sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
  sudo sed -i '/ swap / s/^/#/' /etc/fstab

  sudo systemctl disable swap.target
```

### Host naming
- Server hostname change
  - Change default server's hostname defined during the standard Ubuntu installation by another one according to the server role (e.g "sup1" about first CYBNITY Support's server) via command line `sudo hostnamectl set-hostname sup1`
  - Apply change without closing the terminal via command `exec bash`
  - Check result by running command `hostnamectl`

- Ubuntu version update and upgrade via command line:
```
  sudo apt-get update && sudo apt-get upgrade -y
  sudo apt update && sudo apt -y full-upgrade
```

## Time configuration
### NTP
Network Time Protocol (NTP) package shall be installed. This prevents errors with certificate validation that can occur when the time is not synchronized between the client and server.
- If ntpd is need, install and start it via command:
```
  sudo apt install ntp

  # Enable ntp service if not already enabled during apt installation
  sudo systemctl enable ntp

  # Start ntp service if not already started during apt installation
  sudo systemctl start ntp
```
- Update automatic restart of NTP service (managed by daemon) via file in `/etc/systemd/system/ntp.service` including:
```
  [Service]
  Restart=on-failure
  RestartSec=5s
```
- Reload systemd for the changes to take effect via command:
```
  sudo systemctl daemon-reload
```
### Timezone
Change permanently the OS's timezone used as reference according to the server location, via commands:
```
  sudo ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
  sudo dpkg-reconfigure -f noninteractive tzdata
```

### Hardware clock timezone sync
Timedate is by default installed on Ubuntu in place of previous ntpd tool, check that autosync of timezone from Internet is active via command:
```
  # Check autosync time
  timedatectl

  # Check RTC in local TZ because, 'RTC in local TZ:' no means the hardware clock is interpreted as UTC
  # So in case of poweron scheduling via BIOS configuration, considere the UTC to Local time zone difference (e.g hours gap) when defining a BIOS scheduling action
```

### Command LIne color
- Activate the command line contents by default with environment variable defined in __/etc/environment__:
```
  CLICOLOR=1
```
- Reload environment file to activate changed file, via command:
```
  set -a; . /etc/environment; set +a;
```

### Account password definition
Set account passwords via commands:
```
  # set the current account password
  passwd

  # set the root password
  sudo passwd root
```

## Networking
- Check detected network card via commands:
```
  sudo lspci | grep -E -i --color 'network|ethernet|wireless|wi-fi'
```

- Check capacities of card via command `sudo lshw -class network`

- When need more hardware information, install hwinfo tool via command:
```
  sudo apt-get install hwinfo
```

- Show configuration of detected ethernet controllers via command `sudo hwinfo --netcard`

### Kubernetes node bridge configuration
- Add Kernel parameters (all nodes)
  - Load the required modules on all nodes (set up IP bridge for nodes to communicate over the network) via commands:
```
  # Add modules declaration to container daemon
  sudo tee /etc/modules-load.d/containerd.conf <<EOF
  overlay
  br_netfilter
  EOF

  # Activate modules loading
  sudo modprobe overlay && sudo modprobe br_netfilter
```
  - Configure critical kernel parameters for Kubernetes via command:
```
  sudo tee /etc/sysctl.d/kubernetes.conf <<EOF
  net.bridge.bridge-nf-call-ip6tables = 1
  net.bridge.bridge-nf-call-iptables = 1
  net.ipv4.ip_forward = 1
  EOF
```
  - Reload the changes via command `sudo sysctl --system`

### Intel 10-Gigabit X540-AT2 card drivers
When NIC not detected, usable or not configured during the origin Linux installation, install drivers:
- Download of drivers files via command lines:
```
   curl -O https://downloadmirror.intel.com/832293/ixgbe-5.21.5.tar.gz
   curl -O https://downloadmirror.intel.com/832293/intel-public-key-ixgbe-ko.zip
   curl -O https://downloadmirror.intel.com/832296/ixgbevf-4.20.5.tar.gz
   curl -O https://downloadmirror.intel.com/832296/intel-public-key-ixgbevf-ko.zip
```
- Execute driver installation via command lines:
```
   transactional-update -i pkg install ixgbe-5.21.5.tar.gz
   transactional-update -i pkg install intel-public-key-ixgbe-ko.zip
```
- Reboot system
- Check visibility of detected network cards via command `ip a`
- Install ifconfig via command `sudo apt install net-tools` allowing usage of `ifconfig` command

### NetworkManager configuration
By default, only motherboard default embedded network card is configured (statically or in DHCP mode according to the choice made during the Ubuntu installation procedure).

None configuration is existing about the additional network card (e.g Intel 10-Gigabit X540-AT2 card).

- Identify card identifiers which could be configured into the netplan network configuration management tool used by Ubuntu via command: `ifconfig -a`
- Create a new file `/etc/netplan/99-vlan-network-config.yaml` (complementary configuration to the default existing netplan files included the setting of default network card) including setting per ethernet card:
```
  network:
    version: 2
    renderer: networkd
    ethernets:
      # make optional waiting of internet connection during boot over NIC that are not connected
      # down - don't wait for it to come up during boot
      # dedicated NIC to server management (e.g remote Wake On Lan) with specific ip address and hostname (not based on default server hostname configuration)
      enp0s25:
        dhcp4: true
        optional: true
        nameservers:
          search: [ cybnity.tech ]
        dhcp4-overrides:
          use-dns: true
          send-hostname: false
          hostname: sup1
        dhcp6: true
        dhcp6-overrides:
          use-dns: true
          send-hostname: false
          hostname: sup1

      # set 10Gbps NIC cards in DHCP mode as operation server endpoints
      enp11s0f0:
        dhcp4: true
        optional: true
        nameservers:
          search: [ cybnity.tech ]
        dhcp4-overrides:
          use-dns: true
          send-hostname: true
        dhcp6: true
        dhcp6-overrides:
          use-dns: true
          send-hostname: true
      enp11s0f1:
        dhcp4: true
        optional: true
        nameservers:
          search: [ cybnity.tech ]
        dhcp4-overrides:
          use-dns: true
          send-hostname: true
        dhcp6: true
        dhcp6-overrides:
          use-dns: true
          send-hostname: true
```

  See [Netplan documentation](https://netplan.readthedocs.io/en/latest/netplan-yaml/) for mode detail about usable attributes for configuration file.

- Change the permission allowed to the network configuration files to minimise accessibility to other users via command:
```
  sudo chmod 600 /etc/netplan/*.yaml
```

- Verify file conformity and detect potential format errors via command:
```
  sudo netplan try
```
- The nameservers.search parameter changed the domain of the server.

- Apply the new configuration with command:
```
  sudo netplan apply
```

- Check the value of the DNS Domain (normally equals to cybnity.tech) shown by command: `resolvectl status`

- Check value automatically defined as `cybnity.tech` into the `/etc/resolv.conf` file by Netplan

- Add client static binding record into the LAN DHCP service to assign always same ip address to the NIC mac address (required by Kubernetes cluster using static ip address during node setting creation)

#### DNS Server policy & ACL
When an external DNS server include an ACL policy definition relative to trusted server ip addresses authorized as DNS clients, add ACL declaration for each SUPPORT cluster node ip address allowing them to make request on the DNS server (resolution of other machine names).

For example, into the `/etc/bind/named.conf.options` BIND9 configuration file:
```
  acl internals { 192.168.60.0/24; 192.168.30.0/24; 192.168.50.0/24; localhost; localnets; };
  acl trusted {
        // CYDEL SUPPORT servers (DNS clients)
        192.168.30.10;  # sup1.cybnity.tech - CYDEL01 SUPPORT cluster node
        192.168.30.11;  # sup2.cybnity.tech - CYDEL01 SUPPORT cluster node
        192.168.30.12;  # sup2.cybnity.tech - CYDEL01 SUPPORT cluster node
  };
```

### DNS resolution test
- Connect a network cable between the NIC card port and the network switch, and check that machine have been detected and assigned into the DHCP client list table (e.g hostname equals to `sup1`)

- Try to ping external servers to check the opened route via command executions:
```
  # check route to Internet domains
  ping google.com

  # check route to other server available in default lan
  ping <external server ip>

  # check route to potential external LAN machine (e.g machine name known by DNS server that have been dynamically configured by DHCP client)
  ping <external server name>.<domain name>
```

By default, NetworkManager (configuration file at __/etc/NetworkManager/NetworkManager.conf__) is started by Ubuntu (auto-managing dynamic __resolv.conf__ file update) and status can be checked via command: `sudo systemctl status systemd-resolved`

### Wake On LAN enabling
- Check that WOL is supported by card and activated into the OS:
  - install ethtool via command `sudo apt install ethtool -y`
  - execute commands:
  ```
    # identify the identifier of each available network card
    ip a
    # find out if the network card supports wake-on-LAN
    sudo ethtool enp0s25
  ```

  - check values of properties shown (becarefull, PCI additional card can not support WOL when Supports Wake-on parameter is equals to `d` value):
  ```
    Supports Wake-on: pumbg
	Wake-on: g
  ```

- Setting one of __u__, __m__ or __b__ along with __g__ might also be necessary to enable the feature. Some motherboard manufacturers require you to change the settings in the BIOS to enable this feature. If there is no change when you check after entering the command, it is recommended to look at the BIOS settings

- Since Ubuntu has been transitioning to systemd since version 15.04 and part of that transition is the implementation of Predictable Network Interface Naming, replace INTERFACE for Wake On Lan with the interface name targeted, for ethtool to tell the network card to listen out for magic packets. The __g__ denotes that it should __listen for magic packets__ (and __d__ value makes feature disabled) via command:
```
  # change Wake-on property on interface
  sudo ethtool -s enp0s25 wol g

  # check changed status (changed value of Wake-on property)
  sudo ethtool enp0s25
```

  - If feature supported by a NIC port, make change as permanent (see [setting doc for help](https://thedarkercorner.com/setting-up-wake-on-lan-on-ubuntu-server-22-04-lts/))
    - Create new file `wol.service` into the `/etc/systemd/system` folder, including (used value __d__ for disable, or value __g__ for enable Wake-on mode into the ExecStart command's parameter):
    ```
      [Unit]
      Description=Configure Wake-up on LAN

      [Service]
      Type=oneshot
      ExecStart=/sbin/ethtool -s enp0s25 wol g

      [Install]
      WantedBy=basic.target
    ```

    - Add wol service to the systemd services via command:
    ```
      # update and/or locate new service file
      sudo systemctl daemon-reload
    ```

    - Enable the service to run on start up and to fire up the service via command:
    ```
      sudo systemctl enable wol.service
    ```

    - Check status via command:
    ```
      sudo systemctl status wol
    ```

- Test the operational state of WoL capability with:
  - on server, execute `shutdown -P` to stop the machine
  - on another station, wake it up with a magic packet send (e.g on mac over command execution `wakeonlan <<MAC ADDRESS>>`)

### Firewall
- Check status of default Ubuntu installed firewall via command: `sudo ufw status`
  - and disable when not needed

### Routing
Opening of 6443 tcp port using iptables (see [RKE doc](https://rke.docs.rancher.com/os#ports)) via command:
```
  # Open TCP/6443 for all
  sudo iptables -A INPUT -p tcp --dport 6443 -j ACCEPT
```

### SSH Server configuration
Add TCP forwarding capability to the SSH server via modification of the `/etc/ssh/sshd_config` file which shall include line:
```
  AllowTcpForwarding yes
```

## Monitoring
### Processes & resources monitoring
Additionally to default __top__ cli tool installed by Ubuntu, __glances__ monitoring tool can be installed via command:
```
  # Install tool
  sudo apt install glances

  # Start monitoring console UI
  glances
```
#
[Back To Home](CYDEL01.md)
