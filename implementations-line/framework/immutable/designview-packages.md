## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.framework.immutable` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|AuditLog| |
|BaseConstants| |
|ChildFact| |
|Entity| |
|EntityReference| |
|Evaluations| |
|ExecutableComponentChecker| |
|FactEdge| |
|FactsProvider| |
|FactRecord| |
|FactType| |
|HistoryState| |
|IDeletionFact| |
|IdentifiableFact| |
|Identifier| |
|IFactRepository| |
|IFactStore| |
|IGroup| |
|IHistoricalFact| |
|IMember| |
|ImmutabilityException| |
|IOwnership| |
|IReferenceable| |
|IRestorationFact| |
|IUniqueness| |
|IVersionable| |
|LocationIndependentIdentityNaturalKeyBuilder| |
|Membership| |
|MutableProperty| |
|NaturalKeyIdentifierGenerator| |
|QualitativeDataBuilder|Builder pattern implementation of data quality ensuring the application of quality rules on object to intantiate|
|QualitativeDataGenerator|Producer of qualitative data that manage execution of quality rules for instance to build as ACID model|
|RelationRole| |
|StringBasedNaturalKeyBuilder| |
|StructuralVersionStrategy| |
|Transaction| |
|TransactionItem| |
|TypeVersion| |
|Unmodifiable| |
|VersionConcreteStrategy| |

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

