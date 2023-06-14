## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.framework.domain` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|ActivityState|State of activity (e.g active or not active) regarding a subject that can be used as an activity tag for any type of subject|
|Aggregate|Scope of informations set which can be mutable (e.g domain entity aggregating value objects and/or entities reference), or immutable domain object (e.g entity reference)|
|ApplicationService|Represent a component of a service layer hosted by a domain boundary|
|CommandHandlingService|Represent a component which manage handlers regarding specific Aggregate type|
|CommonChildFactImpl|Reusable generic implementation class as child of immutable historical fact|
|DomainEntityImpl|Basic and common domain entity implementation object. A domain entity IS NOT MODIFIABLE and is equals to an identifiable fact.<br>A domain entity DOES NOT CONTAIN MUTABLE properties. A domain entity can represent an aggregate root (equals to an identification mean) which is an identifiable domain object (e.g persistent business object as immutable version of a complex domain object) attached to an aggregate domain object|
|DomainEventPublisher|Publishing service from a domain model as repository service for Aggregates notifying their state changes|
|DomainEventSubscriber|Interest contract to be notified when types of facts are changed|
|EventRecord|Represent a recorded fact relative to an event which is manageable by a store, including the original version of event tracked and extracted informations allowing to store/retrieve it|
|EventStore|Persistence system of event and aggregate types regarding a single bounded context|
|EventStream|Append-only nature stream of domain events in order of occurence regarding a domain object|
|IAggregate|Identifiable fact that defines a consistency boundary including multiple related objects (e.g domain and/or value objects)|
|IApplicationService|Applicative behaviors contract regarding an application layer|
|IAggregate|In a Domain-Driven Design (DDD), an aggregate defines a consistency boundary. An aggregate may consist of multiple related objects, all of which could be persisted together (e.g atomic operation)|
|IContext|Generic contact allowing to share and provide information in an area of usage|
|IDomainModel|Referential model for a domain, a specification (e.g defined via sub-interface of this one) provide several types of definitions regarding domain's entities, value objects, services and ubiquitous language elements usable in the domain|
|IDomainRepository|Represents a persistence-oriented repository (also sometimes called Aggregate store, or Aggregate-Oriented database) basic contract for a bounded context.<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts|
|IDomainStore|Stream store (with an append-only approach) which maintain history of a type of domain fact (e.g Aggregate versions).<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts|
|IEventStore|Contract regarding storing (with append-only approach) and hydratation of a type of event (e.g versions stream)|
|INotificationService|Publishing of events from event store via messaging infrastructure|
|IVersionable|Supports multiple versions of a same event type|
|NotificationLog|Log event regarding an identifiable domain fact|
|Predecessors|Utility class providing capabilities supporting the management of predecessors and dependent contents|
|Repository|Preservation of domain objects. Each persistent Aggregate type have a repository|
|SessionContext|Implementation class of a ISessionContext|
|SocialEntity|Represent a social entity instance (e.g a company, a person) which define an identifiable structure (e.g organizational, physical)|
|Tenant|Represent an organization subscription that allow to define a scope of multi-tenant application regarding a named organization which facilitates the users registrations through invitation|

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

## MODEL PACKAGE

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
    Context <|-- SessionContext
    ISessionContext <|.. SessionContext

    class ISessionContext {
        <<interface>>
    }
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
    ChildFact <|-- CommonChildFactImpl
    ChildFact <|-- SocialEntity
    ChildFact <|-- NotificationLog
    ChildFact <|-- Tenant
    CommonChildFactImpl <|-- Aggregate
    IAggregate <|.. Aggregate
    Serializable <|.. Aggregate
    IVersionable <|.. Aggregate
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
    class NotificationLog {
        +NotificationLog(Entity loggedEvent, Identifier logEventId)
        +versionHash() String
        +identified() Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId) Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds) Identifier
    }
    class SocialEntity {
        +SocialEntity(Entity predecessor, Identifier id)
        +SocialEntity(Entity predecessor, LinkedHashSet~Identifier~ identifiers)
        +versionHash() String
        +identified() Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId) Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds) Identifier
    }
    class CommonChildFactImpl {
        +CommonChildFactImpl(Entity predecessor, Identifier id)
        +CommonChildFactImpl(Entity predecessor, LinkedHashSet~Identifier~ identifiers)
        +versionHash() String
        +identified() Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId) Identifier
        +generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds) Identifier
    }
    class Aggregate {
    }
    class IAggregate {
        <<interface>>
    }
    class Serializable {
        <<interface>>
    }
    class IVersionable {
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
        +remove(DomainEventSubscriber~T~ aSubscriber) ~T~
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
