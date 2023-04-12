## PURPOSE
Presentation of the procedure for installation and configuration of an Ingress controller providing an API gateway as endpoint of all http/https services exposed by the UI layer.

The [documentation of Bitnami Helm tutorial](https://github.com/bitnami/charts/tree/main/bitnami/nginx-ingress-controller/#installing-the-chart) is reused, allowing to initialize a basic reverse proxy module sufficient for the exposure of exposed services:
- Access-control-sso system over HTTP/HTTPS (Keycloak server)
- Web-reactive-frontend application system over HTTP/HTTPS (Javascript/HTML5 web application)
- Reactive-messaging-gateway system over SockJS/HTTPS (Vert.x messaging server)

# HAProxy
Bitnami helm charts if used to defined a reverse proxy.

See [GitHub project](https://github.com/bitnami/charts/tree/main/bitnami/haproxy/#installing-the-chart) for more detail.

## Installation of ui-apis-gateway-system-haproxy
The reverse proxy and its load balancing as cluster's entrypoint is implemented via HAProxy packaged by [Helm project](haproxy).

Deployment into an environment is performed via Helm command line:

```console
helm install ui-apis-gateway-system -f ./haproxy/values.yaml bitnami/haproxy
```

## Routing
The HAProxy configuration file (values.yaml) help to customize the original Bitnami HAProxy Helm charts, including:
- external > internal routing configuration (e.g port mapping)

The current implementation is only based on ports mapping (e.g one dedicated external port exposed out of the cluster) for access to:
- web-reactive-frontend-system via port 8081 or port 80
- reactive-backend-system via port 8082
- access-control-sso-system via port 8081
