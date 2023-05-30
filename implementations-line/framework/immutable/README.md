## PURPOSE
Presentation of the transversal framework components regarding architecture components respecting the immutability design patterns.

# DESIGN VIEW
Several structural patterns are supporting the immutability and are reusable (e.g by inheritance) for coding of software elements (e.g aggregate, value objects).

|Class Type|Motivation|
| :-- | :-- |
|ChildFact|Represent a strict hierarchy among entities (parent-child relationship betwwen a successor and ont of its predecessors). Ownership pattern regarding fact referencing a parent fact as predecessor|
|Context|Utility class for properties (e.g environment variables read) and resources (e.g container for inheritance by specific applicative, session sub-contexts) management|
|Entity|Represent the creation of a historical fact equals to a immutable historical fact, containing only identifying information(s)|
|EntityReference|Mutable relationship between entities|
|HistoryState|State of decision taken by a user regarding a previous property value concurrently changed|
|IDeletionFact|Represent a deletion of an entity|
|IdentifiableFact|Identification contract regarding an immutable object|
|Identifier|Identifying information (e.g natural key, GUID, or some combination of those and other location-independent identifiers|
|IGroup|Logical group regarding a type of entity|
|IHistoricalFact|Immutable fact created in a system, identified uniquely|
|IMember|Represent a member of a logical group into a membership relation|
|IOwnership|Special case of the Entity pattern, where the entity's identifiers include the identity of an owner|
|IRestorationFact|Restoration fact references a prior deletion. Extension of the Delete pattern|
|Membership|More flexible grouping relationship than the ownership. A many-to-many relationship typically denotes Membership|
|MutableProperty|Represents values that change on a property (simple or complex)|
|NaturalKeyIdentifierGenerator|Utility class helping to generate identifier value based on natural key (location-independent identity pattern)|
|Unmodifiable|Contract of immutability assignable to any element of the architecture|

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
    Unmodifiable <|-- Identifier
    IdentifiableFact <|.. ChildFact
    IdentifiableFact <|.. Entity
    Unmodifiable <|-- IHistoricalFact
    IHistoricalFact <|.. ChildFact
    Entity "1 -entity" --o MutableProperty
    IHistoricalFact <|.. MutableProperty
    IHistoricalFact <|.. EntityReference
    EntityReference *-- "1 -historyStatus" HistoryState
    MutableProperty *-- "1 -historyStatus" HistoryState
    IHistoricalFact <|-- IGroup
    IHistoricalFact <|.. Membership
    Membership *-- "1 -group" IGroup
    Membership *-- "1 member" IMember
    IHistoricalFact <|-- IMember
    MutableProperty o-- "0..* #prior" MutableProperty
    IHistoricalFact <|.. Entity
    IDeletionFact --|> IHistoricalFact
    IRestorationFact --|> IHistoricalFact

    class IHistoricalFact {
        <<interface>>
        +occuredAt() Temporal
    }
    class IdentifiableFact {
        <<interface>>
        +identified() Identifier
    }
    class Entity {
        <<abstract>>
        #identifierBy : List~Identifier~
        #createdAt : Temporal
    }
    class ChildFact {
        <<abstract>>
        #parent : Entity
        #identifierBy : List~Identifier~
        #createdAt : Temporal
        #generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId) Identifier
        #generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds) Identifier
    }
    class IDeletionFact {
        <<interface>>
        +deleted() Entity
    }
    class Unmodifiable {
        <<interface>>
        +immutable() Object
    }
    class Identifier {
        <<interface>>
        +name() String
        +value() Object
    }
    class HistoryState {
        <<enumeration>>
        ARCHIVED, MERGED, COMMITTED
    }
    class EntityReference {
        <<abstract>>
        #entity : Entity
        #referencedRelation : Entity
        #prior : HashSet~EntityReference~
        #changedAt : Temporal
    }
    class IGroup {
        <<interface>>
        +identified() Identifier
    }
    class IMember {
        <<interface>>
        +identified() Identifier
    }
    class Membership {
        <<abstract>>
        #createdAt : Temporal
    }
    class MutableProperty {
        <<abstract>>
        #changedAt : Temporal
        #value : HashMap~String, Object~
    }
    class IRestorationFact {
        <<interface>>
        +deletion() DeletionFact
    }
```

### Example of Instantiation
Presentation of an example of instances representing facts history (as events graph) using a object model reusing the structural patterns.
```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '14px',
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
flowchart BT
    software(("#60;#60;Group#62;#62;<br/>Executable Software"))
    electronicdevice(("#60;#60;Group#62;#62;<br/>Manufactured Electronic Device"))
    system(("#60;#60;Membership#62;#62;<br/>Assembled Digital Product")) --> software
    system --> electronicdevice
    os(("#60;#60;ChildFact#62;#62;<br/>Operating System Started")) --> system
    application(("#60;#60;Entity#62;#62;<br/>Applicative Digital Twin Started")) --> system
    systemlog1(("#60;#60;ChildFact#62;#62;<br/>Monday System Info Log")) --> os
    systemlog2(("#60;#60;ChildFact#62;#62;<br/>Tuesday System Info Log")) --> os
    systemlog1deletion(("#60;#60;DeletionFact#62;#62;<br/>Monday System Info Log Deletion")) --> systemlog1
    style systemlog1deletion stroke-dasharray: 5 5
    systemlog1restoration(("#60;#60;Restoration#62;#62;<br/>Monday System Info Log Restoration")) --> systemlog1deletion
    runtimeconfig(("#60;#60;MutableProperty#62;#62;<br/>Runtime Configuration V1")) --> application
    runtimeconfig2(("#60;#60;MutableProperty#62;#62;<br/>Runtime Configuration V2")) --> application
    runtimeconfig2 --> runtimeconfig
    runtimeconfig3(("#60;#60;MutableProperty#62;#62;<br/>Current Runtime Configuration V3")) --> application
    runtimeconfig3 --> runtimeconfig2
    purchasedDeviceRegistration(("#60;#60;ChildFact#62;#62;<br/>Product Ownership Registered")) --> system
    deviceOwnerAccount(("#60;#60;Entity#62;#62;<br/>Created Device Owner Account")) --> purchasedDeviceRegistration
    interruptedSession(("#60;#60;EntityReference#62;#62;<br/>Interrupted Applicative Session")) --> application
    operatingSession(("#60;#60;EntityReference#62;#62;<br/>Activated Applicative Session")) --> deviceOwnerAccount
    operatingSession --> application
    interruptedSession --> deviceOwnerAccount
    interruptedSession --> operatingSession

```

### Persistence Package
Include types of generic facts usable by logs store (e.g traceability implementation regarding facts occured on a write model or on a read model).

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)
