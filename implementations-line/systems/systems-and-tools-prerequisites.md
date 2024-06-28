## PURPOSE
Presentation of the requirements relative to the installation and/or operating of systems that are used into the deployable environments detailed in each sub-folder (per module).

Any type of prerequisite can be defined by a selected tool according to its version (e.g defined by its open source project documentation), or can be specifically identified by CYBNITY for the kind of usage implemented (e.g limited to a specific environment size defined per IT activity).

This catalog of identified requirements allow to help the definition of resources (e.g minimum CPU, minimum memory size, ideal storage size) implemented at the infrastructure level (e.g Platform As A Service, or Infrastructure As A Service layer) and considered as part of the CYBNITY deployment view.

Theses prerequisites are about tools supporting the software activities (e.g development, test, operating, monitoring...) executed to deliver CYBNITY artefacts and services. They shall be considered as requirements for support tooling used (e.g by projects developers) to build and maintain the CYBNITY software components.

Each prerequisite is categorized according to the type of resource allocation need (e.g software or hardware layer).

# TRANSVERSAL PREREQUISITES
Tools and required resources supporting all the environments involved into the CYBNITY delivery activities.

## TOOLS
|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Halyard|- local machine (MacOS, Ubuntu/Debian), or VM (Ubuntu 18.04+, Debian 10+), or Docker container<br>- RAM: 12GB minimum||Halyard is a command-line administration tool that manages the lifecycle of a Spinnaker deployment, including writing & validating a deployment’s configuration, deploying each of Spinnaker’s microservices, and updating the deployment.<br>[Install and configure spinnaker](https://spinnaker.io/docs/setup/install/); [local install doc](https://spinnaker.io/docs/setup/install/environment/#local-debian); [docker install doc](https://spinnaker.io/docs/setup/install/halyard/#install-halyard-on-docker)|
|Spinnaker|RAM: 16Go minimum<br>|CPU: 4 cores minimum|[Install and configure spinnaker](https://spinnaker.io/docs/setup/install/)|

## CYBNITY EMBEDDED SYSTEMS
These prerequisites are about technologies and/or systems (e.g open source database systems) included or compatible with the CYBNITY software versions, that shall be considered as minimum requirements for deployment of CYBNITY systems.

|System / Solution|Software Layer|Hardware Layer|Documentations|
|:--|:--|:--|:--|
|Redis||||
|Kafka||||
|Janusgraph||||
|Cassandra||||

# PREREQUISITES PER MODULARIZED ENVIRONMENT
- [LOCAL-ENV](modules/local-env/README.md): standalone or sandboxed environment used for projects development.
- [DEV-ENV](modules/dev-env/README.md): centralized environment between multiples projects and/or developers in a shared tools approach.
- [QA-ENV](modules/qa-env/README.md): resources and tools allocated for quality check activities (e.g remotely controlled by the CI tools) in a centralized and automated approach.
- [LIVE-ENV](modules/live-env/README.md): production resources requirements for deployment and operating of CYBNITY solutions.

#
[Back To Home](README.md)
