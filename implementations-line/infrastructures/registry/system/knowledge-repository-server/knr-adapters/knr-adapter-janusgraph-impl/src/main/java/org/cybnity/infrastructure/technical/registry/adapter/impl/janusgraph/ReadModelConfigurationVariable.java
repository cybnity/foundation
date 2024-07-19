package org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Enumeration defining a set of variables regarding a ReadModel provided by
 * the Graph server.
 * <p>
 * The configuration of each value regarding each environment variable enum, is
 * managed into the Helm values.yaml file regarding the executable system which
 * need to declare the environment variables as available for usage via this set
 * of enum.
 * About configuration name identified supported by JanusGraph:
 * - See <a href="https://docs.janusgraph.org/v0.4/basics/configuration/">JanusGraph basics configuration documentation</a> for more help.
 * - See <a href="https://docs.janusgraph.org/v0.4/basics/configuration-reference/">JanusGraph configuration Reference documentation</a> about each setting name supported.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum ReadModelConfigurationVariable implements IReadableConfiguration {

    /**
     * Type of storage backend of JanusGraph.
     * Example of supported values (see <a href="https://docs.janusgraph.org/v0.4/storage-backend/cassandra/">JanusGraph doc about Apache Cassandra backend storage</a> ):
     * - cql
     * - cassandrathrift
     * - cassandra
     * - embeddedcassandra
     * Example of local memory map in JVM supported value: inmemory
     * Example of HBase supported value: hbase
     */
    JANUSGRAPH_STORAGE_BACKEND("JANUSGRAPH_STORAGE_BACKEND", "storage.backend"),

    /**
     * Ip address of the JanusGraph storage server.
     * Example of supported value: 127.0.0.1, 10.10.0.1
     */
    JANUSGRAPH_STORAGE_HOSTNAMES("JANUSGRAPH_STORAGE_HOSTNAMES","storage.hostname"),

    /**
     * Type of indexing system used with the persistence engine.
     * Example of supported value: elasticsearch
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND("JANUSGRAPH_INDEX_SEARCH_BACKEND","index.search.backend"),

    /**
     * Ip addresses of the search engine servers.
     * Example of supported value:
     * - 100.100.101.1
     * - 100.100.101.1, 100.100.101.2
     */
    JANUSGRAPH_INDEX_SEARCH_HOSTNAMES("JANUSGRAPH_INDEX_SEARCH_HOSTNAMES", "index.search.hostname"),

    /**
     * Mode of Elasticsearch usage as remote search indexing system.
     * Example of supported value: true
     */
    JANUSGRAPH_INDEX_SEARCH_ELASTICSEARCH_CLIENT_ONLY("JANUSGRAPH_INDEX_SEARCH_ELASTICSEARCH_CLIENT_ONLY", "index.search.elasticsearch.client-only"),

    /**
     * Mode of Solr indexing system that can run in SolrCloud or Solr standalone instance (http).
     * Example of supported value:
     * - cloud
     * - http
     */
    JANUSGRAPH_INDEX_SEARCH_SOLR_MODE("JANUSGRAPH_INDEX_SEARCH_SOLR_MODE", "index.search.solr.mode")
    ;

    /**
     * Name of this environment variable currently hosted by the system environment.
     */
    private final String name;

    /**
     * Reference name of the JanusGraph configuration property.
     */
    private final String referenceConfigurationName;

    /**
     * Default constructor of a configuration variable that is readable from the
     * system environment variables set.
     *
     * @param aName                          Mandatory name of the environment variable that is readable from
     *                                       the current system environment (e.g defined by the runtime
     *                                       container or operating system).
     * @param aJanusGraphReferenceConfigName Mandatory name for configuration label supported by JanusGraph.
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    private ReadModelConfigurationVariable(String aName, String aJanusGraphReferenceConfigName) throws IllegalArgumentException {
        if (aName == null || "".equalsIgnoreCase(aName))
            throw new IllegalArgumentException("The name of this variable shall be defined!");
        if (aJanusGraphReferenceConfigName == null || "".equalsIgnoreCase(aJanusGraphReferenceConfigName))
            throw new IllegalArgumentException("The name of configuration property supported as reference by JanusGraph shall be defined!");
        this.name = aName;
        this.referenceConfigurationName = aJanusGraphReferenceConfigName;
    }

    /**
     * Get the configuration property name supported by JanusGraph.
     *
     * @return A name of configuration reference.
     */
    public String getReferenceConfigurationName() {
        return this.referenceConfigurationName;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
