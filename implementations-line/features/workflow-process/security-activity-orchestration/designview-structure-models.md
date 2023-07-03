## PURPOSE
Presentation of the structural components regarding implementation architecture.

# DESIGN VIEW
Several structural patterns are supporting the orchestration capabilities.

### Key Components
For more detail, the technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|ChainCommandHandler|Contract of command handling implementing the chain of responsibility chain pattern|
|IWorkflowCommandHandler|Chain of responsibility pattern implementation regarding the handling of workflow command events|
|WorkflowCommandHandlerFactory|Factory of handler. Can be based on a template file (e.g JSON, XML) of standard (e.g NIST, ISO27001).<br>For example, factory is usable to define cyber-security framework including RMF process steps (and optional sub-tasks definitions) as ConcreteHandler definitions|

## STRUCTURE MODELS
Presentation of the design view of the `org.cybnity.feature.security_activity_orchestration` main project's artifacts package.

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
  WorkflowCommandHandlerFactory ..> IWorkflowCommandHandler

  class ChainCommandHandler {
      <<abstract>>
      -label : String
      -next : Collection~ChainCommandHandler~
      -subTasks : List~ChainCommandHandler~
      +ChainCommandHandler(Collection~ChainCommandHandler~ next, List~ChainCommandHandler~ subTasks)
      #next() Collection~ChainCommandHandler~
      #canHandle(Command request)* boolean
      #subTasks() List~ChainCommandHandler~
      +addParallelNextHandler(ChainCommandHandler next)
      final +handle(Command request)
      +label() String
  }
  class WorkflowCommandHandlerFactory {
      <<abstract>>
      +create(ITemplate template)* IWorkflowCommandHandler
      +create(IContext context)* IWorkflowCommandHandler
  }
  class IWorkflowCommandHandler {
      <<interface>>
      +addParallelNextHandler(ChainCommandHandler next)
      +handle(Command request)
  }

```

### Sub-Packages
See complementary presentation of [detailed structure models implemented into the sub-packages](designview-packages.md).

#
[Back To Home](README.md)
