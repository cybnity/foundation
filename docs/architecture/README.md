## PURPOSE
This section is presenting some architectures overview regarding the deployable systems delivered by CYBNITY open source program.

# SYSTEMS ARCHITECTURE
Domain-Driven approach is implemented regarding the structure of the applicative components.

The packaging strategy applied on the project provides all the systems ready for execution into a virtual infrastructure (e.g containers) supported by physical infrastructure(s).

![image](systems-architecture.png)

## APPLICATIVE SYSTEMS
Each domain service is supported by an independent component which can be deployed in standalone life cycle and which manage the dependencies with other components like specified by the contexts integration model.

The internal components of an application are called *microservices* which implement the business processes (e.g feature modules under the application perimeter's responsibility) regarding a domain context.

## INFRASTRUCTURE SYSTEMS
Several main systems are implemented to be deployed as Internet platform and/or to be executed into a mobile infrastructure (e.g specific components on laptop required by mobile rapid response force members during a move into an incident zone).

A global systems registry allows the discovery of systems to link the context with dynamics approach (at runtime).

#
[Back To Summary](../README.md)