## PURPOSE
Presentation of the domain components regarding architecture components respecting the domain-driven design patterns.

# DESIGN VIEW
Several structural patterns are supporting the domain (e.g event sourcing) and are reusable (e.g by inheritance) for coding of application domains elements.

|Class Type|Motivation|
| :-- | :-- |
|DomainEvent|Determine something that has happened in the system (e.g typically as a result of a command, or a change observed regarding a bounded context)|

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
    HistoricalFact <:-- DomainEvent
    IdentificableFact <:-- DomainEvent
    class DomainEvent {
        <<abstract>>
        -occuredOn : OffsetDateTime
        +DomainEvent()
        +DomainEvent(Entity uid)
    }
    class HistoricalFact {
        <<interface>>
    }
    class IdentifiableFact {
        <<interface>>
    }

```

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)