# IMMUTABLE
Main project's package regarding the immutability capabilities, this package include severel sub-packages additionnaly to these components.

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
    Unmodifiable <|-- IHistoricalFact
    IVersionable <|-- IHistoricalFact
    Serializable <|-- IHistoricalFact
    IHistoricalFact <|.. MutableProperty
    IHistoricalFact <|-- IRestorationFact
    IReferenceable <|.. Entity
    ChildFact ..|> IdentifiableFact
    IdentifiableFact <|.. Entity
    IHistoricalFact <|.. EntityReference
    IHistoricalFact <|.. ChildFact
    IHistoricalFact <|.. Entity
    EntityReference "0..n" --o EntityReference : prior
    Entity "1" --o EntityReference : entity
    Entity "0..1" <-- EntityReference : referenceRelation
    IHistoricalFact <|-- IDeletionFact
    IHistoricalFact <|.. Transaction
    HistoryState "0..1" <-- MutableProperty : historyStatus
    Entity "1" --o MutableProperty : owner
    Transaction --> "0..n" TransactionItem : items

    class Transaction {
        #transactionParentContext : Entity
        #createdAt : OffsetDateTime
        -transactionId : Identifier
        +Transaction(Entity transactionParentContext, Identifier id)
        +immutable() Serializable
        +transactionId() Identifier
        +occurredAt() OffsetDateTime
        +transactionParentContext() Entity
        +getItems() Set~TransactionItem~
        +setItems(Set~TransactionItem~ items)
        +versionHash() String
    }
    class TransactionItem {
        -itemContext : Entity
        -propertyState : MutableProperty
        +TransactionItem(Entity childEntityContext, MutableProperty childPropertyVersion)
        +getItemContext() Entity
        +getPropertyState() MutableProperty
    }
    class HistoryState {
        <<enumeration>>
        ARCHIVED
        MERGED
        COMMITTED
        CANCELLED
    }
    class MutableProperty {
        <<abstract>>
        #value : HashMap
        #prior : LinkedHashSet~MutableProperty~
        #changedAt : OffsetDateTime
        +MutableProperty(Entity propertyOwner, HashMap propertyCurrentValue, HistoryState status)
        +MutableProperty(Entity propertyOwner, HashMap propertyCurrentValue, HistoryState status, MutableProperty... predecessors)
        +equals(Object obj) boolean
        +occurredAt() OffsetDateTime
        +owner() Entity
        +historyStatus() HistoryState
        +setHistoryStatus(HistoryState state)
        +enhanceHistoryOf(MutableProperty versionToEnhance, HistoryState versionState) MutableProperty
        +changesHistory() Set~MutableProperty~
        +updateChangesHistory(Set~MutableProperty~ versions)
    }
    class IHistoricalFact {
        <<interface>>
        +occurredAt() OffsetDateTime
    }
    class Unmodifiable {
        <<interface>>
        +immutable() Serializable
    }
    class IVersionable {
        <<interface>>
        +versionHash() String
    }
    class Serializable {
        <<interface>>
    }
    class IReferenceable {
        <<interface>>
        +reference() EntityReference
    }
    class IRestorationFact {
        <<interface>>
        +deletion() IDeletionFact
    }
    class IdentifiableFact {
        <<interface>>
        +identified() Identifier
        valueHashCodeContributors() String[]
    }
    class IDeletionFact {
        <<interface>>
        +deleted() Entity
    }
    class Entity {
        #identifiedBy : ArrayList~Identifier~
        #createdAt : OffsetDateTime
        +Entity(Identifier id)
        +Entity(LinkedHashSet~Identifier~ identifiers)
        +identifiers() Collection~Identifier~
        +occurredAt() OffsetDateTime
        +equals(Object fact) boolean
        +valueHashCodeContributors() String[]
        +hashCode() int
        +reference() EntityReference
    }
    class ChildFact {
        <<abstract>>
        #parent : Entity
        #identifiedBy : ArrayList~Identifier~
        #createdAt : OffsetDateTime
        +ChildFact(Entity predecessor, Identifier id)
        +ChildFact(Entity predecessor, LinkedHashSet~Identifier~ identifiers)
        +identifiers() Collection~Identifier~
        +occurredAt() OffsetDateTime
        +parent() Entity
        +valueHashCodeContributors() String[]
        +hashCode() int
        +equals(Object fact) boolean
        #generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)$ Identifier
        #generateIdentifierPredecessorBased(Entity predecessor, Collection~Identifier~ childOriginalIds)$ Identifier
    }
    class EntityReference {
        -historyStatus : HistoryState
        -changedAt : OffsetDateTime
        +EntityReference(Entity referenceOwner, Entity inRelationWith, HistoryState status)
        +EntityReference(Entity referenceOwner, Entity inRelationWith, HistoryState status, EntityReference... predecessors)
        +historyStatus() HistoryState
        +setHistoryStatus(HistoryState state)
        +getEntity() Entity
        +getReferencedRelation() Entity
        #setReferencedRelation(Entity referencedRelation)
        +immutable() Serializable
        +occurredAt() OffsetDateTime
        +versionHash() String
        +equals(Object obj) boolean
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
    Unmodifiable <|.. AuditLog
    Serializable <|.. AuditLog
    Unmodifiable <|-- Identifier
    Serializable <|-- Identifier
    Exception <|-- ImmutabilityException
    class Identifier {
        <<interface>>
        +name() String
        +value() Serializable
        +valueHashCodeContributors() String[]
    }
    class AuditLog {
        +AuditoLog()
        +immutable() Serializable
    }
    class Evaluations {
        +isIdentifiedEquals(IdentifiableFact fact, IdentifiableFact otherFact)$ boolean
        +isEpochSecondEquals(OffsetDateTime aDate, OffsetDateTime another)$ boolean
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
    IHistoricalFact <|-- IMember
    IHistoricalFact <|-- IGroup
    IMember "1" --o Membership : member
    IGroup "1" --o Membership : group

    class IGroup {
        <<interface>>
        +identified() Identifier
    }
    class IOwnership {
        <<interface>>
        +childrenOfParent(IHistoricalFact parent) Collection~ChildFact~
    }
    class BaseConstants {
        <<enumeration>>
        IDENTIFIER_ID
    }
    class IMember {
        <<interface>>
        +identified() Identifier
    }
    class Membership {
        <<abstract>>
        #createdAt : OffsetDateTime
        +Membership(IMember member, IGroup group)
        +member() IMember
        +group() IGroup
        +occurredAt() OffsetDateTime
    }
    class IHistoricalFact {
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
    NaturalKeyIdentifierGenerator o-- "1" LocationIndependentIdentityNaturalKeyBuilder : builder
    LocationIndependentIdentityNaturalKeyBuilder <|-- StringBasedNaturalKeyBuilder

    class LocationIndependentIdentityNaturalKeyBuilder {
        <<abstract>>
        +convertAllLettersToLowerCase()$
        +dropPunctuationMarks()$
        +removeAnySpace()$
        +generateMinimumCharactersQuantity()$
    }
    class NaturalKeyIdentifierGenerator {
        +NaturalKeyIdentifierGenerator(LocationIndependentIdentityNaturalKeyBuilder builder)
        +build()
    }
    class StringBasedNaturalKeyBuilder {
        -transformationResult : String
        -minCharacters : int = 0
        -isPartialTransformationStarted : boolean = false
        +StringBasedNaturalKeyBuilder(String aNaturalKey, int minLetterQty)
        +convertAllLettersToLowerCase()
        +dropPunctuationMarks()
        +removeAnySpace()
        +generateMinimumCharactersQuantity()
        +getResult() String
    }
```

## PERSISTENCE SUB-PACKAGE

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
    Serializable <|.. RelationRole
    RelationRole "1" --* FactEdge : factsRelationType
    FactType "1" --o RelationRole : relationDeclaredByOwnerType
    FactType "1" --o RelationRole : relationTargetingOtherFactType
    Unmodifiable <|.. RelationRole
    Unmodifiable <|.. FactEdge
    Unmodifiable <|.. FactType
    IVersionable <|.. RelationRole
    IVersionable <|.. FactEdge
    IVersionable <|.. FactType
    IUniqueness <|.. RelationRole
    IUniqueness <|.. FactEdge
    IUniqueness <|.. FactType
    Serializable <|.. FactEdge
    Serializable <|.. FactType

    class Unmodifiable {
        <<interface>>
    }
    class IVersionable {
        <<interface>>
    }
    class Serializable {
        <<interface>>
    }
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
    class IUniqueness {
        <<interface>>
        +basedOne() Set~Field~
    }
    class RelationRole {
        -minLetterQty : int = 50
        -name : String
        -id : String
        +RelationRole(String roleName, FactType relationDeclaredByOwnerType, FactType relationTargetingOtherFactType, String identifier)
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
    QualitativeDataBuilder <-- QualitativeDataGenerator : builder
    IHistoricalFact <|.. FactRecord
    IUniqueness <|.. FactRecord
    TypeVersion "1" --* FactRecord : factTypeVersion
    Serializable <|.. TypeVersion

    class IHistoricalFact {
        <<interface>>
    }
    class TypeVersion {
        -hash : String
        -id : String
        -minLetterQty : int = 20
        -factType : FactType
        +TypeVersion(Class~?~ subject, String identifier)
        +TypeVersion(Class~?~ subject)
        +factType FactType
        +hash() String
        +id() String
    }
    class IUniqueness {
        <<interface>>
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
    class QualitativeDataGenerator {
        +QualitativeDataGenerator(QualitativeDataBuilder builder)
        +build()
    }

```

## REGISTRY SUB-PACKAGE

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
    IOwnership <|.. FactsProvider
    class FactsProvider {
        <<abstract>>
    }
    class IOwnership {
        <<interface>>
    }

```

## UTILITY SUB-PACKAGE

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
    HealthyOperableComponentChecker <|.. ExecutableComponentChecker
    StructuralVersionStrategy <|-- VersionConcreteStrategy

    class ExecutableComponentChecker {
        <<abstract>>
        -context : IContext
        +ExecutableComponentChecker(IContext ctx)
        +ExecutableComponentChecker()
        #getContext() IContext
        #checkConfigurationVariables()
        #requiredEnvironmentVariables()$ Set~IReadableConfiguration~
    }
    class StructuralVersionStrategy {
        <<abstract>>
        +composeCanonicalVersionHash(Class~?~ factType)$ String
    }
    class VersionConcreteStrategy {
        +VersionConcreteStrategy()
        -getAllFields(Class~?~ clazz) List~Field~
        +composeCanonicalVersionHash(Class~?~ factType) String
    }

```

#
[Back To Home](README.md)
