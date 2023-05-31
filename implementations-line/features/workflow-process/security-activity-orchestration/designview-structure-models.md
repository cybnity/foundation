## PURPOSE
Presentation of the structural components regarding implementation architecture.

# DESIGN VIEW
Several structural patterns are supporting the orchestration capabilities.

### Key Components
For more detail, the technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|CommandHandler| |
|CommandHandlerFactory| |
|ConcreteCommandHandler| |
|ICommandHandler| |
|ITemplate| |

## STRUCTURE MODELS
Presentation of the design view of the `org.cybnity.feature.security_activity_orchestration` main project's artifacts package.

### Sub-Packages
See complementary presentation of [detailed structure models implemented into the sub-packages](designview-packages.md).

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
  ICommandHandler <|.. CommandHandler
  CommandHandler <|-- ConcreteCommandHandler
  note for ICommandHandler "Chain of responsibility pattern implementation"
  note for ITemplate "Based on a template file (e.g JSON, XML) of NIST or ISO27001 standard.<br>Cybersecurity framework including RMF process steps (and optional<br>included sub-tasks definitions) as ConcreteCommandHandler definitions<br><br>"
  ITemplate <.. CommandHandlerFactory : load
  ConcreteCommandHandler <.. CommandHandlerFactory : instantiate
  CommandHandler *-- "0..*" CommandHandler : subtasks
  IDomainEvent <.. ConcreteCommandHandler : publish

  class ITemplate {
    <<interface>>
  }
  class ICommandHandler {
    <<interface>>
	+setNext(Collection~ICommandHandler~ next)
	+handle(Command request)
  }
  class CommandHandler {
    <<abstract>>
    -label : String
    -next : Collection~CommandHandler~
    -subTasks : List~CommandHandler~
    +CommandHandler(Collection~CommandHandler~ next)
    #next() Collection~CommandHandler~
    +handle(Command request)
    #canHandle(Command request)* boolean
    +label() String
    #subTasks() List~CommandHandler~
  }
  class ConcreteCommandHandler {
    +ConcreteCommandHandler(Collection~CommandHandler~ next)
    #canHandle(Command request) boolean
  }
  class CommandHandlerFactory {
    <<abstract>>
    +Create(ITemplate template)* ICommandHandler
    +Create(IContext context)* ICommandHandler
  }
  class IDomainEvent {
    <<interface>>
  }
```

#
[Back To Home](README.md)
