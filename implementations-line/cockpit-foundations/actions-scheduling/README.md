# PURPOSE
This capability domain is also identified (e.g artifacts labels, sub-projects names) by the ACSC acronym.
This domain includes several types of modules managed via dedicated sub-projects implemented according to the official Techstack technologies.

# COMPONENTS & SYSTEMS BUILD
## JAVA COMPONENTS
* From shell command line executed from the [features](acsc-features) sub-directory:

  ```shell
  mvn clean install
  ```

All the prototype components are built and available into the local Maven artifacts repository.

## REACTIVE BACKEND MODULE
During coding activity of this component, the Vert.x modified contents can be considered by the runtime with the shell command lines from the [backend](acsc-ui-modules/acsc-backend) sub-directory:
* Package the server service:

  ```shell
  mvn package
  ```

## REACTIVE WEB FRONTEND UI MODULE
### ReactJS components layer
Prerequisites: install Node (see [Nodejs](https://nodejs.org/en/) documentation) and NPM tools according to the development workstation's operating system.

* Check the Node and NPM versions supported by the development workstation via shell command line:

  ```shell
  node --version

  npm --version
  ```

* Navigate into the [frontend](acsc-ui-modules/acsc-frontend) sub-directory

* Install all the frontend project's required modules relative to NodeJS with shell command line (command should be executed under `sudo` to avoid problem of some modules directories installation):

  ```shell

  // install project required modules
  sudo npm install

  // check the security vulnerability and report found issues
  sudo npm audit

  // fix possible sub-modules vulnerabilities
  sudo npm audit fix --force

  // upgrade to the latest version (install globally)
  sudo npm i -g npm-check-updates

  // detect which packages have newer versions
  sudo ncu -g

  // remove the lock on packages and install the latest versions
  sudo npm update

  // check outdated modules (show latest versions compared to versions specified in package.json)
  sudo npm outdated
  ```

#### Tutorial & technologies documentations
- Vert.x/ReactJS: documentation about integration between Vert.x and ReactJS is available on [Eclipse Vert.x How-To](https://how-to.vertx.io/single-page-react-vertx-howto/).
- React-Bootstrap library: documentation is available on [React Bootstrap](https://react-bootstrap.github.io/getting-started/introduction/) github web site.
- ReactJS/Keycloack: tutorial about [security of ReactJS routes with Keycloack](https://cagline.medium.com/authenticate-and-authorize-react-routes-component-with-keycloak-666e85662636).

# SYSTEMS DEPLOYMENT & RUN
## REACTIVE CAPABILITIES BACKEND SERVER

### Start of deployable elements
* Start the server Vert.x server-side backend system via Maven command line:

  ```shell
  mvn exec:java
  ```

## REACTIVE WEB FRONTEND UI SERVER

### Start of deployable elements
- Infrastructure servers: the start of infrastructures services (e.g Redis server, Single-Sign-On server) are __required before to execute the start of UI layer's systems__.
- Frontend web app: read instructions allowing to manage the execution of the Node.js frontend into the sub-project [README.md file](acsc-ui-modules/acsc-frontend/README.md).

# INTEGRATION TEST PROTOCOL
## TEST OF BACKEND (static contents)

### Endpoints validation

### Capabilities validation

## TEST OF FRONTEND (static contents and dynamic interactions)

### Endpoints validation

### Interactive capabilities validation (basic Smoke Tests)
