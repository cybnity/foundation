---USE CASE VIEW---
- Show the functionality of the system as perceived by the external actors;
- Exposes the requirements of the system.
- Artifacts:
	- static aspects (structural diagrams): use case diagram;
	- dynamic aspects (behavioral diagrams): interaction diagram, state chart diagram, activity diagram.
- Usage: requirement.

---DESIGN VIEW---
- Sub-capture how the functionality is designed inside the domain contexts.
- Logical view of systems and sub-systems.
	COCKPIT FOUNDATION
	- Capture how the cockpit UI capabilities are designed inside the foundations.
	
	FEATURE MODULE
	CONTENTS:
	- Show how the internal functionality (e.g logical features) is designed inside each domain context, in terms of the static structure and dynamic behavior.
	- Capture the vocabulary of the problem space and solution space per domain context.
	- Capture the sub-interface's capabilities per domain context (e.g context specification), sub-layers (e.g Domain-Driven-Development oriented) and realization of context described in specification use-case-view.

- Artifacts: specification.
	- static aspects: class diagram, object diagram;
	- dynamic aspects: interaction diagram, state chart diagram, activity diagram.
- Usage: analysis, design.

---PROCESS VIEW---
- Show the concurrency of the system.
- Encompasses the threads and processes that form the system's concurrency and synchronization mechanisms.
- Artifacts:
	- static aspects: equals to design view diagram, with focus on the active classes that represent theses threads and processes.
- Usage: design, implementation.

---IMPLEMENTATION VIEW---
- Show the organization of the core components and files.
- Addresses the configuration management of the system's releases.
- Artifacts:
	- static aspects: component diagram;
	- dynamic aspects: interaction diagram, state chart diagram, activity diagram.
- Usage: implementation.

---DEPLOYMENT VIEW---
- Show the deployment of the system in terms of the physical architecture.
- Encompasses the node that form the system's hardware topology on which the system executes.
- Addresses the distribution, delivery, and installation of the parts that make up the physical system.
- Artifacts:
	- static aspects:
- Usage: design, implementation.