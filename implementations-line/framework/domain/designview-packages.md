## PURPOSE
Presentation of the deisng view packages and provided components into the `org.cybnity.framework.domain` main project's artifacts package.

# STRUCTURE MODELS
Several sub-packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

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
    IdentifiableFact <|.. Command
    IVersionable <|.. Command
    Serializable <|.. Command
    IReferenceable <|.. Command
    Evaluations <.. Command : use
    IContext <|-- ISessionContext

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
    class IVersionable {
        <<interface>>
    }
    class IContext {
        <<interface>>
    }
    class Serializable {
        <<interface>>
    }
    class IReferenceable {
        <<interface>>
    }
    class ISessionContext {
        <<interface>>
        +tenant() Tenant
    }
    class Command {
        <<abstract>>
        #identifiedBy : Entity
        #occuredOn : OffsetDateTime
        +identified() Identifier
        +valueHashCodeContributors() String[]
        +hashCode() int
        +equals(Object event) boolean
        +occuredOn() OffsetDateTime
        +reference() EntityReference
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
    class DataTransferObject {
        <<abstract>>
        +equals(Object obj) boolean
    }

```

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
    Entity <|-- DomainEntityImpl
    FactRecord <|-- EventRecord
    IdentifiableFact <|-- IAggregate
    ProcessManager <|-- CommandHandlingService

    class CommandHandlingService {
        <<abstract>>
        -recipientOfCommands : IAggregate
        -notifiablePublishers : Set~DomainEventPublisher~
        #recipientOfCommands() IAggregate
        #getNotifiablePublishers() Set~DomainEventPublisher~
        #setNotifiablePublishers(Set~DomainEventPublisher~ notifiablePublishers)
    }
    class ProcessManager {
        <<abstract>>
    }
    class IdentifiableFact {
        <<interface>>
    }
    class IAggregate {
        <<interface>>
        +execute(Command change, IContext ctx)
    }
    class Entity {
        <<abstract>>
    }
    class DomainEntityImpl {
        +identified() Identifier
        +immutable() Serializable
        +versionHash() String
    }
    class EventRecord {
        +immutable() Serializable
        +versionHash() String
    }
    class IDomainModel {
        <<interface>>
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
    Context <|-- SessionContext
    ISessionContext <|.. SessionContext
    ChildFact <|-- CommonChildFactImpl
    ChildFact <|-- SocialEntity
    ChildFact <|-- Tenant
    MutableProperty <|-- ActivityState
    Tenant ..> Predecessors : use
    Tenant *-- "0..1" MutableProperty : organization
    Tenant *-- "0..1" ActivityState : activityStatus

    class ChildFact {
        <<abstract>>
    }
    class Predecessors {
        +generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)$ Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds)$ Identifier
    }
    class Tenant {
        +status() ActivityState
        +activate() MutableProperty
        +deactivate() MutableProperty
        +setOrganization(MutableProperty tenantRepresentedBy)
        +organization() MutableProperty
        +immutable() Serializable
        +versionHash() String
        +identified() Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId) Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds) Identifier
    }
    class ISessionContext {
        <<interface>>
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
    ISubscribable <|.. DomainEventPublisher
    ISubscribable <|.. Repository
    ISubscribable <|.. EventStore
    IEventStore <|.. EventStore
    Unmodifiable <|.. EventStream
    Serializable <|.. EventStream

    class Serializable {
        <<interface>>
    }
    class ISubscribable {
        <<interface>>
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
    class Repository {
        <<abstract>>
    }
    class DomainEventSubscriber~T~ {
        <<abstract>>
        +handleEvent(T event)*
        +subscribeToEventType()* Class~?~
    }
    class DomainEventPublisher {
        +instance()$ DomainEventPublisher
        +subscribe(DomainEventSubscriber~T~ aSubscriber) ~T~
        +remove(DomaineEventSubscriber~T~ aSubscriber) ~T~
        +publish(T aDomainEvent) ~T~
        +reset() DomainEventPublisher
    }
    class EventStream {
        -version : int
        -events : List~DomainEvent~
        +immutable() Serializable
    }
    class Unmodifiable {
        <<interface>>
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
    
    class IWriteModel {
        <<interface>>
    }
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
