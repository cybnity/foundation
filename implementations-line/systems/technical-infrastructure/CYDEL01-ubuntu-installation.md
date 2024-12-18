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
  - define independent datadisk dedicated to the K8S resources storage with a mounting point at `/srv`
  - set installation of Open SSH server (allowing remote connection over LAN network)
  - install third-party drivers (e.g nvidia detected card driver)

After installation, storage layout and filesystem layout shall be shown (via `lsblk -f` command) as:

| NAME | FSTYPE | TYPE | RO | MOUNTPOINTS |
|:--|:--|:--|:--|:--|
| sda | btrfs | disk | 0 | /srv |
| sr0 | | rom | 0 | |
| nvme0n1 | | disk | 0 | |
| nvme0n1p1 | vfat| part | 0 | /boot/efi |
| nvme0n1p2 | ext4 | part | 0 | /boot |
| nvme0n1p3 | LVM2_member| part | 0 | |
| - ubuntu--vg-ubuntu--lv | ext4 | lvm | / |

- Swap (all nodes) disabling (change consistent after a reboot with fstb file modification) to enhance Kubernetes performance via command:

```
  sudo swapoff -a
  #sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
  sudo sed -i '/ swap / s/^/#/' /etc/fstab
```

- Add Kernel parameters (all nodes)
  - load the required modules on all nodes (set up IP bridge for nodes to communicate over the network) via command:
```
  sudo tee /etc/modules-load.d/containerd.conf <<EOF
  overlay
  br_netfilter
  EOF
  sudo modprobe overlay
  sudo modprobe br_netfilter
```

  - configure critical kernel parameters for Kubernetes via command:
```
  sudo tee /etc/sysctl.d/kubernetes.conf <<EOF
  net.bridge.bridge-nf-call-ip6tables = 1
  net.bridge.bridge-nf-call-iptables = 1
  net.ipv4.ip_forward = 1
  EOF
```

  - reload the changes via command `sudo sysctl --system`

- Server hostname change
  - change default server's hostname defined during the standard Ubuntu installation by another one according to the server role (e.g "cybsup01" about CYBNITY Support's server 1) via command line `sudo hostnamectl set-hostname cybsup01`

  - to make change without closing the terminal via command `exec bash`

- Update of /etc/hosts file check (e.g DHCP mode from network system) or static ip address.
  Ensure your system can resolve its hostname by updating the /etc/hosts file with the IP address and the new hostname via command:
```
  sudo vi /etc/hosts

  # Add a line: server-desired-hostname
  # For example:
  192.168.30.12 cybdev02.cybnity.tech
```

- Ubuntu version update and upgrade via command line:
```
  sudo apt-get update && sudo apt-get upgrade -y
  sudo apt update && sudo apt -y full-upgrade

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

  - show configuration of detected ethernet controllers via command `sudo hwinfo --netcard`

### Intel 10-Gigabit X540-AT2 card drivers
When NIC not detected or usable, install when network card were not detected/configured during the origin Linux installation.

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
- install ifconfig via command `sudo apt install net-tools` allowing usage of __ifconfig__ command

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
        dhcp4-overrides:
          use-dns: true
          send-hostname: false
          hostname: cybsup01_mgt
        dhcp6: true
        dhcp6-overrides:
          use-dns: true
          send-hostname: false
          hostname: cybsup01_mgt

      # set 100Gbps NIC cards in DHCP mode as operation server endpoints
      enp11s0f0:
        dhcp4: true
        optional: true
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

- verify file conformity and detect potential format errors via command:
```
sudo netplan try
```

- Apply the new configuration with command:
```
sudo netplan apply
```

- Add client static binding record into the LAN DHCP service to assign always same ip address to the NIC mac address (required by Kubernetes cluster using static ip address during node setting creation)
- Connect a network cable between the NIC card port and the network switch, and check that machine have been detected/assigned into the DHCP client list table (e.g hostname cybsup01)
- Try to ping external servers to check the opened route via command executions:
```
# check route to Internet domains
ping google.com

# check route to other server available in default lan
ping <external server ip>

# check route to potential external LAN machine (e.g machine name known by DNS server that have been dynamically configured by DHCP client)
ping <external server name>.<domain name>

```

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

  - check values of properties shown (becarefull, PCI additional card can not support WOL when Supports Wake-on parameter value is equals to 'd'):
    ```
    Supports Wake-on: pumbg
	Wake-on: g
    ```

- Setting one of __u__, __m__ or __b__ along with __g__ might also be necessary to enable the feature. Some motherboard manufacturers require you to change the settings in the BIOS to enable this feature. If there is no change when you check after entering the command, it is recommended to look at the BIOS settings.

- Since Ubuntu has been transitioning to systemd since version 15.04 and part of that transition is the implementation of Predictable Network Interface Naming, replace INTERFACE for Wake On Lan with the interface name targeted, for ethtool to tell the network card to listen out for magic packets. The __g__ denotes that it should __listen for magic packets__ (and __d__ value makes feature disabled) via command:
  ```
  # change Wake-on property on interface
  sudo ethtool -s enp0s25 wol g

  # check changed status (changed value of Wake-on property)
  sudo ethtool enp0s25
  ```

  - If feature supported by NIC port, make change as permanent (see [setting doc for help](https://thedarkercorner.com/setting-up-wake-on-lan-on-ubuntu-server-22-04-lts/))
    - create new file `wol.service` into the `/etc/systemd/system` folder, including (used value __d__ for disable, or value __g__ for enable Wake-on mode into the ExecStart command's parameter):
    ```
    [Unit]
    Description=Configure Wake-up on LAN

    [Service]
    Type=oneshot
    ExecStart=/sbin/ethtool -s enp0s25 wol g

    [Install]
    WantedBy=basic.target
    ```

    - add wol service to the systemd services via command:
    ```
    # update and/or locate new service file
    sudo systemctl daemon-reload
    ```

    - enable the service to run on start up and to fire up the service via command:
    ```
    sudo systemctl enable wol.service
    ```

    - check status via command:
    ```
    sudo systemctl status wol
    ```

- test the operational state of WoL capability with:
  - on server, execute `poweroff` to stop the machine
  - on another station, wake it up with a magic packet send (e.g on mac over command execution `wakeonlan <<MAC ADDRESS>>`)

### Timezone
Change permanently the OS's timezone used as reference according to the server location, via commands:
```
sudo ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
sudo dpkg-reconfigure -f noninteractive tzdata
```

#
[Back To Home](CYDEL01.md)
