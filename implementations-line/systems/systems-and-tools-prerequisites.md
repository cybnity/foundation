## PURPOSE
Presentation of the requirements relative to the installation and/or operating of systems that are used into the CYBNITY infrastructures.

Any type of prerequisite can be defined by a selected tool according to its version (e.g defined by its open source project documentation), or can be specifically identified by CYBNITY for the kind of usage implemented (e.g limited to a specific environment size defined per IT activity).

This catalog of identified requirements allow to help the definition of resources (e.g minimum CPU, minimum memory size, ideal storage size) implemented at the infrastructure level (e.g Platform As A Service, or Infrastructure As A Service layer) and considered as part of the CYBNITY deployment view.

# CYBNITY TOOLS PREREQUISITES
Theses prerequisites are about tools supporting the software activities (e.g development, test, operating, monitoring...) executed to deliver CYBNITY artefacts and services. They shall be considered as requirements for support tooling used (e.g by projects developers) to build and maintain the CYBNITY software components.

Each prerequisite is categorized according to the type of resource allocation need (e.g software or hardware layer).

## ALL ENVIRONMENTS
Tools and required resources supporting all the environments involved into the CYBNITY delivery activities.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Spinnaker|- vCPU: <br>- RAM: <br>- Storage: |- CPU: | |

## LOCAL ENVIRONMENT
Standalone or sandboxed environment used for projects development.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Minikube||||

## DEV ENVIRONMENT
Centralized environment between multiples projects and/or developers in a shared tools approach.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Minikube||||

## QA ENVIRONMENT
Resources and tools allocated for quality check activities (e.g remotely controlled by the CI tools) in a centralized and automated approach. Optional additional resources can be required for simulation activities supporting the demonstration and validation activities.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|K3S||||

## LIVE ENVIRONMENT
Production resources requirements for deployment and operating of CYBNITY solutions.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Kubernetes||||


# CYBNITY SOLUTIONS PREREQUISITES
These prerequisites are about technologies and/or solutions (e.g open source database systems) included or compatible with the CYBNITY software versions, that shall be considered as requirements for deployment and runtime of CYBNITY deployable systems.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Redis||||
|Kafka||||
|Janusgraph||||
|Cassandra||||
