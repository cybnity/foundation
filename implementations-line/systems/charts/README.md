## PURPOSE
Presentation of the assistance documentation regarding the usage of Helm tool (e.g command line usefull) allowing to develop, maintain and manage the Helm charts defining the CYBNITY systems configurations.

# HELM USAGE
Several Helm command lines are available to be executed from the `charts directory`. The Kubernetes instance should be started and active (e.g Minikube started).

## Charts Management
<details><summary>Install a chart</summary>
<p>
Install a defined chart into the Kubernetes cluster:

```shell
helm install ui-apis-gateway ./ui-apis-gateway
```

</p>
</details>
<details><summary>Upgrade an installed chart</summary>
<p>
Upgrade a release to a specified or current version of a chart or configuration into the Kubernetes cluster:

```shell
helm upgrade ui-apis-gateway ./ui-apis-gateway
```

</p>
</details>
<details><summary>See installed charts</summary>
<p>
Query the named releases of charts installed on the kubernetes instance:

```shell
helm ls -all
```

</p>
</details>
</details>
<details><summary>Rollback a release to previous versions</summary>
<p>
Specific version to roll back to or leave argument black, in which cas it rolls back to the previous version.

```shell
helm rollback ui-apis-gateway 1
```

</p>
</details>

<details><summary>Uninstall a chart</summary>
<p>
Uninstall a release completely from the Kubernetes cluster:

```shell
helm uninstall ui-apis-gateway
```

</p>
</details>

## Charts Distribution
Helm is usable to package, publish, and fetch Kubernetes applicatiosn as chart archives.

<details><summary>Package chart</summary>
<p>
Create a versioned archive file of charts to be able to distribute them:

```shell
helm package ./ui-apis-gateway
```

An option also exist to sign the chart archive if need.
</p>
</details>