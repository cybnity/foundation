## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.framework.domain` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

| Class Type                   | Motivation                                                                                                                                                                                                                                                                                                                                                                                                                               |
|:-----------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ActivityState                | State of activity (e.g active or not active) regarding a subject that can be used as an activity tag for any type of subject                                                                                                                                                                                                                                                                                                             |
| Aggregate                    | Represents a scope of information providing attributes and/or capabilities as a complex domain object. An aggregate root of the process entity domain is defined via immutable attributes (e.g ValueObject, EntityReference of other domains' objects, ChildFact historical and identified fact) and/or mutable attributes (e.g MutableProperty objects)                                                                                 |
| ApplicationService           | Represent a component of a service layer hosted by a domain boundary                                                                                                                                                                                                                                                                                                                                                                     |
| CommandHandlingService       | Represent a component which manage handlers regarding specific Aggregate type                                                                                                                                                                                                                                                                                                                                                            |
| CommonChildFactImpl          | Reusable generic implementation class as child of immutable historical fact                                                                                                                                                                                                                                                                                                                                                              |
| CompletionState              | Represent a state of completion defining by a name and optionally by a percentage value about reached completion rate                                                                                                                                                                                                                                                                                                                    |
| ConcreteSnapshot             | Represent a snapshot of object (e.g aggregate version) which is manageable by a store (e.g as history of snapshots serialized and re-hydrated)                                                                                                                                                                                                                                                                                           |
| DomainEntity                 | Basic and common domain entity implementation object. A domain entity IS NOT MODIFIABLE and is equals to an identifiable fact. A domain entity DOES NOT CONTAIN MUTABLE properties.<br>A domain entity can represent an aggregate root (equals to an identification mean) which is an identifiable domain object (e.g persistent business object as immutable version of a complex domain object) attached to an aggregate domain object |
| DomainEventInMemoryStoreImpl | Implementation of EventStore based on in-memory storage                                                                                                                                                                                                                                                                                                                                                                                  |
| DomainEventPublisher         | Publishing service from a domain model as repository service for Aggregates notifying their state changes                                                                                                                                                                                                                                                                                                                                |
| EventRecord                  | Represent a recorded fact relative to an event which is manageable by a store, including the original version of event tracked and extracted informations allowing to store/retrieve it                                                                                                                                                                                                                                                  |
| EventStore                   | Persistence system of event and aggregate types regarding a single bounded context                                                                                                                                                                                                                                                                                                                                                       |
| EventStream                  | Append-only nature stream of domain events in order of occurrence regarding a domain object                                                                                                                                                                                                                                                                                                                                              |
| IAggregate                   | Identifiable fact that defines a consistency boundary including multiple related objects (e.g domain and/or value objects)                                                                                                                                                                                                                                                                                                               |
| IApplicationService          | Applicative behaviors contract regarding an application layer                                                                                                                                                                                                                                                                                                                                                                            |
| IAggregate                   | In a Domain-Driven Design (DDD), an aggregate defines a consistency boundary. An aggregate may consist of multiple related objects, all of which could be persisted together (e.g atomic operation)                                                                                                                                                                                                                                      |
| IContext                     | Generic contact allowing to share and provide information in an area of usage                                                                                                                                                                                                                                                                                                                                                            |
| IDomainModel                 | Referential model for a domain, a specification (e.g defined via sub-interface of this one) provide several types of definitions regarding domain's entities, value objects, services and ubiquitous language elements usable in the domain                                                                                                                                                                                              |
| IDomainRepository            | Represents a persistence-oriented repository (also sometimes called Aggregate store, or Aggregate-Oriented database) basic contract for a bounded context.<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts                                                                                                                      |
| IDomainStore                 | Stream store (with an append-only approach) which maintain history of a type of domain fact (e.g Aggregate versions).<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts                                                                                                                                                           |
| IDomainEventSubscriber       | Interest contract to be notified when types of facts are changed                                                                                                                                                                                                                                                                                                                                                                         |
| IEventStore                  | Contract regarding storing (with append-only approach) and hydration of a type of event (e.g versions stream)                                                                                                                                                                                                                                                                                                                            |
| ISnapshot                    | Represent a specific state of value regarding a subject (e.g full state aggregate instance)                                                                                                                                                                                                                                                                                                                                              |
| INotificationService         | Publishing of events from event store via messaging infrastructure                                                                                                                                                                                                                                                                                                                                                                       |
| NotificationLog              | Log event regarding an identifiable domain fact                                                                                                                                                                                                                                                                                                                                                                                          |
| Predecessors                 | Utility class providing capabilities supporting the management of predecessors and dependent contents                                                                                                                                                                                                                                                                                                                                    |
| Repository                   | Preservation of domain objects. Each persistent Aggregate type have a repository                                                                                                                                                                                                                                                                                                                                                         |
| SessionContext               | Implementation class of a ISessionContext                                                                                                                                                                                                                                                                                                                                                                                                |
| SocialEntity                 | Represent a social entity instance (e.g a company, a person) which define an identifiable structure (e.g organizational, physical)                                                                                                                                                                                                                                                                                                       |
| Tenant                       | Represent a logical context (e.g organization dedicated) subscription that allow to define a scope into a multi-tenant application with a name which facilitates the users registrations through invitation                                                                                                                                                                                                                              |

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

## MODEL.EVENT PACKAGE
Several types of domain events are defined by each applicative domain. This package includes enabler and utilities classes which are reusable by the domain implements.

| Class Type                      | Motivation                                                                                                                                 |
|:--------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------|
| AttributeName                   |                                                                                                                                            |
| CollaborationEventType          |                                                                                                                                            |
| CommandFactory                  | Factory of command event supported by a Anti-Corruption Layer                                                                              |
| CommandName                     |                                                                                                                                            |
| ConcreteCommandEvent            | Generic event regarding a CRQS command type to execute a resource change via a topic and that can be treated by a domain                   |
| ConcreteDomainChangeEvent       | Generic event regarding a change occurred on a topic relative to a domain                                                                  |
| ConcreteQueryEvent              | Generic event regarding a CQRS query command type to execute a search or read of resources via a topic and that can be treated by a domain |
| CorrelationIdFactory            | Factory of correlation identifier reusable into command event                                                                              |
| DomainEventFactory              | Factory of domain event (e.g domain aggregate change; bounded context event) supported by a Anti-Corruption Layer                          |    
| DomainEventType                 |                                                                                                                                            |
| EventSpecification              | Utility class regarding event specification                                                                                                |
| HydrationAttributeProvider      |                                                                                                                                            |
| IAttribute                      |                                                                                                                                            |
| IEventType                      |                                                                                                                                            |
| ProcessingUnitPresenceAnnounced |                                                                                                                                            |
| RandomUUIDFactory               |                                                                                                                                            |

## MODEL PACKAGE

| Class Type                | Motivation                                                                                                                                                                                                                                                                                                                                                                                                                               |
|:--------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ActivityState             | State of activity (e.g active or not active) regarding a subject that can be used as an activity tag for any type of subject                                                                                                                                                                                                                                                                                                             |
| Aggregate                 | Represents a scope of information providing attributes and/or capabilities as a complex domain object. An aggregate root of the process entity domain is defined via immutable attributes (e.g ValueObject, EntityReference of other domains' objects, ChildFact historical and identified fact) and/or mutable attributes (e.g MutableProperty objects)                                                                                 |
| CommandHandlingService    | Represent a component which manage handlers regarding specific Aggregate type                                                                                                                                                                                                                                                                                                                                                            |
| CommonChildFactImpl       | Reusable generic implementation class as child of immutable historical fact                                                                                                                                                                                                                                                                                                                                                              |
| CompletionState           | Represent a state of completion defining by a name and optionally by a percentage value about reached completion rate                                                                                                                                                                                                                                                                                                                    |
| DomainEntity              | Basic and common domain entity implementation object. A domain entity IS NOT MODIFIABLE and is equals to an identifiable fact. A domain entity DOES NOT CONTAIN MUTABLE properties.<br>A domain entity can represent an aggregate root (equals to an identification mean) which is an identifiable domain object (e.g persistent business object as immutable version of a complex domain object) attached to an aggregate domain object |
| DomainEntityDeserializer  |                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| DomainEventPublisher      | Publishing service from a domain model as repository service for Aggregates notifying their state changes                                                                                                                                                                                                                                                                                                                                |
| EventRecord               | Represent a recorded fact relative to an event which is manageable by a store, including the original version of event tracked and extracted informations allowing to store/retrieve it                                                                                                                                                                                                                                                  |
| EventStore                | Persistence system of event and aggregate types regarding a single bounded context                                                                                                                                                                                                                                                                                                                                                       |
| EventStream               | Append-only nature stream of domain events in order of occurrence regarding a domain object                                                                                                                                                                                                                                                                                                                                              |
| HydrationCapability       |                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| IAggregate                | In a Domain-Driven Design (DDD), an aggregate defines a consistency boundary. An aggregate may consist of multiple related objects, all of which could be persisted together (e.g atomic operation)                                                                                                                                                                                                                                      |
| IDomainEventSubscriber    | Interest contract to be notified when types of facts are changed                                                                                                                                                                                                                                                                                                                                                                         |
| IDomainModel              | Referential model for a domain, a specification (e.g defined via sub-interface of this one) provide several types of definitions regarding domain's entities, value objects, services and ubiquitous language elements usable in the domain                                                                                                                                                                                              |
| IEventStore               | Contract regarding storing (with append-only approach) and hydration of a type of event (e.g versions stream)                                                                                                                                                                                                                                                                                                                            |
| ISnapshot                 | Represent a specific state of value regarding a subject (e.g full state aggregate instance)                                                                                                                                                                                                                                                                                                                                              |
| ITransactionStateObserver | Contract of notification supported by a component observing transactions status                                                                                                                                                                                                                                                                                                                                                          |
| MutedAggregateFactory     | Factory of an aggregate that provide instance prepared specifically according to a type of aggregate                                                                                                                                                                                                                                                                                                                                     |
| NotificationLog           | Log event regarding an identifiable domain fact                                                                                                                                                                                                                                                                                                                                                                                          |
| Predecessors              | Utility class providing capabilities supporting the management of predecessors and dependent contents                                                                                                                                                                                                                                                                                                                                    |
| Repository                | Preservation of domain objects. Each persistent Aggregate type have a repository                                                                                                                                                                                                                                                                                                                                                         |
| SessionContext            | Implementation class of a ISessionContext                                                                                                                                                                                                                                                                                                                                                                                                |
| SocialEntity              | Represent a social entity instance (e.g a company, a person) which define an identifiable structure (e.g organizational, physical)                                                                                                                                                                                                                                                                                                       |
| Tenant                    | Represent a logical context (e.g organization dedicated) subscription that allow to define a scope into a multi-tenant application with a name which facilitates the users registrations through invitation                                                                                                                                                                                                                              |
| TenantBuilder             |                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| TenantDescriptor          |                                                                                                                                                                                                                                                                                                                                                                                                                                          |

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
    Entity <|-- DomainEntity
    FactRecord <|-- EventRecord
    ICommandHandler <|-- IAggregate
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
    class ICommandHandler {
        <<interface>>
    }
    class IAggregate {
        <<interface>>
        +root() EntityReference
    }
    class Entity {
        <<abstract>>
    }
    class DomainEntity {
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
    Aggregate <|-- Tenant
    HydrationCapability <|.. CommonChildFactImpl
    CommonChildFactImpl <|-- Aggregate
    IAggregate <|.. Aggregate
    Serializable <|.. Aggregate
    MutableProperty <|-- ActivityState
    Tenant ..> Predecessors : use
    Tenant *-- "0..1" TenantDescriptor : label
    Tenant *-- "0..1" ActivityState : activityStatus

    class HydrationCapability {
        <<interface>>
        +mutateWhen(DomainEvent change)
        +replayEvents(EventStream history)
    }
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
        +setLabel(TenantDescriptor name)
        +label() TenantDescriptor
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
        +root() EntityReference
    }
    class IAggregate {
        <<interface>>
    }
    class Serializable {
        <<interface>>
    }
    class ActivityState {
        <<MutableProperty>>
        +isActive() Boolean
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
    class IDomainEventSubscriber~T~ {
        +handleEvent(T event)*
        +subscribeToEventType()* Class~?~
    }
    class DomainEventPublisher {
        +instance()$ DomainEventPublisher
        +subscribe(IDomainEventSubscriber~T~ aSubscriber) ~T~
        +remove(IDomainEventSubscriber~T~ aSubscriber) ~T~
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
    class CompletionState {
		<<MutableProperty>>
		+name() String
		+percentage() Float
	}

```

## APPLICATION PACKAGE

| Class Type             | Motivation                                                           |
|:-----------------------|:---------------------------------------------------------------------|
| ApplicationService     | Represent a component of a service layer hosted by a domain boundary |
| ConfigurationViolation |                                                                      |
| IApplicationService    | Applicative behaviors contract regarding an application layer        |
| INotificationService   | Publishing of events from event store via messaging infrastructure   |

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

## INFRASTRUCTURE PACKAGE

| Class Type                                 | Motivation                                                                                                                                                                                                                                                                                                          |
|:-------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| AbstractSnapshotProcess                    | Snapshot generation and persistence can be delegated to a background thread, following an event stream evolutions                                                                                                                                                                                                   |
| DomainEventInMemoryStoreImpl               | Implementation of EventStore based on in-memory storage                                                                                                                                                                                                                                                             |
| IDomainRepository                          | Represents a persistence-oriented repository (also sometimes called Aggregate store, or Aggregate-Oriented database) basic contract for a bounded context.<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts |
| IDomainStore                               | Stream store (with an append-only approach) which maintain history of a type of domain fact (e.g Aggregate versions).<br>For example, manage the domain data (e.g sharded database for a tenant) ensuring isolation of persistent domain model from the other bounded contexts                                      |
| MessageHeader                              |                                                                                                                                                                                                                                                                                                                     |
| ResourceDescriptor                         | Common description of a resource managed into a Redis context                                                                                                                                                                                                                                                       |
| SnapshotProcessEventStreamPersistenceBased | Implementation class of a snapshot process which produce and manage snapshots                                                                                                                                                                                                                                       |

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
        +queryNameBasedOn() String
    }
    class IDomainStore~T~ {
        <<interface>>
        +append(T fact, ISessionContext ctx)
        +findEventFrom(Identifier uid, ISessionContext ctx)
    }
    class DomainEventInMemoryStoreImpl {
        <<EventStore>>
    }

```

### Infrastructure.util sub-package
Utility classes supporting the infrastructure components.

| Class Type                 | Motivation                                                                                                                                                                                                                                       |
|:---------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| AbstractDataViewConvention | Convention reference relative to an element managed into a data view (e.g pattern of date) and/or read-model of views.<br>This type of convention is helping to standardize shared elements between systems which are using data views' elements |
| DateConvention             | Convention relative to the translation of a date into or from a string version managed into the infrastructure layer                                                                                                                             |

#
[Back To Home](README.md)
