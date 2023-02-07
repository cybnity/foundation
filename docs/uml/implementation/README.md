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

### Java Packages Projects Structures
The Java components projects are organized via a structure of Maven project's sub-modules regarding all the source codes developed by the team.
![image](Implementation_projects_structure.PNG)

### Infrastructure Modules Projects Structures
The Terraformed module regarding the CYBNITY deployable and executable infrastructure components are organized via a [structure of Terraform projects](/implementations-line/systems/README.md).

### Development Tutorials and Conventions
Several coding documentations are available into the [how-to sub-directory](how-to) for assistance to developers.

The CYBNITY coding norms are considered like mandatory to apply and can be controlled via quality check process (e.g by Continuous Integration chain, source code reviews) and are origins for reject of source codes changes considered like violation of the documentation norm.

The CYBNITY conventions are more permissive because are considered as optional, without mandatory quality check (based on the developer motivation and skills), but can be transformed in norm after a period of application.
#### Conventions
- [Coding conventions](how-to/coding-conventions.md)
#### Norms
- Branch tagging norm

#
[Back To Parent](../)
