## PURPOSE
Presentation of the transversal framework components provided via several sub-projects dedicated to specific utilities.
Each framework element (e.g library, container template) and sub-project is able to be reused by the CYBNITY software projects as common and generic technical elements.

# FRAMEWORK LIBRARIES
Projects providing utility classes and specification components as Java library specialized per architecture or technical requirement area.

- [Support library](support/README.md): java library of utility elements for development and support of CYBNITY software implementation (e.g annotations for requirements linking to source codes)
- [Immutable library](immutable/README.md): java library providing essential elements to develop an immutable software based architecture
- [Domain library](domain/README.md): java library providing essential elements to develop a Domain-Driven Design (DDD) software based architecture

# VIRTUALIZATION CONTAINER TEMPLATES
Projects providing templates of Docker images which are usable as bases for the assembly and build of CYBNITY system modules containerized.
Each Docker template generation is designed and build via Maven plugin in a structured POM project.

- [Java TEE container](java-tee-container): Trusted Execution Environmnent (TEE) template of Eclipse Temurin JRE with Linux operating system
- [Services Registry container](services-registry-container): Zookeeper template
