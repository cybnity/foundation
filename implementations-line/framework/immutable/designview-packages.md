## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.framework.immutable` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|FactEdge| |
|FactRecord| |
|FactType| |
|IFactRepository| |
|IFactStore| |
|IUniqueness| |
|QualitativeDataBuilder|Builder pattern implementation of data quality ensuring the application of quality rules on object to intantiate|

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

## PERSISTENCE

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
    Unmodifiable <|.. FactEdge
    Unmodifiable <|.. FactType
    IVersionable <|.. FactEdge
    IVersionable <|.. FactType
    RelationRole "1" --* FactEdge : factsRelationType
    IHistoricalFact <|.. FactRecord
    IUniqueness <|.. FactEdge
    IUniqueness <|.. FactRecord
    IUniqueness <|.. FactType
    Serializable <|.. FactEdge
    Serializable <|.. FactType
    TypeVersion <-- FactRecord : factTypeVersion

    class FactType {
        -name : String
        -id : String
        -minLetterQty : int = 50
        +FactType(String categoryName, String identifier)
        +FactType(String categoryName)
        +basedOn() Set~Field~
        +id() String
        +name() String
        +immutable() Serializable
        +versionHash() String
    }
    class IHistoricalFact {
        <<interface>>
    }
    class IUniqueness {
        <<interface>>
        +basedOne() Set~Field~
    }
    class RelationRole {
    }
    class FactEdge {
        -successorId : String
        -predecessorId : String
        +FactEdge(String successorFactIdentifier, String predecessorFactIdentifier, RelationRole factsRelationType)
        +basedOn() Set~Field~
        +factsRelationType() RelationRole
        +predecessorId() String
        +successorId() String
        +immutable() Serializable
        +versionHash() String
    }
    class FactRecord {
        -body : Serializable
        -factOccurredAt : OffsetDateTime
        -recordedAt : OffsetDateTime
        -bodyHash : int
        -factId : Integer
        +FactRecord(IHistoricalFact originFact)
        +basedOn() Set~Field~
        +getFactId() Integer
        +bodyHash() int
        +body() Serializable
        +factTypeVersion() TypeVersion
        +recordedAt() OffsetDateTime
        +immutable() Serializable
        +versionHash() String
        +occurredAt() OffsetDateTime
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
    class IFactRepository~T~ {
        <<interface>>
        +nextIdentity() T
        +factOfId(Identifier aFactId) T
        +remove(T fact) boolean
        +removeAll(Collection~T~ aFactsCollection)
        +save(T aFact) T
        +saveAll(Collection~T~ aFactsCollection)
    }
    class IFactStore~T~ {
        <<interface>>
        +append(T fact)
        +findEventFrom(Identifier uid) T
    }
    class QualitativeDataBuilder {
        <<abstract>>
        +makedCompleteness()$
        +makeConsistency()$
        +makeConformity()$
        +makeAccuracy()$
        +makeIntegrity()$
        +makeTimeliness()$
        +getResult()$ Object
    }

```


## REGISTRY

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


```

## UTILITY

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


```

#
[Back To Home](README.md)
