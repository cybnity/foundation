## PURPOSE
Developer help regarding the management of the Docker image template regarding a basic Java Runtime Execution container reusable by any CYBNITY server containerized.
The Dockerfile of the project is automated via Maven plugin that generate in-memory file (based on docker plugin configuration implemented into the pom.xml file).

# DOCKER USAGE
A docker orchestrator should be started previously to execute any docker command line.

``` shell
# Start minikube
minikube start --driver=hyperkit --container-runtime=docker
# Export docker host and Docker daemon into the shell context variables
minikube docker-env
# Set docker env
eval $(minikube docker-env)
```

## BUILD OF IMAGE
Build process is managed via Maven.

See https://dmp.fabric8.io/ documentation's __docker:build chapter__ that present all the informations managed during the build step of the Dockerfile implemented by the Maven pom.xml.

### After Maven generation of Dockerfile
After `mvn clean package` execution, it's possible to execute additional command lines from the `target/docker/cybnity/jre-tee sub-directory/...` including the generated Dockerfile.

Build dockerfile (without docker-compose help) from directory from shell command line:
`docker build -t cybnity/jre-tee .`

## DOCKER IMAGE REPOSITORY
List images built into the repository

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
`docker tag jre-tee cybnity/jre-tee:latest`

Push docker image to online CYBNITY private Docker repository from shell command line:
`docker push cybnity/jre-tee:latest`

## RUN IMAGE IN MINIKUBE
``` shell
kubectl run cybnity/jre-tee --image=cybnity/jre-tee --image-pull-policy=Never
# Check that it's running
kubectl get pods
```
