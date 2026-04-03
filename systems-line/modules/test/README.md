## PURPOSE
Several validation steps are allowed onto deployed CYBNITY systems and this space is managing dedicated test module projects.

A test module have missing to be deployed additionnaly to one or several CYBNITY modules (e.g feature module containerized as an independent K8S system), with ability to make functional or technical verification relative to its behaviour.

It can exist multiple types of testing modules used at several moment of the CYBNITY project life cycle, according to multiple specific missions (e.g validation of integration between systems modules; end-to-end validation between functions using sub-components and resources of an operational environment).

Each type of mission and testing modules managed in this area is defined into sub-folder.

# SYSTEMS INTEGRATION TESTING MODULES
Mission: verification of integration and behaviour conformity between several system modules of a CYBNITY version, allowing to confirm integration testing sign-off gate.

Usage:
- during integration quality stage
- deployed into QA environment

Project folder: [systems-integration-testing](systems-integration-testing/README.md).

# SMOKE TESTING MODULES
Mission: verification of critical functions regarding uninstable behaviour detected from initial build that need to be securized like always resolved.

Usage:
- during integration quality stage
- deployed into QA environment

# SANITY TESTING MODULES
Mission: verification of new functions and bugs fixed regarding stable version of a CYBNITY systems version. Reveal simple failures (e.g missing deployed resource/component/configuration). Focused on most important functionalities of systems new version.

Usage:
- during user acceptance test stage
- deployed into UAT environment, and/or into Green environment

# LOAD TESTING MODULES
Mission: verification of robustness and capacity of a CYBNITY systems version in situation of massive data load (e.g quantity and duration without interruption of user data entry).

Usage:
- during capacity and performance evaluation stage
- deployed into a Performance environment

# SCALABILITY TESTING MODULES
Mission: verification of reactivity abilities of a CYBNITY systems version when multiples and parallel usages (e.g users, integrated 3rd-party system) are executed.

Usage:
- during capacity and performance evaluation stage
- deployed into a Performance environment

# THROUGHPUT TESTING MODULES
Mission: verification of CYBNITY systems versions behaviour conformity in situation of network (e.g bandwith evolution) performance change, potentially generating an impact of CYBNITY systems version's SLA.

Usage:
- during capacity and performance evaluation stage
- deployed into a Performance environment

# LONGEVITY TESTING MODULES
Mission: validate CYBNITY systems version and UX stability and serviceability features over a longer period against appropriate load and stress conditions with real-time traffic and applications.

Usage:
- during capacity and performance evaluation stage
- deployed into a Performance environment
