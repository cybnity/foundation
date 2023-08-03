## PURPOSE
Presentation of implementation project providing frontend web application to final users of the CYBNITY software.

# PROJECT
## React Application Structure
The ReactJS implementation project is organized with a capabilities domain-oriented structure as defined by the Cockpits Foundation Capabilities Model.

### src/features sub-folders
Each ReactJS component have its own folder where each one include dedicated styling, types and testing logic.

- Scheduling folder regarding **Actions & Scheduling UI module**
- Assets folder regarding **Assets & Perimeters Protection UI module**
- Automation folder regarding **Automation UI module**
- Awareness folder regarding **Awareness & Training UI module**
- Situations folder regarding **Behaviours/Situations Anticipation & Control UI module**
- Commandment folder regarding **Commandment UI module**
- Coordinations folder regarding **Coordinations UI module**
- Dashboards folder regarding **Dashboard & Threshold**)
- Maneuvers folder regarding **Defensive Maneuvers UI module**
- Responses folder regarding **Defensive Responses & Controls UI module**
- Goals folder regarding **Goals & Evidences UI module**
- Strategy folder regarding **ISMS & Strategy UI module**
- Missions folder regarding **Missions & Programming UI module**
- Excellence folder rearding **Operational Excellence UI module**
- Recovery folder regarding **Operational Recovery UI module**
- Cartography folder regarding **Operations Cartography UI module**
- SKPI folder regarding **SKPI & Dashboard UI module**
- Stakeholders folder regarding **Stakeholders & Responsibilities UI module**
- Compliances folder regarding **Standards & Compliances UI module**
- Risks folder regarding **Threat/Risks Prevention & Treatment UI module**
- Vulnerabilities folder regarding **Vulnerabilities Prevention & Treatment UI module**

For each folder of feature component, several files are defined:
- index.js that represents the public interface of the folder where everything gets exported that's relevant to the outside world
- component.js that holds the actual implementation logic of the component
- test.js
- style.css

An universal **src/components** folder include components that are shared across domains.

### server folder
Include files required by the Express http server implementation.

### public folder
Include all the files and contents (e.g media files) that are publically accessible over the http service (e.g health check basic html file, favicon for browsers...).

### Other
Additional folders and artifacts are hosted by the project structure according to:
- **main folder**: dedicated to Docker image assembly controlled by Maven build tool that package the final deployable container image of the frontend web app


# COCKPIT-FOUNDATION COMPONENTS
The source codes managed in this area of Foundation project are about the capabilities provided to the users (e.g web ui services provided to user's web browser) via the user interface layer.

The implementation source codes realizes interactive functions provided by **UI Modules** (UI layer providing functional or technical capabilities) to deliver User eXperiences (UX) to the solution final users of CYBNITY solutions.
