## PURPOSE
Presentation of the procedure for installation and configuration of an NGINX Ingress controller providing an API gateway as endpoint of all http/https services exposed by the UI layer.

The [documentation of Bitnami Helm tutorial](https://github.com/bitnami/charts/tree/main/bitnami/nginx-ingress-controller/#installing-the-chart) is reused, allowing to initialize a basic reverse proxy module sufficient for the exposure of exposed services:
- Access-control-sso system over HTTP/HTTPS (Keycloak server)
- Web-reactive-frontend application system over HTTP/HTTPS (Javascript/HTML5 web application)
- Reactive-messaging-gateway system over SockJS/HTTPS (Vert.x messaging server)

## NGINX Ingress Controller
The default implementation of the CYBNITY UI APIs Gateway module is ensured by NGINX and K8S Ingress controller services.

### Charts Management
<details><summary>Install a chart</summary>
<p>

```console
helm repo add binami https://charts.bitnami.com/bitnami

# Install the local value.xml configuration of Ingress charts provided by charts repository
helm install ingress -f ./nginx-ingress-controller/values.yaml bitnami/nginx-ingress-controller
```

</p>
</details>
