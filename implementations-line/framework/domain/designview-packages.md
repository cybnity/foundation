## PURPOSE
Presentation of the deisng view packages and provided components.

# STRUCTURE MODELS

Several packages are implemented to organize the components (e.g specification elements, implementation components) into the `org.cybnity.framework` main package.

## MODEL

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
    IHistoricalFact <|.. DomainEvent
    IVersionable <|.. DomainEvent
    IReferenceable <|.. DomainEvent
    IdentifiableFact <|.. DomainEvent
    IdentifiableFact <|-- IAggregate
    ISubscribable <|.. DomainEventPublisher
    ISubscribable <|.. Repository
    ISubscribable <|.. EventStore
    IEventStore <|.. EventStore
    Unmodifiable <|.. EventStream
    
    class DomainEvent {
        <<abstract>>
        -occuredOn : OffsetDateTime
        -identifiedBy : Entity
        +DomainEvent()
        +DomainEvent(Entity uid)
    }
    class IHistoricalFact {
        <<interface>>
    }
    class IdentifiableFact {
        <<interface>>
    }
    class IAggregate {
        <<interface>>
        +execute(Command change, IContext ctx)
    }
    class IReferenceable {
        <<interface>>
    }
    class DomainEventPublisher {
        +publish(T domainEvent) ~T~
        +reset() DomainEventPublisher
    }
    class DomainEventSubscriber {
        <<abstract>>
        +handleEvent(T event)
        +subscribeToEventType() Class
    }
    class EventStore {
        <<abstract>>
    }
    class IEventStore {
        <<interface>>
        +append(DomainEvent event)
        +findEventFrom(Identifier uid) DomainEvent
        +loadEventStream(Identifier uid) EventStream
        +loadEventStream(Identifier uid, int skipEvents, int maxCount) EventStream
    }
    class EventStream {
        -version : int
        -events : List~DomainEvent~
    }
    class Unmodifiable {
        <<interface>>
    }
    class Repository {
        <<abstract>>
    }

```

## APPLICATION

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
    IWriteModel <|-- IApplicationService
    IApplicationService <|.. ApplicationService
    
    class IApplicationService {
        <<interface>>
    }
    class ApplicationService {
        <<abstract>>
    }
    class INotificationService {
        <<interface>>
        +findNotificationLog(Identifier logId, EventStore store) NotificationLog
        +currentNotificatiopnLog() NotificationLog
        +publishNotification()
    }

```

## INFRASTRUCTURE

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
    IFactStore~T~ <|-- IDomainStore~T~
    IFactRepository~T~ <|-- IDomainRepository~T~

    class IFactStore~T~ {
        <<interface>>
    }
    class IFactRepository~T~ {
        <<interface>>
    }
    class IDomainRepository~T~ {
        <<interface>>
        +nextIdentity(ISessionContext ctx) T
        +factOfId(Identifier aFactId, ISessionContext ctx) T
        +remove(T fact, ISessionContext ctx) boolean
        +removeAll(Collection~T~ aFactCollection, ISessionContext ctx)
        +save(T aFact, ISessionContext ctx) T
        +saveAll(Collection~T~ aFactCollection, ISessionContext ctx)
    }
    class IDomainStore~T~ {
        <<interface>>
        +append(T fact, ISessionContext ctx)
        +findEventFrom(Identifier uid, ISessionContext ctx)
    }

```

#
[Back To Home](README.md)
