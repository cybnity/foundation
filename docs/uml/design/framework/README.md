## PURPOSE
Presentation of the common element (e.g used by several modules).
The version of each diagram shown in this area is the latest produced via the source file.

# FRAMEWORK
Several sub-project are managed to support frameworked concepts.

## IMMUTABLE
All concepts allowing implementation of an immutable architecture's requirements are supported by the `org.cybnity.framework.immutable` java library [project](/implementations-line/framework/immutable).

|Class Type|Motivation|
| :-- | :-- |
|Entity|Represent the creation of a historical fact equals to a immutable historical fact, containing only identifying information(s)|
|HistoricalFact|Immutable fact created in a system, identified uniquely|
|Identifier|Identifying information (e.g natural key, GUID, or some combination of those and other location-independent identifiers|
|ChildFact|Represent a strict hierarchy among entities (parent-child relationship betwwen a successor and ont of its predecessors). Ownership pattern regarding fact referencing a parent fact as predecessor|

### Identifiable
![image](Identifiable_description.PNG)

### ResourceType
![image](ResourceType_description.PNG)

#
[Back To Parent](../)
