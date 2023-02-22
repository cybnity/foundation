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
%%{init: {'theme': 'base', 'themeVariables': { 'background': '#ffffff', 'fontSize': '18px', 'primaryColor': '#fff', 'tertiaryBorderColor': '#3a5572', 'edgeLabelBackground':'#0e2a43', 'lineColor': '#3a5572', 'tertiaryColor': '#fff'}}}%%
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

    class HistoricalFact:::standard {
        <<interface>>
        +occuredAt() Temporal
    }
    class IdentifiableFact:::standard {
        <<interface>>
        +identified() Identifier
    }
    class Entity:::standard {
        <<abstract>>
        #identifierBy : List~Identifier~
        #createdAt : Temporal
    }
    class ChildFact:::standard {
        <<abstract>>
        #parent : Entity
        #identifierBy : List~Identifier~
        #createdAt : Temporal
    }
    class DeletionFact:::standard {
        <<interface>>
        +deleted() Entity
    }
    class Unmodifiable:::standard {
        <<interface>>
        +immutable() Object
    }
    class Identifier:::standard {
        <<interface>>
        +name() String
        +value() Object
    }
    class HistoryState:::standard {
        <<enumeration>>
        ARCHIVED, MERGED, COMMITTED
    }
    class EntityReference:::standard {
        <<abstract>>
        #entity : Entity
        #referenced : Entity
        #prior : Set~EntityReference~
        #changedAt : Temporal
    }
    class Group:::standard {
        <<interface>>
        +identified() Identifier
    }
    class Member:::standard {
        <<interface>>
        +identified() Identifier
    }
    class Membership:::standard {
        <<abstract>>
        #createdAt : Temporal
    }
    class MutableProperty:::standard {
        <<abstract>>
        #changedAt : Temporal
        #value : HashMap~String, Object~
    }
    class RestorationFact:::standard {
        <<interface>>
        +deletion() DeletionFact
    }

    classDef standard fill:#fff,stroke:#0e2a43,stroke-width:1px,color:#0e2a43

```

## Example of objects instantiations
Presentation of a sample of instances which can be linked together according to their roles into a facts graph.


    classDef bddtest fill:#3a5572,stroke:#3a5572,color:#fff
	classDef impact fill:#fff,stroke:#e5302a,stroke-width:1px,color:#e5302a
	classDef goal fill:#0e2a43,stroke:#0e2a43,color:#fff
