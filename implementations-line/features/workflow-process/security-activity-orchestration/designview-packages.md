## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.feature.security_activity_orchestration` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|CompletionState|Represent a state of completion defining by a name and optionally by a percentage value about reached completion rate|
|IState|Represent a providing contract regarding the description of a state (e.g a process step) based on a collection of attributes|
|Process|Represent a workflow based on steps (e.g risk management process) realizable by an actor and specifying an organizational model framing activities|
|ProcessBuilder|Utility class implementing the builder design pattern to create easily a process instance automatically mananing the instantiation dependencies between internal domain facts|
|ProcessDescriptor|Definition regarding a process, that can be changed, and which need to be historized in an immutable way the history of changes (version of this information)|
|Staging|Ordered and/or paralellized workflow specification based on steps, that can be changed, and which is historized in an immutable way the history of changes|
|Step|Represent a workflow phase (e.g also named process step) that define a state of a working set (e.g unique or multiple actions)|

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
	Process *-- "1" CompletionState : completion
	Process <.. ProcessBuilder : build instances
	note for ProcessBuilder "Builder pattern without rupture of the immutability<br>(valideConformity() called by build() method), allowing<br>templating via new Process.builder(identity).activation(...)<br>.description(...).steps(...).build();<br><br>"
	IAggregate <|.. Process
	note for IAggregate "Immutable framework based"
	ActivityState "1" --* Step : activation
	Step *-- "1" CompletionState : completion
	Process *-- "1" ProcessDescriptor : description
	Process *-- "0..1" Staging : staging
	Staging *-- "1..*" Step : steps
	Step ..> "1 commandProcessor" ChainCommandHandler : delegate command handling
	Step *-- "1..*" Attribute : properties
	IWorkflowCommandHandler <|.. Step
	IState <|.. Step
	Process *-- "1" ActivityState : activation
	Unmodifiable <|.. Attribute
	note for Attribute "Domain framework based as immutable property<br>(e.g primary responsibility stakeholder, supporting roles)<br><br>"

	class Process {
		<<Aggregate>>
		+Process(Entity predecessor, Identifier id, HashMap<String, Object> descriptionAttributes)
		+Process(Entity predecessor, LinkedHashSet~Identifier~ identifiers, HashMap<String, Object> descriptionAttributes)
		#Process(Entity predecessor, LinkedHashSet~Identifier~ identifiers, ProcessDescriptor description,
			ActivityState activation, CompletionState completion, Staging staging)
		+name() String
		+activation() ActivityState
		+changeActivation(ActivityState state)
		+completion() CompletionState
		+changeCompletion(CompletionState state)
		+description() ProcessDescriptor
		+changeDescription(ProcessDescriptor description)
		+staging() Staging
		+changeStaging(Staging staging)
		-checkStagingConformity(Staging staging, Entity processOwning)
		-checkDescriptionConformity(ProcessDescriptor description, Entity processOwning)
		-checkCompletionConformity(CompletionState state, Entity processOwning)
		-checkActivationConformity(ActivityState state, Entity processOwning)
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
	}
    class ProcessDescriptor {
		<<MutableProperty>>
		-PropertyAttributeKey.Name : String
		-PropertyAttributeKey.Properties : Collection~Attribute~
		-PropertyAttributeKey.TemplateEntityRef : EntityReference
		+getName() String
		+properties() Collection~Attribute~
		+owner() Entity
		+templateEntityRef() EntityReference
	}
	class IState {
		<<interface>>
		+properties() Collection~Attribute~
	}
	class ActivityState {
		<<org.cybnity.framework.domain.model.MutableProperty>>
		-PropertyAttributeKey.StateValue : Boolean
		+isActive() Boolean
		+ownerReference() EntityReference
		+checkActivationConformity(ActivityState state, Entity owner)$
	}
    class CompletionState {
		<<MutableProperty>>
		-PropertyAttributeKey.Name : String
		-PropertyAttributeKey.Percentage : Float
		+name() String
		+percentage() Float
		+ownerReference() EntityReference
		+checkCompletionConformity(CompletionState state, Entity owner)$
	}
	class IAggregate {
		<<interface>>
	}
	class IWorkflowCommandHandler {
		<<interface>>
	}
	class Step {
		<<MutableProperty>>
		-PropertyAttributeKey.Name : String
		-PropertyAttributeKey.Properties : Collection~Attribute~
		-PropertyAttributeKey.SubSteps : List~Step~
		+Step(EntityReference propertyOwner, String name, ChainCommandHandler commandHandlingDelegate)
		+Step(EntityReference propertyOwner, String name, ChainCommandHandler commandHandlingDelegate,
			Step... predecessors)
		+Step(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ChainCommandHandler commandHandlingDelegate)
		+Step(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ChainCommandHandler commandHandlingDelegate, Step... predecessors)
		+properties() Collection~Attribute~
		+name() String
		+setCommandProcessor(ChainCommandHandler commandHandlingDelegate)
		+addParallelNextHandler(ChainCommandHandler next)
		+ownerReference() EntityReference
		+handle(Command request)
		+subStates() List~IState~
		+activation() ActivityState
		+changeActivation(ActivityState state)
		+completion() CompletionState
		+changeCompletion(CompletionState state)
	}
	class ChainCommandHandler {
      <<abstract>>
	}
	class ProcessBuilder {
		final -processIdentifiers : LinkedHashSet~Identifier~
		final -processParent : Entity
		-activation : Boolean
		-completionName : String
		-currentPercentageOfCompletion : Float
		-description : Collection~Attribute~
		-processName : String
		-templateEntityRef : EntityReference
		-i18nTranslation : Locale

		#ProcessBuilder(@required LinkedHashSet~Identifier~ processIdentifiers, Entity processParent, String processName)
		+instance(LinkedHashSet~Identifier~ processIdentifiers, Entity processParent, String processName)$ ProcessBuilder
		+build() Process
		+valideConformity(Process instance)$
		+withActivation(Boolean isActiveStatus) ProcessBuilder
		+withCompletion(@required String named, Float currentPercentageOfCompletion) ProcessBuilder
		+withDescription(Collection~Attribute~ properties) Processbuilder
		+withTemplateEntityReference(EntityReference templateRef) ProcessBuilder
		+withI18NTranslation(Locale i18nTranslation) ProcessBuilder
		#getI18NProperties() ResourceBundle
	}

```
#
[Back To Home](README.md)
