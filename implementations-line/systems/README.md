## PURPOSE
Presentation of the projects dedicated to manage the provisioning and orchestration of delivered CYBNITY infrastructure module versions.

The management of several types of modules is approached with an integrated vision that mix the best pratices proposed by each used tool, into a global common and compatible architecture of the source codes projects.

To avoid risks (e.g large modules are slow, insecure, difficult to understand/test/review), some modularity requirements are respected for development and maintenance at a Production-grade infrastructure modules:
- Small modules
- Composable modules
- Testable modules
- Releasable modules

Several assembly projects are managed regarding the packaging of modular and deployable systems relative to independent and autonomous containerized systems of security features, and/or integrated applicative modules (e.g aggregation of CYBNITY domain's features).

### DEPLOYMENT VIEW
See the [deployment view documentation](../../docs/uml/README.md#deployment-view-systems--applications) for more detail on the design requirements that describe the environments, infrastructures and operating conditions required to install, activate and operate the systems safely.

# INFRASTRUCTURE PROJECTS
The infrastructure projects governs the provisioning management at the Kubernetes level as an Infrastructure-As-Code implementation.

## APPLICATION MODULES PROJECTS
Perimeter: packaged applicative or infrastructure modular systems as Services (Kubernetes service) that can be executed (e.g capability to be deployed into an environment).

Project type: Terraform implementation structure.

Description: main sub-folder is named `services` which contain a sub-folder per any type of modularized system supported by Infrastructure-As-Code implementation. An implemented module can represent a deployment module of an infrastructure Service (e.g Kubernetes service, CYBNITY application service) or System (e.g a cluster of backend application systems, a availability zone implemented by a Kubernetes Node).

## GENERIC INFRASTRUCTURE MODULES PROJECTS
Perimeter: generic, reusable and standalone modules for deploying infrastructure modules (e.g a public load balancer).

Project type: Terraform implementation structure.

Description: some specific sub-folders are created per type of generic technical module type (e.g all the modules relative to the networking activities are hosted into a `networking` folder) regarding generic/standalone modules (e.g only one instance of alb between several area or cluster of frontend application modules) is supported by a dedicated sub-folder named by its logical name (e.g alb regarding a Application Load Balancer as `network module name`). The goal is to define one time the generic/reusable modules which are not clusterized.

## DEPLOYMENT MODULES PROJECTS
Perimeter:  generic, reusable and standalone modules implementing the deployment of clusterized infrastructure/applicative modules (e.g a cluster of multiple CYBNITY application modules).

Project type: Terraform implementation structure.

Description one specific sub-folder is created for each type of clusterized module (e.g named specifically as a __function name__ equals to a suffix `-rolling-deploy` of the managed targeted module) into the `cluster` sub-folder. For example, a terraformed deployment module (e.g Auto Scaling Group module deployed in a rolling deployment approach the webserver-cluster of a application module, named `asg-rolling-deploy`) can do a zero-downtime rolling deployment betwen several instance of an applicative or infrastructure module.

## ENVIRONMENT MODULES PROJECTS
Perimeter: configuration setting projects of Environments (e.g Kubernetes clusters) that can be activated, operated, monitored and restored regarding packaged Services.

Project type: Terraform implementation structure.

Description: main folder is [modules](modules) which contain a sub-folder per __environment name__ (e.g local dev, integrated dev, test, staging & QA, live) equals to a Terraformed module where sub-modules can be operated. One configuration file (e.g .tf, .yaml) is implememented per cluster managed in the source codes repository including labeled deployment. One cluster defined by environment with fault tolerance and HA.
- Local (LOCAL): developer's workstation terraformed environment (e.g laptop with OS and Minikube runtime platform). This environment is also supported by Maven tool profile named `localhost` and activated by default during Java component build life cycle
- Integrated development (DEV): terraformed server infrastructure supporting a shared software development version (e.g temporary during a developers team validation between several features currently in coding step). This environment is also supported by Maven tool profile named `dev-deploy-environment` and activable during build life cycle with property `environment=dev-deploy`
- Quality Acceptance (QA): terraformed server infrastructure supporting a software version qualification process as test environment. This environment is also supported by Maven tool profile named `qa-environment` and activable during build life cycle with property `environment=qa`
- Production (LIVE): terraformed production server infrastructure supporting the applications and services hosted for the use by final users as a Live environment.

## SYSTEMS PROJECT STANDARD STRUCTURE
The integrated terraformed modules are managed via the standard project structure:

```
modules
├── live-env
│   ├── cluster
│   │   ├── <<clusterized module name>>-<<function>>
│   │   │   ├── main.tf
│   │   │   ├── outputs.tf
│   │   │   └── variables.tf
│   │   └── <<clusterized module name>>
│   │       ├── main.tf
│   │       ├── outputs.tf
│   │       └── variables.tf
│   ├── networking
│   │   └── <<network module name>>
│   │       ├── main.tf
│   │       ├── outputs.tf
│   │       └── variables.tf
│   └── services
│       └── <<CYBNITY application module/service name>>
│           ├── main.tf
│           ├── outputs.tf
│           └── variables.tf
├── qa-env
├── dev-env
└── local-env
```

Corresponding test and examples structures are implemented and should be equals to the implemented modules allowing to make multiple tests for each module, witch each example showing different configurations and permutations of how that module can be used.

```
test
├── qa-env
│   ├── cluster
│   ├── networking
│   └── services
├── dev-env
└── local-env

examples
├── qa-env
│   ├── cluster
│   │   └── <<clusterized module name>>-<<function>>
│   │       ├── one-instance
│   │       │   ├── main.tf
│   │       │   ├── outputs.tf
│   │       │   └── variables.tf
│   │       ├── auto-scaling
│   │       │   ├── main.tf
│   │       │   ├── outputs.tf
│   │       │   └── variables.tf
│   │       ├── with-load-balancer
│   │       │   ├── main.tf
│   │       │   ├── outputs.tf
│   │       │   └── variables.tf
│   │       └── custom-tags
│   ├── networking
│   └── services
├── dev-env
└── local-env
```
