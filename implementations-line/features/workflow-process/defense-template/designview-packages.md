## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.feature.defense_template` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|Referential|Basis implementation class of a referential|
|Template|Common definition class regarding a specification object which define a template (e.g process aggregate object)|

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

## DOMAIN.MODEL PACKAGE

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
  ValueObject~String~ <|-- Referential
  IReferential <.. Referential
  Serializable <.. Referential
  IVersionable <.. Referential
  ITemplate <.. Template

  class Referential {
    <<ValueObject>>
    -acronym : String
    -label : String
  }

  class Template {
    <<Aggregate>>
    -originReferential : IReferential
    -name : MutableAttribute
    -modelOf : DomainObjectType
    +Template(Entity predecessor, Identifier id, IReferential originReferential, String name,
			DomainObjectType modelOf)
    +Template(Entity predecessor, LinkedHashSet~Identifier~ identifiers, IReferential originReferential, String name,
			DomainObjectType modelOf)
    -Template(Entity predecessor, LinkedHashSet~Identifier~ identifiers, IReferential originReferential,
			MutableAttribute name, DomainObjectType modelOf)
    +originReferential() IReferential
    +named() MutableAttribute
    +name() String
    +type() DomainObjectType
  }

```
#
[Back To Home](README.md)
