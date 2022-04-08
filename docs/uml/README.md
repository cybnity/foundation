## PURPOSE
Welcome on the CYBNITY technical documentation using Unified Modeling Language (UML) notation for analysis and design activities.

The source artifacts are managed into this directory and maintained via the [Eclipse Papyrus](https://www.eclipse.org/papyrus/) open source tool.
Each Papyrus model file (.di) include internal structure hosting specific diagrams relative to its scope of documentation.

# FUNCTIONAL VIEW (Use Cases)
- Show the functionality of the system(s) as perceived by the external actors;
- Exposes the requirements of the system.

## Artifacts
Source file named "Use case view":
- Static aspects (structural diagrams): use cases;
- Dynamic aspects (behavioral diagrams): interactions, statecharts, activities.
- Model sub-packages:
  - Each context of the software (e.g Domain context) is described in a separate sub-package. 
  
## Usage
Formalizes software functional and/or technical analysis according to the functional and technical requirements.

# DESIGN VIEW (Logical Components)
- Sub-capture how the functionality is designed inside the domain contexts;
- Logical view of systems and sub-systems.

## Artifacts
Source file named "Design view":
- Static aspects (structural diagrams): classes, objects;
- Dynamic aspects (behavioral diagrams): interactions, statecharts, activities, sequences.
- Model sub-packages:
  - Cockpits Foundations
    - Captures how the capabilities provided by the Cockpits User Interfaces (UI) are designed inside the UI layer).
  - Feature Modules
    - Show how the internal functionalities (e.g logical features) are designed inside each domain context, in terms of the static structure and dynamic behavior;
    - Captures the vocabulary of the problem(s) space(s) and solution space per domain context;
    - Captures the sub-interface's capabilities per domain context (e.g context specification), sub-layers (e.g Domain-Driven-Development oriented) and realization of context described in specification use-case-view.
    - Each module include a sub-package par layer of design (Domain-Drive-Development)
      - ui layer
      - application layer
      - domain layer
      - infrastructure layer

## Usage
Formalizes the specification of the software and sub-components produced during the solution analysis and technical design activities.

# PROCESS VIEW (Executions)
- Show the concurrency of the system(s);
- Encompasses the threads and processes that form the system's concurrency and synchronization mechanisms.

## Artifacts
Source file named "Process view":
- Static aspects: equals to design view's diagrams, with focus on the active classes that represent theses threads and processes.
- Model sub-packages:
  - Performance
  - Scalability

## Usage
Describes execution models and synchronization rules, identified during the technical design phase and implementation rules definition.

# IMPLEMENTATION VIEW (Technical Components and Structures)
- Show the organization of the core components and files (e.g source code, setting files);
- Packaging models and dependencies distribution;
- Addresses the configuration management of the system's releases.

## Artifacts
Source file named "Implementation view":
- Static aspects (structural diagrams): components, packages;
- Dynamic aspects (behavioral diagrams): interactions, statecharts, activities.
- Model sub-packages:
  - Configuration-management
  - System-assembly
	
## Usage
Formalizes the maintenance documentation aligned with source codes developed, including specificities regarding technologies (e.g language requirements) and frameworks (e.g implementation templates, protocols) used for implementation of the software.

# DEPLOYMENT VIEW (Systems & Applications)
- Show the deployment of the systems in terms of physical architecture;
- Encompasses the node that form the system's hardware topology (e.g type of infrastructure components, network, virtual environments) on which the system executes (e.g resources requirements, runtime platform);
- Addresses the distribution (e.g flow opening), delivery (e.g procedures to respect), and installation (e.g resource prerequisites) of the parts that make up the physical system.

## Artifacts
Source file named "Deployment view":
- Static aspects (structural diagrams): components, deployment.
- Model sub-packages:
  - Installation
  - Delivery
  - System-distribution
  - System-typology

## Usage
Describes the environment(s), infrastructure and operating conditions required to install, activate and operate the systems safely.

[Back To Summary](../)
