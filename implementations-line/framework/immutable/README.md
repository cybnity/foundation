## PURPOSE
Presentation of the transversal framework components regarding architecture components respecting the immutability design patterns.

# DESIGN VIEW
Several structural patterns are supporting the immutability and are reusable (e.g by inheritance) for coding of application domains elements (e.g aggregate, domain object).

|Class Type|Motivation|
| :-- | :-- |
|Entity|Represent the creation of a historical fact equals to a immutable historical fact, containing only identifying information(s)|
|HistoricalFact|Immutable fact created in a system, identified uniquely|
|Identifier|Identifying information (e.g natural key, GUID, or some combination of those and other location-independent identifiers|
|ChildFact|Represent a strict hierarchy among entities (parent-child relationship betwwen a successor and ont of its predecessors). Ownership pattern regarding fact referencing a parent fact as predecessor|
|Unmodifiable|Contract of immutability assignable to any element of the architecture|
|DeletionFact|Represent a deletion of an entity|
|EntityReference|Mutable relationship between entities|
|Group|Logical group regarding a type of entity|
|HistoryState|State of decision taken by a user regarding a previous property value concurrently changed|
|IdentifiableFact|Identification contract regarding an immutable object|
|Member|Represent a member of a logical group into a membership relation|
|Membership|More flexible grouping relationship than the ownership. A many-to-many relationship typically denotes Membership|
|MutableProperty|Represents values that change on a property (simple or complex)|
|Ownership|Special case of the Entity pattern, where the entity's identifiers include the identity of an owner|
|RestorationFact|Restoration fact references a prior deletion. Dxtension of the Delete pattern|

## Structure Models

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
    Unmodifiable <|-- HistoricalFact
    HistoricalFact <|.. ChildFact
    Entity "1 -entity" --o MutableProperty
    HistoricalFact <|.. MutableProperty
    HistoricalFact <|.. EntityReference
    EntityReference *-- "1 -historyStatus" HistoryState
    MutableProperty *-- "1 -historyStatus" HistoryState
    HistoricalFact <|-- Group
    HistoricalFact <|.. Membership
    Membership *-- "1 -group" Group
    Membership *-- "1 member" Member
    HistoricalFact <|-- Member
    MutableProperty o-- "0..* #prior" MutableProperty
    HistoricalFact <|.. Entity
    DeletionFact --|> HistoricalFact
    RestorationFact --|> HistoricalFact

    class HistoricalFact {
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
    }
    class DeletionFact {
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
        #referenced : Entity
        #prior : Set~EntityReference~
        #changedAt : Temporal
    }
    class Group {
        <<interface>>
        +identified() Identifier
    }
    class Member {
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
    class RestorationFact {
        <<interface>>
        +deletion() DeletionFact
    }
```

## Example of objects instantiations
Presentation of a sample of instances which can be linked together according to their roles into a facts graph.

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
flowchart TB
    asset(Entity Device Asset) <-- board(ChildFact Manufactured Electronic Mainboard)
    asset <-- soft(ChildFact Installed Software)


```
