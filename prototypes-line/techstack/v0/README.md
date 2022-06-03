## PURPOSE
This project space is dedicated to the prototyping of Foundation TechStack initial version.
Main goal is to validate the compatibility of technologies set assembly, their promises reality and to support the acceptance step (step 2) of the [demonstrator V0](https://github.com/cybnity/foundation-techstack/blob/0aa4d70e4b1c880e1ebec00f51ea7dd7947fee4d/demonstrators-line/demonstrator-v0/technologies-stack-analysis.md)

# PROTOTYPE PERIMETER
## EVALUATED SET
| TECHNOLOGY | VERSION | COMMENTS | SUPPORT |
| :--- | :--- | :--- | :--- |
| ReactBootstrap | | | |
| Eclipse Vert.x SockJS Client | | | |
| ReactJS | | | |
| Eclipse Vert.x Core | | | |
| Eclipse Vert.x Web | | | |
| Gravitee | | | |
| Apache Directory Server | | | |
| midPoint | | | |
| Keycloack | | | |
| Jedis | | | |
| Redis | | | |
| Apache Kafka | | | |
| Apache Flink | | | |
| Apache Solr | | | |
| MongoDB | | | |
| Telegraf Agent | | | |
| Grafana | | | |
| InfluxDB | | | |
| Spring Core | | | |
| Spring Boot | | | |
| Apache Zookeeper | | | |
| Liberica JDK | | | |
| Eclipse OpenJ9 8/11 JRE | | | |
| Consul | | | |
| Linux Ubuntu LTS (minimal for Docker)| | | |
| Docker | | | |
| MicroK8s | | | |
| Kubernetes | | | |

## TARGETED ACTIONS
- Installation of each technology according to the Foundation architecture
- Test of configuration per technology allowing their technical collaboration
- Implementation of a basic test (e.g HelloWorld) with basic source codes allowing to check the good integration between each technology
- Update of results and observations into the technology stack analysis page of Demonstrator V0

# PROTOTYPE IMPLEMENTATION

## SYSTEMS BOUNDARY
A specific systems area is built and prepared for execution test as Proof-Of-Concept (POC) of integrated technologies.

The main applications focused for the POC is:
- **Asset Control** application as deployable CYBNITY system including a sample of read capability (e.g read a sample of asset description)
  - **Frontend UI server** (asset control UI frontend implementation module) **as UI system**;
  - **Backend UI server** (asset control UI backend implementation module) **as server-side backend system**;
  - **Domain Gateway server** (asset control application implementation module) **as domain application services layer system** managing stateless anti-corruption layer;
  - **RTS Computation Unit server** (asset control RTS computation unit implementation module) **as domain model system** providing domain feature about Asset.

The main infrastructure services focused for the POC are:
- For integration tests between UI layer's components and application layer
  - **Users Interactions broker** (users interactions infrastructure implementation module) **as UI layer's collaboration area**.
- For integration tests between collaboration spaces and gateway layer
  - **Domains Interactions broker**
- For check of security integration tests between applicative systems
  - **Access Control SSO server** (access control and Single-Sign-On infrastructure implementation module) **as security manager of frontend access**;
  - **Identities & Access management server** (identity and access management infrastructure implementation module) **as UIAM system**;
  - **Secret management server** (secret management infrastructure implementation module) **as strongbox system**.
- For check of tracking capabilities regarding systems
  - **Event Logging server** (event logging infrastructure implementation module) **as activity/event logs store**.

## TECHNOLOGY
Several technologies are selected into the stack version for implementation of components and systems.

| SYSTEM | TYPE | TECHNOLOGIES | COMMENTS |
| :--- | :--- | :--- | :--- |
|Asset Control FrontEnd server|Web Reactive FrontEnd|- ReactBootstrap<br>- ReactJS<br>Vert.x SockJS Client<br>- Google Chrome web browser| |
|Asset Control BackEnd server|Reactive BackEnd Server|- Vert.x Web<br>- Jedis<br>- Vert.x Core<br>- OpenJ9 JVM<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|JSON/HTTPS over SSO|
|Asset Control & SSO server|Security Services|- Keycloack<br>- OpenJ9 JVM<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|Token management for front/backend's user access|
|Identities Access Management server|Security Services|- midPoint<br>- Apache Directory Server<br>- OpenJ9 JVM<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|including test account allowing call of Access domain read feature, and access check by application layer when coming from UI layer|
|Secret Management server|Security Services|- Vault<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|test of storage/retrieve of user token used by Asset Control & SSO server|
|Users Interactions broker|Users Interactions Space|- Redis Cluster<br>- Telegraf Agent<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|Telegrag agent (plugin for Redis cluster) push monitoring to Event Logging Server|
|Event Logging server|Logging|- InfluxDB<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|Logs repository regarding Redis instances' activities|
|Asset Control Domain Gateway server|Domains Gateway Server|- Java Kafka POJO<br>- Zookeeper Client<br>- Jedis<br>- Kafka Connector<br>- Spring Core<br>- SpringBoot<br>- OpenJ9 JVM<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s|Sample code which integrate the request (e.g validate a request parameter like "asset name to read") of UI layer to domain, and execute the requested feature (e.g read of an asset description) via delegation to a RT computation unit (e.g domain model of Asset Control implementing the Security Feature named Asset);<br>Java Processor/Consumer as domain application service layer|
|Domains Interactions broker|Events Brokers Cluster (Domains Interactions Space)|- Kafka Broker instance<br>-Zookeeper Registry<br>- OpenJ9 JVM<br>- Ubuntu OS<br>- Docker Image<br>- MicroK8s| Kafka cluster|
|RTS Computation Unit server|Real-Time Stream Computation Unit|- Apache Flink<br>- Zookeeper Client<br>- Kafka Connector<br>- Flink CEP<br>- OpenJ9 JVM<br>- Docker Image|Asset feature java implementation with Flink;<br>Flink CEP test for command chain pattern implementation that build the Asset description requested by the UI layer and provider by the Domain model|
