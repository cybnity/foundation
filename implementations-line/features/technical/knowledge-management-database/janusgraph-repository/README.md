## PURPOSE
Presentation of the JanusGraph repository common implementation module, allowing to support the coding of graph repositories as Knowledge Repositories.

This project allow to develop customized read-model projections (optimized view of domain data in denormalized versions) in a standardized way, that are automatically synchronized with their dependent write-model stores.

This implementation organize the encapsulation of JanusGraph and sub-technology at the infrastructure layer level.

It's a specific set of reusable implementation components packaged as Java library which can be embedded by any CYBNITY other module.

|Cloudified As|Component Category|Component Type|Deployment Area|Platform Type|
| |CYBNITY Technical Service System|Infrastructure Service|CYBNITY Domains Area|IT & Data Platform|

# IMPLEMENTATION STACK
The main technologies set is:
- Java Library
- JanusGraph Core Library (see [Janusgraph technical documentation](https://docs.janusgraph.org/) for help)
  - Native support of property graph data model exposed by [Apache TinkerPop](https://tinkerpop.apache.org/)
  - Native support of [Gremlin graph traversal and query language](https://tinkerpop.apache.org/gremlin.html)

# DESIGN VIEW

- [Structure models presentation](designview-structure-models.md) that give an overview of some key components.

## GUIDELINE & SAMPLE PROJECT
An example of application domain repository is provided into the unit test source codes for help to development of a CYBNITY domain repository relative to an Aggregate domain object.
Complementary to the support of unit test ensuring validation of utility classes provided by the project, this implementation source codes allow developer to identify which types of class are required to implement:
- a domain repository (e.g SampleDomainTransactionsRepository)
- a read-model perimeter (e.g SampleDomainReadModelImpl) as set of data-view projections managed by a domain repository (that can manage one or multiple read-models over several graph models)
- a set of transaction components (e.g SampleDataViewStateTransactionImpl) implementing specific operations and queries relative to a Domain Aggregate (e.g aggregate object managed in a store by an application that need to expose a read-model version of its denormalized data views)
  - 2 data view transactions (CreateSampleDataViewVersion, UpgradeSampleDataViewVersion) ensuring specific modifications of a data-view status of a read-model projection state, according to evolution of an aggregate detected from its change events notifications
  - 1 data view query transaction (FindSampleDataViewVersionByEqualsLabel) providing read capability (from explicit Query command) of the read-model projection into JanusGraph and Gremlin traversal query language

See [repository sample implementation structure](designview-read-model-repository-sample-models.md) that give an overview of key implementation components reusing the library classes for coding of a domain repository read-model projections perimeter.