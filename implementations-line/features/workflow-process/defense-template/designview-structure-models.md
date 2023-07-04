## PURPOSE
Presentation of the structural components regarding implementation architecture.

# DESIGN VIEW
Several structural patterns are supporting the templating capabilities.

### Key Components
For more detail, the technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|DomainObjectType|Represent a type of domain object (e.g template type) which can define a category of domain object. It's more or less like a generic type assignable to any domain object as specification information|
|IReferential|Represent a reference to a standard (e.g ISO/IEC) or external framework (e.g NIST CSF) or official regulation (e.g GDPR) that can be referenced as frame of topics specification|
|ITemplate|Represent a contract of templating regarding an information|
|WorkflowCommandHandlerFactory|Factory of handler. Can be based on a template file (e.g JSON, XML) of standard (e.g NIST, ISO27001).<br>For example, factory is usable to define cyber-security framework including RMF process steps (and optional sub-tasks definitions) as ConcreteHandler definitions|

## STRUCTURE MODELS
Presentation of the design view of the `org.cybnity.feature.defense_template` main project's artifacts package.

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
  class ITemplate {
    <<interface>>
    +name() String
    +type() DomainObjectType
  }

  class IReferential {
    <<interface>>
    +acronym() String
    +label() String
  }

  class DomainObjectType {
      <<ValueObject>>
      +description() String
      +name() String
  }

  class WorkflowCommandHandlerFactory {
      <<abstract>>
      +create(ITemplate template)* IWorkflowCommandHandler
      +create(IContext context)* IWorkflowCommandHandler
  }

```

### Sub-Packages
See complementary presentation of [detailed structure models implemented into the sub-packages](designview-packages.md).

#
[Back To Home](README.md)
