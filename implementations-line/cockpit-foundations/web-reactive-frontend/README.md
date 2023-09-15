## PURPOSE
Presentation of implementation project providing frontend web application to final users of the CYBNITY software.

# DESIGN VIEW
Presentation of the design models regarding the web application:
- [User Interface layer](doc/designview-ui-layer.md)

# IMPLEMENTATION VIEW
## React Application Structure
The ReactJS implementation project is organized with a capabilities domain-oriented structure as defined by the Cockpits Foundation Capabilities Model.

### src/components sub-folder
The common technical components (e.g standard button, reusable smart form) are defining in this area and can reused anywhere in the user interface.

The universal __src/components__ folder include components that are shared across domains.

### src/media sub-folder
This directory is dedicated to the common media files (e.g logo, standard basis background, sound file) which are commonly used by several front ui components. In this directory, the embedded media are usable by reactive components.

### src/fonts sub-folder
Contains the specific fonts files reusable into the global and/or features CSS styles.

### src/features sub-folders
Each ReactJS component have its own folder where each one include dedicated styling, types and testing logic.

- Scheduling folder regarding __Actions & Scheduling UI module__
- Assets folder regarding __Assets & Perimeters Protection UI module__
- Automation folder regarding __Automation UI module__
- Awareness folder regarding __Awareness & Training UI module__
- Situations folder regarding __Behaviours/Situations Anticipation & Control UI module__
- Commandment folder regarding __Commandment UI module__
- Coordinations folder regarding __Coordinations UI module__
- Dashboards folder regarding __Dashboard & Threshold__)
- Maneuvers folder regarding __Defensive Maneuvers UI module__
- Responses folder regarding __Defensive Responses & Controls UI module__
- Goals folder regarding __Goals & Evidences UI module__
- Strategy folder regarding __ISMS & Strategy UI module__
- Missions folder regarding __Missions & Programming UI module__
- Excellence folder rearding __Operational Excellence UI module__
- Recovery folder regarding __Operational Recovery UI module__
- Cartography folder regarding __Operations Cartography UI module__
- SKPI folder regarding __SKPI & Dashboard UI module__
- Stakeholders folder regarding __Stakeholders & Responsibilities UI module__
- Compliances folder regarding __Standards & Compliances UI module__
- Risks folder regarding __Threat/Risks Prevention & Treatment UI module__
- Vulnerabilities folder regarding __Vulnerabilities Prevention & Treatment UI module__

For each folder of feature component, several files are defined:
- index.js that represents the public interface of the folder where everything gets exported that's relevant to the outside world
- component.js that holds the actual implementation logic of the component
- test.js
- style.css

### src/main sub-folder
Dedicated to Docker image assembly controlled by Maven build tool that package the final deployable container image of the frontend web app.

### server folder
Include files required by the Express http server implementation.

### public folder
Include all the files and contents (e.g media files) that are public accessible over the http service (e.g health check basic html file, favicon for browsers...). Be carefull that media required by components shall not be included in this folder!

Files hosted in this directory shall only be referenced by HTML pages (e.g over %PUBLIC_URL% folder location variable name).

### node_modules folder
Contains all node packages that have been installed according to the specifics versions defined into the __package.json__ file.

# COCKPIT-FOUNDATION COMPONENTS
The source codes managed in this area of Foundation project are about the capabilities provided to the users (e.g web ui services provided to user's web browser) via the user interface layer.

The implementation source codes realizes interactive functions provided by __UI Modules__ (UI layer providing functional or technical capabilities) to deliver User eXperiences (UX) to the final users of CYBNITY solutions.

## UI Components Styling
The React bootstrap stack is integrated and reused for theming of the UI layer components.

See [React Bootstrap documentation](https://react-bootstrap.github.io/docs/getting-started/introduction) for more help.

### Customization
Application customization of the Bootstrap theme or any Bootstrap variables is defined in the custom Sass file __src/custom.scss__.

See [SASS documentation](https://getbootstrap.com/docs/5.3/customize/sass/) for help on customization via variables redefinition.

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)