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
The JanusGraph repository implementation library is mainly providing helper components (including automated behaviour common to any data-model repository and projections organization) and that are ready to be extended by custom data-view projections models.

| Class Type                           | Motivation                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|:-------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| AbstractDomainGraphImpl              | Represent a graph of domain elements (e.g structure of data views relative to a domain scope) and implementation capabilities regarding schema manipulation, graph data update, and graph queries via the selected query framework (e.g Tinkerpop)                                                                                                                                                                                                                                         |
| AbstractReadModelProjectionsSet      | Implementation class of using JanusGraph database projections provider (e.g projection builder defining graph read-model of domain's objects).<br> Set of projection is generally defined per domain or subdomain scope regarding all supported projections.<br>It's the data layer's set of graph manipulation capabilities.<br>This read-model management component is responsible to select and maintain up-to-date the data views projections compatible with a domain graph perimeter |
| AbstractReadModelRepository          | Read-Model repository of data view managed regarding a scope                                                                                                                                                                                                                                                                                                                                                                                                                               |
| GraphDataViewNamingConvention        | Utility class helping to define standardized logical names regarding the persistent data view manageable by a repository                                                                                                                                                                                                                                                                                                                                                                   |
| AbstractGraphDataViewTransactionImpl | Graph data view projection usable as read-model transaction, which defined the graph manipulation rules (e.g query with parameters; graph change directives and linking rules).<br>It manages the identification of one or several events as Data Layer inputs supported by a referenced graph model, and execute on it the read or change instructions on the graph model                                                                                                                 |

## GUIDELINE & SAMPLE PROJECT
An example of application domain repository is provided into the unit test source codes for help to development of a CYBNITY domain repository relative to an Aggregate domain object.
Complementary to the support of unit test ensuring validation of utility classes provided by the project, this implementation source codes allow developer to identify which types of class are required to implement.

See [repository sample implementation structure](designview-read-model-repository-sample-models.md) that give an overview of key implementation components reusing the library classes for coding of a domain repository read-model projections perimeter.