## PURPOSE
Developer help regarding the management of the Docker image template regarding a basic Java Runtime Execution container reusable by any CYBNITY server containerized.

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
Build a Docker image of Linux/Java runtime environment container from shell command (including force remove of intermediary container) from shell command line:
`docker-compose build --force-rm`

Build dockerfile (without docker-compose help) from directory from shell command line:
`docker build -t cybnity/jre-container .`

## CHECK IMAGE
Scan image vulnerabilities from shell command line:
`docker scan cybnity/jre-container`

## START IMAGE
Run it from shell command line:
`docker run -it --rm cybnity/jre-container`

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

`docker kill cybnity/jre-container`

## PUBLISH TO DOCKER REPOSITORY
Create tag for built java container distribution specific to CYBNITY technology from shell command line:
`docker tag jre-container cybnity/jre-container:latest`

Push docker image to online CYBNITY private Docker repository from shell command line:
`docker push cybnity/jre-container:latest`

## RUN IMAGE IN MINIKUBE
``` shell
kubectl run cybnity/jre-container --image=cybnity/jre-container --image-pull-policy=Never
# Check that it's running
kubectl get pods
```
