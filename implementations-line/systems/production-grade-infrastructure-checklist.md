## PURPOSE
Presentation of the requirements to define, manage and maintain for going to production.

# PRODUCTION-GRADE INFRASTRUCTURE CHECKLIST

|Task|Description|CYBNITY tools|
|:--|:--|:--|
|Install|Install the software binaries and all dependencies|ArgoCD|
|Configure|Configure the software at runtime. Includes port settings, TLS certs, service discovery, leaders, followers, replication, etc.|Helm, Consul|
|Provision|Provision the infrastructure. Include servers, load balancers, network configuration, firewall settings, IAM permissions, etc.|Terraform, Kubernetes, Calico / Kubernetes Network Policy, Nginx load-balancing & web proxy|
|Deploy|Deploy the service on top of the infrastructure. Roll out updates with no downtime. Includes blue-green, rolling, and canary deployments.|ArgoCD, Terraform, Kubernetes|
|High availability|Withstand outages of individual processes, servers, services, data centers, and regions.|Cloud datacenter, multiregion, replication, auto scalling, load balancing Terraformed Kubernetes modules|
|Scalability|Scale up and down in response to load. Scale horizontally (more servers) and/or vertically (bigger servers with more resources).|Auto scalling, replication, sharding, caching Terraformed Kubernetes modules|
|Performance|Optimize CPU, memory, disk, network, and GPU usage. Includes query tuning, benchmarking, load testing, and profiling.|Kubernetes|
|Networking|Configure static and dynamic IPs, ports, service, discovery, firewalls, DNS, SSH access, and VPN access.|K8s VPCs, K8s network policy (firewall), Snort IPS/proxy, routers, DNS registrars|
|Security|Encryption in transit (TLS) and on disk, authentication, authorization, secrets management, server hardening.|Consul, Vault|
|Metrics|Availability metrics, business metrics, app metrics, server metrics, events, observability, tracing, and alerting.|InfluxDB, Telegraf agent|
|Logs|Rotate logs on disk. Aggregate log data to central location.|Helm, Telegraf agent, Snort, InfluxDB, Grafana|
|Backup and Restore|Make backups of DBs, caches, and other data on a scheduled basis. Replicate to separate region/account.|Replication, MongoDB streams state async backup, Kafka cluster, Kubernetes|
|Cost Optimization|Pick proper instance types, use spot and reserved instances, use auto scaling, and nuke unused resources.|K8s auto scalling|
|Documentation|Document the code, architecture, and practices. Create playbooks to respond to incidents.|Concordion, Markdown READMEs on Github wiki|
|Tests|Write automated tests for infrastructure code. Run tests after every commit and nightly.|Terratest|
