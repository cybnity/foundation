#! /bin/bash
# Start remotely CYDEL01 infrastructure via Wake-On-Lan (WOL) features enabled on servers' NICs dedicated to remote management

# Start Support cluster servers (manager of other clusters and CYBNITY continuous delivery chain)
echo '--- Start SUPPORT cluster machines ---'
echo '    - Start CYBSUP01 (K8 clusters management)'
wakeonlan 64:00:6a:7c:79:49

# Start DEV cluster servers
echo '--- Start DEV cluster machines ---'
echo '    - Start CYBDEV01 (DEV cluster primary server)'
wakeonlan 18:60:24:93:84:fa
echo '    - Start DEV cluster nodes (in parallel)'
# wol command with & character at end of each command

# Confirm finalized remote start of CYDEL01 servers
echo '--- CYDEL01 infrastructure is started and ready ---'
