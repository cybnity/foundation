## PURPOSE
Presentation of the domain components regarding architecture components respecting the domain-driven design patterns.

# FUNCTIONAL VIEW
Presentation of the main functionalities area which allow realization of DDD requirements.

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '10px',
        'primaryColor': '#fff',
        'primaryTextColor': '#0e2a43',
        'primaryBorderColor': '#0e2a43',
        'secondaryColor': '#fff',
        'secondaryTextColor': '#fff',
        'secondaryBorderColor': '#fff',
        'tertiaryColor': '#fff',
        'tertiaryTextColor': '#fff',
        'tertiaryBorderColor': '#fff',
        'edgeLabelBackground':'#fff',
        'lineColor': '#0e2a43',
        'titleColor': '#fff',
        'textColor': '#fff',
        'lineColor': '#0e2a43',
        'nodeTextColor': '#fff',
        'nodeBorder': '#0e2a43',
        'noteTextColor': '#fff',
        'noteBorderColor': '#fff'
    }
  }
}%%
flowchart BT
  subgraph global
    direction BT
    subgraph 4
      id9((PUBLISHED LANGUAGE))
    end
    subgraph 3
      direction BT
      id14 -- names enter --> id18
      id17((CORE DOMAIN)) -- avoid overinvesting in --> id16((GENERIC SUBDOMAINS))
      id5((CONTEXT MAP)) -- segregate the conceptual messes --> id6((BIG BALL OF MUD))
      id14 -- assess / overview relationships with --> id5
      id5 -- translate and insulate unilaterally with --> id7((ANTI-CORRUPTION LAYER))
      id5 -- free teams to go --> id8((SEPARATE WAYS))
      id5 -- minimize translation --> id11((CONFORMIST))
      id5 -- relate allied contexts as --> id12((CUSTOMER / SUPPLIER))
      id5 -- overlap allied contexts through --> id13((SHARED KERNEL))
      id5 -- interdependent contexts from --> id13
      id14((BOUNDED CONTEXT))
      id5 -- support multiple clients through --> id10((OPEN HOST SERVICE))
      id10 -- formalize --> id9
      id5 -- loosely couple contexts through --> id9
      id14 -- keep model unified by --> id15
    end
    subgraph 2
      direction BT
      id3 -- model gives structure to --> id18((UBIQUITOUS LANGUAGE))
      id3 -- isolate domain expressions with --> id19((LAYERED ARCHITECTURE))
      id3 -- express change with --> id2
      id4 -- push state change with --> id2((DOMAIN EVENTS))
      id17 -- cultivate rich model with --> id18
      id3 -- define model within --> id14
      id3((MODEL-DRIVEN DESIGN))
      id4 -- act as root of --> id22((AGGREGATES))
      id4 -- encapsulate with --> id22
      id4 -- access with --> id23((REPOSITORIES))
      id22 -- access with --> id23
      id3 -- express model with --> id1((SERVICES))
      id3 -- express identity with --> id4((ENTITIES))
      id15((CONTINUOUS INTEGRATION))
      id3 -- express state & computation with --> id20((VALUE OBJECTS))
      id20 -- encapsulate with --> id22
      id22 -- encapsulate with --> id21
      id4 -- encapsulate with --> id21
    end
    subgraph 1
      direction BT
      id20 -- encpasulate with --> id21((FACTORIES))
    end
  end
  classDef future stroke-dasharray: 5 5
  class id4,id5,id6,id7,id8,id9,id10,id11,id12,id13,id14,id16,id17,id18,id21 future;

