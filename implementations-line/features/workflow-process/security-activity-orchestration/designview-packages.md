## PURPOSE
Presentation of the design view regarding the sub-packages of `org.cybnity.feature.security_activity_orchestration` project.

# DESIGN VIEW
The technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|Attribute| |
|IState| |
|ProcessDescriptor| |

# STRUCTURE MODELS
Several packages are implemented to organize the components (e.g specification elements, implementation components) additionnaly to these provided by this package.

## domain.model package

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
		+name() Attribute
	}

```
#
[Back To Home](README.md)
