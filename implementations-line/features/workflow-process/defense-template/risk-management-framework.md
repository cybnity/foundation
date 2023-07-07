## PURPOSE
Presentation of the implementation model regarding the NIST Risk Management Framework (RMF) supported by the application domain.

# DESIGN VIEW
The process of risk management is delivered as a template for users managing the cybersecurity risks reusing the NIST RMF concerns, state machines, collaboration events configuring the standard data and classes definition.

## STRUCTURE MODELS
### Risk Management Template
Risk treatment workflow is templated according to the NIST RMF process.

The implementation model follow the Responsibility-Chain and Command design patterns as structure of each workflow step executed and generating events. See standard workflow design model for more detail.

The NIST risk management process includes several steps (e.g prepare, assess, categorize, implement, authorize, monitor) and sub-tasks organized according to event conditions thrown by the domain.

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '18px',
        'primaryColor': '#fff',
        'primaryBorderColor': '#0e2a43',
        'secondaryBorderColor': '#0e2a43',
        'tertiaryBorderColor': '#0e2a43',
        'edgeLabelBackground':'#0e2a43',
        'lineColor': '#0e2a43',
        'tertiaryColor': '#fff'
    }
  }
}%%

graph RL
  prepare("PREPARE") --> id(("PrepareRisk<br>Assessment<br><br>"))
  assess("ASSESS") -- "[risk assessment prepared]" --> prepare
  assess -- "[security controls implemented]" --> implement("IMPLEMENT")
  categorize("CATEGORIZE") -- "[risk assessment prepared]" --> prepare
  select("SELECT") -- "[risk assessment prepared]" --> prepare
  select -- "[information system categorized]" --> categorize
  implement -- "[risk assessment prepared]" --> prepare
  implement -- "[security controls selected]" --> select
  authorize("AUTHORIZE") -- "[risk assessment prepared]" --> prepare
  authorize -- "[security controls instantiation assessed]" --> assess
  monitor("MONITOR") -- "[risk assessment prepared]" --> prepare
  monitor -- "[system authorized]" --> authorize
```

#### Risk assessment
Feature Type: make risk-based cybersecurity risk assessment of a scope

Requirements:
- [defined specification](https://www.notion.so/cybnity/Make-risk-based-cybersecurity-assessment-of-a-scope-43ab0ffd88624aa4af17862a57149b2b?pvs=4)
- and [REQ_FCT_73 by NIST](https://www.notion.so/cybnity/FCT_73_NIST-4c39f0929d724b4fb25954fdf9a1d052?pvs=4)










## Authorization policy strategy
Several policy strategy types are supported by the Access Control Process Module according to the kind of resource and relation between clients and object where usage privileges are controlled.

Some policy types usage make sens into specific context (e.g Identity and Access Management; Client Identity and Access Management).

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '18px',
        'primaryColor': '#fff',
        'primaryBorderColor': '#0e2a43',
        'secondaryBorderColor': '#0e2a43',
        'tertiaryBorderColor': '#0e2a43',
        'edgeLabelBackground':'#0e2a43',
        'lineColor': '#0e2a43',
        'tertiaryColor': '#fff'
    }
  }
}%%

classDiagram
  AuthorizationPolicy <|-- RoleBasedAccessControl
  AuthorizationPolicy <|-- TimeBasedAccessControl
  AuthorizationPolicy <|-- RegexBasedAccessControl
  AuthorizationPolicy <|-- ClientScopeBasedAccessControl
  AuthorizationPolicy <|-- UserBasedAccessControl
  AuthorizationPolicy <|-- ClientBasedAccessControl
  AuthorizationPolicy <|-- GroupBasedAccessControl
  GroupBasedAccessControl --> "0..*" GroupBasedAccessControl :children
  note for RoleBasedAccessControl "Ideal for least privilege and need to know approach.<br>For a system (e.g basis network module)<br><br>"
  note for TimeBasedAccessControl "During a defined time period"

  class AuthorizationPolicy {
    <<IAM scope>>
  }
  class RoleBasedAccessControl {
  }
  class TimeBasedAccessControl {
  }
  class ClientBasedAccessControl {
    <<CIAM scope>>
  }
  class UserBasedAccessControl {
  }
  class AttributesBasedAccessControl {
    <<AuthorizationPolicy>>
  }
  class GroupBasedAccessControl {
  }
  class ClientScopeBasedAccessControl {
	-scope : ScopeAttribute
  }
  class RegexBasedAccessControl {
    -identityPattern : IdentityAttribute[]
  }

```
```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '18px',
        'primaryColor': '#fff',
        'primaryBorderColor': '#0e2a43',
        'secondaryBorderColor': '#0e2a43',
        'tertiaryBorderColor': '#0e2a43',
        'edgeLabelBackground':'#0e2a43',
        'lineColor': '#0e2a43',
        'tertiaryColor': '#fff'
    }
  }
}%%

classDiagram
  note for AttributesBasedAccessControl "Policy of policies that can depend of context"
  IResource <|.. ControlledResource
  Unmodifiable <|.. ControlledResource
  AttributesBasedAccessControl o-- "1..*" SubjectAttribute :subjectDescriptions
  AttributesBasedAccessControl o-- "1..*" ActionAttribute :actionDescriptions
  AttributesBasedAccessControl o-- "1..*" EnvironmentAttribute :environmentDescriptions
  note for SubjectAttribute "Attributes describing the subject who is demanding access<br>(e.g roles, group memberships, competencies, user ID, etc..)<br><br>"
  note for ActionAttribute "Combination of attributes describing what user<br>want to perform (e.g read, write, action type)<br><br>"
  note for ControlledResource "Information asset or object impacted by the action"
  note for EnvironmentAttribute "Common attribute related to the current time and location from<br>where access is requested, type of communication channel, or client type<br><br>"

  class ControlledResource {
    <<abstract>>
    #resource : IResource
    -policies : Collection~AuthorizationPolicy~
    ControlledResource(IResource resource, Collection~AuthorizationPolicy~ controls)
    +controledBy() Collection~AuthorizationPolicy~
  }
  class AttributesBasedAccessControl {
    <<AuthorizationPolicy>>
    -subjectDescription : Collection~SubjectAttribute~
    -actionableActions : Collection~ActionAttribute~
    -environmentDescription : Collection~EnvironmentAttribute~
    +AttributesBasedAccessControl(Collection~SubjectAttribute~ subjectDescription, Collection~ActionAttribute~ actionableActions, Collection~EnvironmentAttribute~ environmentDescription)
  }
  class ActionAttribute {
    <<interface>>
  }
  class EnvironmentAttribute {
	<<interface>>
  }
  class SubjectAttribute {
	<<abstract>>
  }
  class ActionAttribute {
	<<interface>>
  }

```

#
[Back To View](README.md)
