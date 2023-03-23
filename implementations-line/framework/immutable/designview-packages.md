## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.framework.immutable` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |

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
    Ummodifiable <|.. FactEdge
    IVersionable <|.. FactEdge
    RelationRole "1" --* FactEdge : factsRelationType
    IHistoricalFact <|.. FactRecord
    VersionConcreteStrategy <.. FactEdge : use
    IUniqueness <|.. FactEdge
    IUniqueness <|.. FactRecord
    Serializable <|.. FactEdge
    TypeVersion <-- FactRecord : factTypeVersion

    class IHistoricalFact {
        <<interface>>
    }
    class IUniqueness {
        <<interface>>
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
