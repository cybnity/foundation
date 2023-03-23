## PURPOSE
Presentation of the structural components regarding architecture implementing immutable design patterns.

# DESIGN VIEW
Several structural patterns are supporting the immutable capabilities and are reusable (e.g by inheritance) for coding of application elements.

### Key Components
For more detail, the technical description regarding behavior and best usage is maintained into the Javadoc of each component.

|Class Type|Motivation|
| :-- | :-- |
|BasicConfigurationVariable| |
|Context| |
|IContext| |
|IReadableConfiguration| |
|MissingConfigurationException| |
|UnoperationalStateException| |

## STRUCTURE MODELS
Presentation of the design view of the `org.cybnity.framework` main project's artifacts package.

### Sub-Packages
See complementary presentation of [detailed structure models implemented into the sub-packages](designview-packages.md).


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
    IReadableConfiguration <|.. BasicConfigurationVariable
    IContext <|.. Context
    UnoperationalStateException <|-- MissingConfigurationException
    Exception <|-- UnoperationalStateException

    class IContext {
        <<interface>>
        +get(Class~?~ typeOfResult) Object
        +get(String resourceName) Object
        +get(IReadableConfiguration config) String
        +addResource(Object instance, String resourceName, boolean forceReplace) boolean
        +remove(String resourceName) boolean
    }
    class Context {
        -resources : ConcurrentHashMap
        +Context()
        +remove(String resourceName) boolean
        +addResource(Object instance, String resourceName, boolean forceReplace) boolean
        +get(Class~?~ typeOfResult) Object
        +get(String resourceName) Object
        +get(IReadableConfiguration config) String
    }
    class BasicConfigurationVariable {
        <<enumeration>>
        -name : String
        -BasicConfigurationVariable(String aName)
        +getName() String
    }
    class HealthyOperableComponentChecker {
        <<abstract>>
        -checkedStatus : boolean = false
        +checkOperableState()
        +isOperableStateChecked() boolean
        #checkConfigurationVariables()$
        #checkOperatingFiles()$
        #checkResourcesPermissions()$
    }
    class IReadableConfiguration {
        +getName() String
    }
```

#
[Back To Home](README.md)
