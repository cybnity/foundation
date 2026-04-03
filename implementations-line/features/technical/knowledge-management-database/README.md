## PURPOSE
This types of technical features are providing optimization services supporting the search (supported by indexing capabilities) and read of several types of data (e.g application activity traces histories) which are originally managed into specific storage technologies (e.g application domains knowledge graph database; bakcup of write-model domains streams; technical event logs).
These components develop data accessibility for any extended type of usage (e.g Artificial Intelligence).

# SOFTWARE COMPONENTS

- [janusgraph-repository](janusgraph-repository/README.md): common utility components library allowing implementation (by extension) of JanusGraph server usage. JanusGraph is integrated to ensure storage of queryable informations (e.g over graphQL language) which can be quickly retrieved during application process and/or pipelined data stream capabilities (e.g by domain layer). The backend persistence system (e.g Cassandra, or HBase) is responsible to store any type of data views (e.g read-model projections relative to an application domain). Some indexing capabilities can be supported by additional components (e.g persistence system completed by indexing system like Apache Solr or ElasticSearch) in real-time regarding technical logs stores to support the build and real-time refresh of data visualization systems (e.g metrics and KPI dashboard).
