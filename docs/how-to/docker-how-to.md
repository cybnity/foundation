## PURPOSE
Developer help regarding the management of the Docker image template regarding a basic Java Runtime Execution container reusable by any CYBNITY server containerized.
The Dockerfile of the project is automated via Maven plugin that generate in-memory file (based on docker plugin configuration implemented into the pom.xml file).

# DOCKER USAGE
## START OF MINIKUBE (KUBERNETES PLATFORM FOR WORKSTATION)
A docker orchestrator should be started previously to execute any docker command line.

``` shell
# Create a minikube profile (allowed memory and cpu are defined PER NODE)
minikube start --driver=hyperkit --container-runtime=docker --profile local-dev --nodes 4 --cpus 2 --disk-size '7g' --memory '2g'

# WHEN CLUSTER INCLUDING ONLY ONE UNIQUE NODE : Export docker host and Docker daemon into the shell context variables
minikube docker-env

# Set docker env
eval $(minikube docker-env)

# Optionally, change active profile by default
minikube profile local-dev

# Start a cluster profiled (or without -p option for start of default active profile)
minikube start -p local-dev

# Add CYBNITY deployment labels regarding each node according to the CYBNITY cluster areas
## kubectl label nodes local-dev <label-name>=<label-value>
kubectl label nodes local-dev layer=user-interfaces-area
kubectl label nodes local-dev-m02 layer=domains-io-area
kubectl label nodes local-dev-m03 layer=domains-area
kubectl label nodes local-dev-m04 layer=infrastructure-services-area

# Verify the labeled zones
kubectl get nodes --show-labels

# Start web dashboard of a profile (and included nodes)
minikube dashboard -p local-dev

# Delete profile
minikube delete -p local-dev

```

## BUILD OF IMAGE
Build process is managed via Maven.

See https://dmp.fabric8.io/ documentation's __docker:build chapter__ that present all the informations managed during the build step of the Dockerfile implemented by the Maven pom.xml.

### After Maven generation of Dockerfile
After `mvn clean package` execution, it's possible to execute additional command lines from the `target/docker/cybnity/jre-tee sub-directory/...` including the generated Dockerfile.

Build dockerfile (without docker-compose help) from directory from shell command line:
`docker build -t cybnity/jre-tee .`

### Rancher Desktop
During the Docker image build by Maven, a possible need of more disk storage space can generate a build error during the generation of a Docker image. It's due to Docker __buildx__ image created by Docker (during docker-maven-plugin build step).

### Default Docker host name
By default, the Rancher Desktop docker instance is named rancher-desktop (not named "default" like basically used on any other Docker installation or by Maven plugin executed during the CYBNITY projects build).

When __Administrative Access__ have not been enabled on Rancher Desktop (from Preferences > Application > General > __Allow to acquire administrative credentials (sudo access)__ enabled option), the automatic change of DOCKHER_HOST and default context switch of Docker socks is not made by Rancher Desktop, and require a manual change of context to use by the developer via command:
```
  # Show started Docker context and socket endpoints
  docker context ls
  
  # Change execution context by default to rancher-desktop context
  docker context use rancher-desktop
```

See [Docker contexts documentation](https://docs.docker.com/engine/manage-resources/contexts/) about management of host context.
  
### On Mac OS
To change and increase the allocated disk storage size for buildx, stop Rancher Desktop and execute commands:
```
  rdctl shutdown
  # Create an override.yaml file with the desired disk size, e.g
  echo 'disk: "256GiB"' > ~/Library/Application\ Support/rancher-desktop/lima/_config/override.yaml
  
  # Delete the existing VM
  rm -rf ~/Library/Application\ Support/rancher-desktop/lima/0
  
  # Clean Docker repository
  docker system prune -a
```

Restart Rancher Descktop and, from Diagnostics section, activate the Container Engine setting (via Mute enabling).

## DOCKER IMAGE REPOSITORY
List images built into the repository

## DOCKER REPOSITORY CLEANING
When a out of space is arriving that avoid to build new image template, clean the workstation system with command line:
`docker system prune -a`

## CHECK IMAGE
Scan image vulnerabilities from shell command line:
`docker scan cybnity/jre-tee`

## START IMAGE
Run it from shell command line:
`docker run -it --rm cybnity/jre-tee`

Run a docker container without shell console (as a daemon without interaction) or without -d argument to maintain shell console opened from shell command line:
`docker-compose up -d`

Check that the container is running from shell command line:
`docker ps`

## MONITOR
Read logs from shell command line:
`docker-compose logs -f`

## STOP IMAGE
Kill container from shell command line:
`docker-compose kill`

or

`docker kill cybnity/jre-tee`

## PUBLISH TO DOCKER REPOSITORY
Create tag for built java container distribution specific to CYBNITY registry managed by docker.io from shell command line:
`docker tag cybnity/jre-tee cybnity/jre-tee:latest`

Push docker image to online CYBNITY private Docker repository from shell command line:
`docker push cybnity/jre-tee:latest`

## RUN IMAGE IN MINIKUBE

- Deploy a container as a Kubernetes Pod

``` shell
kubectl run cybnity-frontend --image=cybnity/web-reactive-frontend --image-pull-policy=Never
```

- Check that pod and embedded container is running

``` shell
kubectl get pods
```
