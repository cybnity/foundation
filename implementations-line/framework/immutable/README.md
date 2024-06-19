## PURPOSE
Presentation of the transversal framework components regarding architecture components respecting the immutability design patterns.

# FUNCTIONAL VIEW
Presentation of the capabilities area which allow realization of immutability requirements.

### Example of Instantiation
Presentation of an example of instances representing facts history (as events graph) using a object model reusing the structural patterns.
```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '14px',
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
flowchart BT
    software(("#60;#60;Group#62;#62;<br/>Executable Software"))
    electronicdevice(("#60;#60;Group#62;#62;<br/>Manufactured Electronic Device"))
    system(("#60;#60;Membership#62;#62;<br/>Assembled Digital Product")) --> software
    system --> electronicdevice
    os(("#60;#60;ChildFact#62;#62;<br/>Operating System Started")) --> system
    application(("#60;#60;Entity#62;#62;<br/>Applicative Digital Twin Started")) --> system
    systemlog1(("#60;#60;ChildFact#62;#62;<br/>Monday System Info Log")) --> os
    systemlog2(("#60;#60;ChildFact#62;#62;<br/>Tuesday System Info Log")) --> os
    systemlog1deletion(("#60;#60;DeletionFact#62;#62;<br/>Monday System Info Log Deletion")) --> systemlog1
    style systemlog1deletion stroke-dasharray: 5 5
    systemlog1restoration(("#60;#60;Restoration#62;#62;<br/>Monday System Info Log Restoration")) --> systemlog1deletion
    runtimeconfig(("#60;#60;MutableProperty#62;#62;<br/>Runtime Configuration V1")) --> application
    runtimeconfig2(("#60;#60;MutableProperty#62;#62;<br/>Runtime Configuration V2")) --> application
    runtimeconfig2 --> runtimeconfig
    runtimeconfig3(("#60;#60;MutableProperty#62;#62;<br/>Current Runtime Configuration V3")) --> application
    runtimeconfig3 --> runtimeconfig2
    purchasedDeviceRegistration(("#60;#60;ChildFact#62;#62;<br/>Product Ownership Registered")) --> system
    deviceOwnerAccount(("#60;#60;Entity#62;#62;<br/>Created Device Owner Account")) --> purchasedDeviceRegistration
    interruptedSession(("#60;#60;EntityReference#62;#62;<br/>Interrupted Applicative Session")) --> application
    operatingSession(("#60;#60;EntityReference#62;#62;<br/>Activated Applicative Session")) --> deviceOwnerAccount
    operatingSession --> application
    interruptedSession --> deviceOwnerAccount
    interruptedSession --> operatingSession

```

# DESIGN VIEW

- [Structure models presentation](designview-structure-models.md) that give an overview of some key components.
- [Sub-packages structure models](designview-packages.md) detailing specific sub-packages contents.

Several unit tests are implemented into the Maven project and propose examples of best usage of the framework elements (e.g for developer help who can reuse the library's elements).

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)

#
[Back To Home](../README.md)
