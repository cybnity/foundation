## PURPOSE
Presentation of the implementation view regarding each layer (e.g user interface, application, domain, infrastructure) and technical structures supporting the design models.
The version of each diagram shown in this area is the latest produced via the source file (Implementation view).

# GUIDELINES & PRINCIPLES
Identify the guidelines and principles of implementation that are framing the implementation tactics (e.g distribution of codes, configuration via packaging model) and that help selection of implementation frameworks (e.g for dependencies injection, UI layer kit implementation, coupling of service components) according to a Domain-Driven Development approach.

## PRINCIPLES
Presentation of the implementation principles respected by the CYBNITY technical designers, developers and packaging managers.

### Application Packaging
The packaging of technical and business components is framed according to dependencies and embedding principles.

![image](Application_system_packaging_principle.PNG)

### Solution Assembly
The assembly of specific modules can be customized for delivery of a business solution (e.g alternative to a proprietary applicative solution covering only a specific security domain).

![image](security_solution_oriented_architecture.png)

## GUIDELINES

### Source Code Projects Structure
The CYBNITY open source is organized via a structure of Maven project's sub-modules regarding all the source codes developed by the team.
![image](Implementation_projects_structure.PNG)

#
[Back To Parent](../)