```

# DESIGN VIEW
Several structural patterns are supporting the domain (e.g event sourcing) and are reusable (e.g by inheritance) for coding of application domains elements.

|Class Type|Motivation|
| :-- | :-- |
|Command|Imperative and identifiabl element that is a request for the system to perform a task|
|CommandHandlingService|Represent a component which manage handlers regarding specific Aggregate type|
|CommonChildFactImpl|Reusable generic implementation class as child of immutable historical fact|
|DomainEventPublisher|Publishing service from a domain model as repository service for Aggregates notifying their state changes|
|DomainEventSubscriber|Interest contract to be notified when types of facts are changed|
|DomainEvent|Determine something that has happened in the system (e.g typically as a result of a command, or a change observed regarding a bounded context)|
|EventStream|Append-only nature stream of domain events in order of occurence regarding a domain object|
|EventStore|Persistence system of event and aggregate types regarding a single bounded context|
|IAggregate|Identifiable fact that defines a consistency boundary including multiple related objects (e.g domain and/or value objects)|
|IApplicationService|Applicative behaviors contract regarding an application layer|
|IBoundedContext|Represent a defined perimeter providing resources|
|IContext|Generic contact allowing to share and provide information in an area of usage|
|ICommandHandler|Responsible of actions realization requested via Command event|
|IEventStore|Contract regarding storing (with append-only approach) and hydratation of a type of event (e.g versions stream)|
|INotificationService|Publishing of events from event store via messaging infrastructure|
|IReadModel|Denormalized dto repository (also named Query Model) supporting CQRS pattern|
|IService|Domain service usable to perform a significant business process created in a domain model when the operation feels out of place as a method on an Aggregate or a Value Object|
|ISubscribable|Contract of notifications reception about fact events|
|IValidationNotificationHandler|Handling of problems detected on a subject (e.g Entity attribute) implementing deleted validation approach|
|IVersionable|Supports multiple versions of a same event type|
|IWriteModel|Also named Command Model, segregation element (e.g event store) of CQRS pattern managing change commands and normalized data|
|NotificationLog|Log event regarding an identifiable domain fact|
|ProcessManager|Behavior design pattern, is a mediation component that distribute messages when complex routing between Aggregates|
|Repository|Preservation of domain objects. Each persistent Aggregate type have a repository|
|UnidentifiableFactNotificationLog|Log event regarding a fact that was not previously identified but requiring attention (e.g system failure, unknown fact observed|
|Validator|Implementation class of Specification pattern or Strategy pattern that detect invalid state of subject and informs observers|
|ValueObject|Describes a thing in a domain that can be maintained as immutable and integral unit|

## STRUCTURE MODELS

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
    IdentifiableFact <|.. Command
    IVersionable <|.. Command
    IReferenceable <|.. Command
    ICommandHandler <|.. ProcessManager
    ProcessManager <|-- CommandHandlingService
    IContext <|-- IBoundedContext
    ChildFact <|-- NotificationLog
    Entity <|-- UnidentifiableFactNotificationLog
    class IVersionable {
        <<interface>>
        +versionUID() Long
    }
    class IReferenceable {
        <<interface>>
    }
    class IdentifiableFact {
        <<interface>>
    }
    class Command {
        <<abstract>>
        -identifiedBy : Entity
        -occuredOn : Temporal
    }
    class CommandHandlingService {
        <<abstract>>
        -recipientOfCommands : IAggregate
        -notifiablePublishers : Set~DomainEventPublisher~
    }
    class ProcessManager {
        <<abstract>>
        -mediated : HashMap~String, ICommandHandler~
        #context : IContext
        #managedHandlers() HashMap~String, ICommandHandler~
    }
    class ICommandHandler {
        <<interface>>
        +handle(Command command, IContext ctx)
        +handleCommandTypeVersions() Set~Long~
    }
    class IBoundedContext {
        <<interface>>
    }
    class IContext {
        <<interface>>
        +addResource(Object instance, String resourceName, boolean forceReplace) boolean
        +remove(String resourceName) boolean
        +get(String resourceName) Object
        +get(Class~?~) Object
    }
    class ISubscribable {
        <<interface>>
        +subscribe(DomainEventSubscriber~T~ subcriber)
        +remove(DomainEventSubscriber~T~ subscriber)
    }
    class UnidentifiableFactNotificationLog {
        -originFacts : List~IHistoricalFact~
    }
    class IValidationNotificationHandler {
        <<interface>>
        +handleError(String message)
    }
    class Validator {
        <<abstract>>
        -notificationHandler : IValidationNotificationHandler
        +validate()*
    }
    class IWriteModel {
        <<interface>>
        +handle(Command command)
    }
    class IReadModel {
        <<interface>>
    }
    class ValueObject {
        <<abstract>>
        #valueEquality(ValueObject~T~ obj)*
    }

```

### Sub-Packages
See the presentation of [detailed structure models implemented into the sub-packages](designview-packages.md).

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)
