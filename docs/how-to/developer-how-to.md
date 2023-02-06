## PURPOSE
Presentation of several utilities, guides and documentations helping the software developers to manage, use, develop and/or ensure a developer role on the CYBNITY program.
- [Public documentation](#cybnity-public-documentation)
- [Minimum prototyping platform](#minimum-prototyping-platform)
- [Development environment](#development-environment)
- [Open source projects platform](#cybnity-open-source-projects-platform)
- [Integrated development environment](#integrated-development-environment)
- [System images registry](#system-images-registry)
- [Continuous integration/delivery infrastructure][#continuous-integration--delivery-infrastructure]

## CYBNITY PUBLIC DOCUMENTATION
<details><summary>SOFTWARE PROGRAM DOCUMENTATIONS</summary>
<p>

Free access to requirements (e.g security, functional) and architecture (e.g logical design of systems and concepts) documentations helping to understand the general architecture and design requirements that are a frame of technology implementation.

This documentations are available online via www.cybnity.org website and managed by the CYBNITY core team (people who are only able to modify these documentations) into a Notion knowledge database server.

</p>
</details>
<details><summary>DELIVERY PLANS</summary>
<p>

The project roadmap is maintained by the CYBNITY core team into the GitHub Foundation docs sub-directory named [managed-programs](../governance/managed-programs/README.md)., giving a permanent overview of the CYBNITY trajectory and allowing to understand how some specific implementation projects iterations are prioritized.

It give also a view on the engagement plan regarding next main steps of the CYBNITY implementation vision.

</p>
</details>

## MINIMUM PROTOTYPING PLATFORM
<details><summary>ROUCH SKETCHS</summary>
<p>

Some basic concepts and sketchs are created by the CYBNITY UX/UI team's members regarding the software user interfaces, allowing to understand how the final users can have rich experience provided by the developed UI layer components.

The sketchs are created and maintained via a Figma workplace in reserved access.

</p>
</details>
<details><summary>SIMPLISTIC & REAFINED WIREFRAMES</summary>
<p>

To validate the UX/UI concepts with a specific community of early adopters (e.g RSSI, CISO of companies) as co-designer of CYBNITY needs and User Interfaces (UI), some specific user scenario are designed via wireframe created on Figma to validate the UI approach and to test several types of navigation to reach the functional goals targeted by the CYBNITY persona.

The Figma tool is used to create and maintain the wireframe deliverables. Mainly, this wireframes are accessible online by the CYBNITY team (e.g validator of UX/UI during interview with co-designers).

</p>
</details>
<details><summary>DEMONSTRATION & REAFINED MOCKUPS</summary>
<p>

When User eXperience (UX) and main User Interface (UI) concepts are had been validated with co-designers and community of testers, the reafined wireframes are enhanced with a look & feel approach.

The CYBNITY UI team work on the identification of best visual components, style of color, position of UI components and/or on interaction effects allowing to deliver a visual experience performant and beautiful for the final users.

The reafined mockups are maintained into the Figma tool, and are available online for the CYBNITY developers (via reserved accesses) as a support to te development of UI layer (e.g front end project of CYBNITY domains).

</p>
</details>
<details><summary>CYBNITY DEMONSTRATORS</summary>
<p>

Demonstration platform and autonomously executable software are created as demonstrators of CYBNITY version that allow to make demo to final users, to early adopters and/or to CYBNITY partners to make validation about usage (e.g business use case, specific demo of a CYBNITY feature to a specific industry) and to collect feedbacks (e.g allowing to improve final developed version of a feature).

Demonstrator are developed by CYBNITY UX/UI team's members in several technologies. For example, techstack demonstrators are considered like Proof-Of-Concept regarding a technology integrated with a CYBNITY software version. Another example is a web UI demonstrator regarding some specific usage scenario regarding a business process (e.g web application generated from Figma mockups including interactive behaviour).

</p>
</details>

## DEVELOPMENT ENVIRONMENT
<details><summary>INFRA-AS-CODE RUNTIME</summary>
<p>

Execution platforms are used like tool for industrialization of the system layer allowing to build executable software in Cloud environment and/or into specific targeted infrastructure during the technical development phase.

The tools used for IaC system are respecting the official Techstack (e.g Docker, Terraform, Helm, Kubernetes, Minikube) to develop the systems layer (see [implementations-line](../../implementations-line)) and are implemented to support several types of runtime approach:
- Execution on a standalone developer's workstation (Minikube)
- Execution on a cloud server instance (e.g OVH public cloud instance)

</p>
</details>

## CYBNITY OPEN SOURCE PROJECTS PLATFORM

## INTEGRATED DEVELOPMENT ENVIRONMENT
<details><summary>SOFTWARE FEATURES BRANCHES</summary>
<p>

The CYBNITY open source features are developed on this repository and into several additional repositories on GitHub.

</p>
</details>
<details><summary>TOOLING EXTENSIONS</summary>
<p>

Several development tools are used to manage the source codes, the software build industrialization, the configuration of the component, and/or the deployment into environments (e.g dev, test, integration) which are aligned with the recommendations of the official Techstack like:
- Maven (java software components build)
- Node.js & NPM (front end coding)
- GitHub desktop client (source code versioning)
- GitHub Actions (CI/CD pipelines)
- Concordion (test)

</p>
</details>

## SYSTEM IMAGES REGISTRY
<details><summary>SYSTEM IMAGES REPOSITORY</summary>
<p>

An instance of public repository managing the availability of Docker templates built by the CI chain is deployed.

Each developer can reuse templated Docker images automatically diffused by the CYBNITY CI chain.

</p>
</details>

## CONTINUOUS INTEGRATION / DELIVERY INFRASTRUCTURE
<details><summary>ARTIFACT VERSIONS MAVEN REPOSITORY</summary>
<p>

The java packages are built via Maven on the workstation's m2 repository of each developer by default.

When a source code version of CYBNITY project is committed and/or merged into a GitHub repository, the Continuous Integration (CI) chain implemented via some GitHub Actions (see [.github/workflows/](../../.github/workflows) of each GitHub repository) is automatically executed according to the pipelined build process.

The execution of pipelined build process can be followed by any developer since the CYBNITY [Foundation GitHub Actions section](https://github.com/cybnity/foundation/actions).

A CYBNITY remote repository is automatically maintained up-to-date regarding specific versions of built components like:
- snapshot versions of sub-projects __integrated by the CI in staging branch only__
- tagged versions of sub-projects (e.g feature, hotfix, and fix branches)

A Maven remote repository is reserved to the CI chain and is only usable in read permission by the public project and/or in dependency by other CYBNITY's repositories (e.g a repository of a domain application which need a dependency to a staging version).

None account other than the CI account (confidentially used by the GitHub CI chain) can be opened for any other usage, and the default local repository of each developer's workstation is used by default for any build of sub-version during the development phase.

The configuration of repository usage is implemented into the Maven parent pom.xml file of each project (e.g in Foundation project; in each specific application domain repository's `implementation-line` directory) and doesn't require any specific additional usage of Maven `settings.xml` file by the developers).

</p>
</details>
<details><summary>ENVIRONMENT</summary>
<p>

Some dedicated environment are implemented in support to the Continuous Integration (CI) and Continuous Delivery (CD) chains that are pipelined.

The definition of available environments is managed via:
- Maven profiles (e.g when a build, quality check and/or specific behaviour of the Maven lifecycle or plugin need to be customized according to a targeted environment). Becarefull, none application settings (e.g resources filtered with specific values used into an environment) should be managed via the Maven profiles. Only Maven specific behaviour can be configured via Profiles.
- GitHub project environments that are automatically managed and updated by the CI chain according the CYBNITY pipelined CI chain.

</p>
</details>

#
[Back To Parent](../)
