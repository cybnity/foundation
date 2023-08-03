## PURPOSE
Presentation of the assistance documentation regarding the usage of Helm tool (e.g command line usefull) allowing to develop, maintain and manage the Helm charts defining the CYBNITY systems configurations.

# HELM USAGE
Several Helm command lines are available to be executed from the `charts directory`. The Kubernetes instance should be started and active (e.g Minikube started).

## Charts Management
<details><summary>Pull a chart</summary>
<p>
Download a chart from a repository in local directory:

```shell
helm pull "bitnami/xxx" --untar
```

</p>
</details>
<details><summary>Test chart</summary>
<p>
Using `--dry-run` will make it easier to test your chart code, but it won't ensure that Kubernetes itself will accept the templates you generate.

```shell
helm install --debug --dry-run goodly-guppy ./reactive-backend-system
```
</p>
</details>
<details><summary>Install a chart</summary>
<p>
Install a defined chart into the Kubernetes cluster:

```shell
# Install local templates from charts directory
helm install reactive-backend-system ./reactive-backend-system/ --values ./reactive-backend-system/values.yaml
# Install repository template (e.g hosted by a third-party's repository server) with specific values.yaml to apply
helm install access-control-sso-system -f ./access-control-sso-system/values.yaml bitnami/keycloak
```

</p>
</details>
<details><summary>Upgrade an installed chart</summary>
<p>
Upgrade a release to a specified or current version of a chart or configuration into the Kubernetes cluster:

```shell
helm upgrade reactive-backend-system ./reactive-backend-system
```

</p>
</details>
<details><summary>See installed charts</summary>
<p>
Query the named releases of charts installed on the kubernetes instance:

```shell
helm ls
```

</p>
</details>
<details><summary>Rollback a release to previous versions</summary>
<p>
Specific version to roll back to or leave argument black, in which cas it rolls back to the previous version.

```shell
helm rollback reactive-backend-system 1
```

</p>
</details>

<details><summary>Uninstall a chart</summary>
<p>
Uninstall a release completely from the Kubernetes cluster:

```shell
helm uninstall reactive-backend-system
```

</p>
</details>
<details><summary>Forward external port to POD/Service internal port</summary>
<p>
For access to Pod or Service since external point of the Cluster, start a process in a dedicated linux shell forwarding the calls via:

```shell
# Access from external web browser on HTTP://127.0.0.1:8080/ to keycloak running in LoadBalancer mode on port 81 TCP
kubectl port-forward --namespace default svc/access-control-sso-system 8080:81
# Access from external web browser on HTTP://127.0.0.1:8081/ to Web Reactive Frontend service instance running in ClusterIP mode on port 80 TCP
kubectl --namespace default port-forward svc/web-reactive-frontend-system 8081:80
# Access from external web browser on HTTP://127.0.0.1:8082/ to Reactive Backend service instance running in ClusterIP mode on port 80 TCP
kubectl --namespace default port-forward svc/reactive-backend-system 8082:80

```

Some executable scripts are provided by the [systems modules sub-projects](/implementation-line/systems/modules) that simplify the start of all the ports forwarding of the K8s services according the environment used (e.g local-env).

</p>
</details>
<details><summary>Minikube activation</summary>
<p>

To activate the ingress module in Minikube:

```shell
minikube addons enable ingress

```

When minikube is started, the LoadBalancer does not expose external ip by default.

So, to expose automatically any LoadBalancer external ip by Minikube, start Minikube tunneling:

```shell
minikube tunnel

# To show exposed ports
kubectl get svc
```

</p>
</details>

## Charts Distribution
Helm is usable to package, publish, and fetch Kubernetes applications as Chart archives.

<details><summary>Add CYBNITY helm repo on local repo catalog</summary>
<p>

```shell
helm repo add cybnity https://cybnity.github.io/iac-helm-charts
```

</p>
</details>

<details><summary>Package chart</summary>
<p>
Create a versioned archive file of charts to be able to distribute them (e.g to Helm charts repository):

```shell
helm package ./reactive-messaging-gateway
```

An option also exist to sign the chart archive if need.
</p>
</details>

<details><summary>Publish chart package repository</summary>
<p>
1. From the Dockerhub platform profile page, create a Personal Access Token (PAT) from the Profile > Security section with a name (e.g `olivier-pat-token`).

- Save the PAT password value into an environment variable allowing reuse from workstation and/or system that publish the helm charts

```shell
echo $REG_PAT | helm registry login registry-1.docker.io -u cybnity --password-stdin
```

2. From local directory where a packaged chart is ready for publish

- Push a packaged chart to Dokerhub repository:

```shell
# Login with a Dockerhub account and password, according to the context where PAT can be used (e.g cybnity organization)
helm registry login registry-1.docker.io -u cybnity

helm push reactive-messaging-gateway-0.1.0.tgz  oci://registry-1.docker.io/cybnity
```

An option also exist to sign the chart archive if need.
</p>
</details>
