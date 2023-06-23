## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.feature.security_activity_orchestration` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|Attribute|Represent a characteristic which can be add to a topic (e.g a technical named attribute which is defined on-fly on an existing object, including a value). It's more or less like a generic property assignable to any topic or object (e.g property on a workflow step instance).<br>For example, can be use to defined a tag regarding a property added to a domain or aggregate object|
|IState|Represent a providing contract regarding the description of a state (e.g a process step) based on a collection of attributes|
|ProcessDescriptor|Definition regarding a process, that can be changed, and which need to be historized in an immutable way the history of changes (version of this information)|

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
	note for Attribute "Domain framework based as immutable property<br>(e.g primary responsibility stakeholder, supporting roles)<br><br>"
	Unmodifiable <|.. Attribute


	class Process {
		<<Aggregate>>
		+builder ProcessBuilder$
		+Process(Entity predecessor, Identifier id, HashMap<String, Object> descriptionAttributes)
		+Process(Entity predecessor, LinkedHashSet<Identifier> identifiers, HashMap<String, Object> descriptionAttributes)
		+Process(ProcessBuilder buildManager)
		+name() String
		+activation() ActivityState
		+changeActivation(ActivityState state)
		+completion() CompletionState
		+changeCompletion(CompletionState state)
		+description() ProcessDescriptor
		+changeDescription(ProcessDescriptor description)
		+staging() Staging
		+changeStaging(Staging stageing)
		-checkStagingConformity(Staging staging, Entity processOwner)
		-checkDescriptionConformity(ProcessDescriptor description, Entity processOwner)
		-checkCompletionConformity(CompletionState state, Entity processOwner)
		-checkActivationConformity(ActivityState state, Entity processOwner)
	}
	class Staging {
		<<MutableProperty>>
		+steps : List~Step~
		+Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
		+Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status, Staging... prior)
		+steps() List~Step~
		+owner() Entity
		-checkStepsConformity()
	}
	class Unmodifiable {
		<<interface>>
	}
	class Attribute {
		<<ValueObject>>
		-value : String
		-name : String
		+name() String
		+value() String
		+immutable() Serializable
	}
    class ProcessDescriptor {
		<<MutableProperty>>
		-PropertyAttributeKey.Name : String
		-PropertyAttributeKey.Properties : Collection~Attribute~
		+getName() String
		+properties() Collection~Attribute~
		+owner() Entity
	}
	class IState {
		<<interface>>
		+properties() Collection~Attribute~
	}
	class ActivityState {
		<<org.cybnity.framework.domain.model.MutableProperty>>
		+isActive() Boolean
	}
	class ITemplate {
		<<interface>>
		+name() String
	}
    class CompletionState {
		<<MutableProperty>>
	}
	class IAggregate {
		<<interface>>
	}
	class IWorkflowCommandHandler {
		<<interface>>
	}
	class Step {
		<<MutableProperty>>
		+Step(@required Collection~Attribute~ properties)
		+name() String
		+properties() Collection~Attribute~
	}
	class ChainCommandHandler {
      <<abstract>>
	}

```
#
[Back To Home](README.md)
