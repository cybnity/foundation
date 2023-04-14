## PURPOSE
Presentation of the procedure for installation and configuration of an HAProxy service providing an API gateway as endpoint of all http/https services exposed by the UI layer.

The [Bitnami GitHub project](https://github.com/bitnami/charts/tree/main/bitnami/haproxy/#installing-the-chart) is reused, allowing to initialize a basic reverse proxy module sufficient for the exposure of exposed services:
- Access-control-sso system over HTTP/HTTPS (Keycloak server)
- Web-reactive-frontend application system over HTTP/HTTPS (Javascript/HTML5 web application)
- Reactive-messaging-gateway system over SockJS/HTTPS (Vert.x messaging server)

# HAProxy
HAProxy solution is reused as reverse proxy. See [HAProxy documentation](https://www.haproxy.com/documentation/kubernetes/latest/community/install/kubernetes/) for mode detail on its configuration, and [HAPEE-LB technical documentation](https://www.haproxy.com/documentation/hapee/latest/onepage/) about detailed features and settings (e.g filters, logging, cahing, ACLs, binding, proxyies, global parameters).

Bitnami helm charts if used to define a reverse proxy via a Kubernetes Deployment (type of controller used to manage a set of pods that is responsible for replicating and scaling the proxy pods).

## Installation of ui-apis-gateway-system-haproxy
The reverse proxy and its load balancing as cluster's entrypoint is implemented via HAProxy packaged by [Helm project](haproxy).

Deployment in an environment is performed via the Helm command line:

```console
helm install ui-apis-gateway-system -f ./haproxy/values.yaml bitnami/haproxy
```

Several elements are deployed:
- A ConfigMap representing the deployed routing rules taht are defined by haproxy configuration coming from the Helm chart regarding the Service provisioning
- A Service (LoadBalander type) to handle all unrouted traffic (e.g return 404), and defining HAProxy configuration (e.g http characteristics changes via HAProxy configuration file)
- Two Replica Sets managing the fault tolerance of the HAProxy Service's pods (e.g 2 instance of HAProxy services executed for scaling management)
- A Deployment managing the Replica Sets instance and recovery (e.g during rolling and recovery phase)


## External access to proxy's pods
An K8s Ingress object (independent resource that configures external access to a service's pods) is created with the HAProxy Helm Chart after the Service has been deployed, to hook it up to external traffic (convenient to isolate service definitions from the logic of how clients connected to them, giving most flexibility).

L7 routing is one of the features of Ingress that allow incoming requests to be routed to the exact pods that can serve them (e.g Keycloak server, web frontend, reactive messaging gateway) based on HTTP characteristics such as the requested URL path. Other features include terminating TLS and load balancing traffic.

The Ingress Controller continuously watches for changes (e.g pods that have arbitrary IPs and ports) and is responsible to hide all internal networking from exterior of the Cluster area (users-interactions-area Node). The Ingress Controller with a ConfigMap resource and/or annotations on the Ingress object can decouple proxy configuration from services.

## Routing
The HAProxy configuration file (values.yaml) help to customize the original Bitnami HAProxy Helm charts, including:
- external exposure of unified entrypoint (proxy on port 80) > internal routing configuration (e.g port mapping) > dedicated serviecs (e.g access-control-sso-system, reactive-backend-system, web-reactive-frontend-system)
