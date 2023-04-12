## PURPOSE
Presentation of the procedure for installation and configuration of an Ingress controller providing an API gateway as endpoint of all http/https services exposed by the UI layer.

The [documentation of Bitnami Helm tutorial](https://github.com/bitnami/charts/tree/main/bitnami/nginx-ingress-controller/#installing-the-chart) is reused, allowing to initialize a basic reverse proxy module sufficient for the exposure of exposed services:
- Access-control-sso system over HTTP/HTTPS (Keycloak server)
- Web-reactive-frontend application system over HTTP/HTTPS (Javascript/HTML5 web application)
- Reactive-messaging-gateway system over SockJS/HTTPS (Vert.x messaging server)

# HAProxy
Bitnami helm charts if used to defined a reverse proxy.

See [GitHub project](https://github.com/bitnami/charts/tree/main/bitnami/haproxy/#installing-the-chart) for more detail.

## Installation of ui-apis-gateway-system

```console
helm install ui-apis-gateway-system -f ./haproxy/values.yaml bitnami/haproxy
```







# Kubernetes NGINX Ingress Controller
This Ingress controller is developed as a reverse proxy and load balancer for Kubernetes via [GitHub](https://github.com/kubernetes/ingress-nginx).

Maintained by the Kubernetes community, see [documentation](https://kubernetes.github.io/ingress-nginx/) for more details.
