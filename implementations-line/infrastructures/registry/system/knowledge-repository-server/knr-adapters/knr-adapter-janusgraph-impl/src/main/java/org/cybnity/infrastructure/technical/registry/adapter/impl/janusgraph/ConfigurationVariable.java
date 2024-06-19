package org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Enumeration defining a set of variables regarding a graph provided by JanusGraph.
 * <p>
 * The configuration of each value regarding each environment variable enum, is
 * managed into the Helm values.yaml file regarding the executable system which
 * need to declare the environment variables as available for usage via this set
 * of enum.
 *
 * See <a href="https://docs.janusgraph.org/configs/configuration-reference/">Configuration Reference documentation</a> about JanusGraph configuration options.
 * See <a href="https://docs.janusgraph.org/configs/example-config/">configuration examples documentation</a> according to the type of backend storage to integrate with JanusGraph and deployment model.
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum ConfigurationVariable implements IReadableConfiguration {

    /**
     * Type of graph core factory (e.g org.janusgraph.core.JanusGraphFactory).
     */
    GREMLIN_GRAPH("GREMLIN_GRAPH"),

    /**
     * Storage backend server port
     */
    JANUSGRAPH_STORAGE_BACKEND_PORT("JANUSGRAPH_STORAGE_BACKEND_PORT"),

    /**
     * Type of storage backend supported by JanusGraph.
     * For example, can support <a href="https://docs.janusgraph.org/storage-backend/hbase/">values</a> equals to:
     * - cql: Apache Cassandra protocol supported for clients.
     * - hbase: Apache HBase
     * - inmemory: local memory map in JVM
     */
    JANUSGRAPH_STORAGE_BACKEND_TYPE("JANUSGRAPH_STORAGE_BACKEND_TYPE"),

    /**
     * List of unique or multiple hostname ip addresses (e.g 77.77.77.77, 192.168.1.10).
     */
    JANUSGRAPH_STORAGE_BACKEND_HOSTNAMES("JANUSGRAPH_STORAGE_BACKEND_HOSTNAMES"),

    /**
     * Storage backend access account username.
     */
    JANUSGRAPH_STORAGE_BACKEND_USERNAME("JANUSGRAPH_STORAGE_BACKEND_USERNAME"),

    /**
     * Storage backend access account password.
     */
    JANUSGRAPH_STORAGE_BACKEND_PASSWORD("JANUSGRAPH_STORAGE_BACKEND_PASSWORD"),

    /**
     * Keyspace of Cassandra storage backend.
     * The name of the keyspace to store the JanusGraph graph in. Allows multiple JanusGraph graphs to co-exist in the same Cassandra cluster.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_KEYSPACE("JANUSGRAPH_STORAGE_BACKEND_CQL_KEYSPACE"),

    JANUSGRAPH_STORAGE_BACKEND_CQL_LOCAL_DATACENTER("JANUSGRAPH_STORAGE_BACKEND_CQL_LOCAL_DATACENTER"),

    /**
     * Duration in milliseconds to wait (e.g 30000 for 30 seconds to wait) after each table creation (e.g since Amazon Keyspace creates table asynchronously).
     * This option shall be removed of configuration after the creation of the graph.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_INIT_WAIT_TIME("JANUSGRAPH_STORAGE_BACKEND_CQL_INIT_WAIT_TIME"),

    /**
     * True or False regarding SSL activation for communication with storage backend.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_ENABLED("JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_ENABLED"),

    /**
     * Location of the trust store.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_TRUSTSTORE_LOCATION("JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_TRUSTSTORE_LOCATION"),

    /**
     * Password of the trust store.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_TRUSTSTORE_PASSWORD("JANUSGRAPH_STORAGE_BACKEND_CQL_SSL_TRUSTSTORE_PASSWORD"),

    /**
     * True or False. Recommended to turn on to prohibit all full-scan attempts.
     * For example, Amazon keyspace diverges from Apache Cassandra and might result in incomplete results when a full-scan is executed.
     */
    JANUSGRAPH_STORAGE_BACKEND_QUERY_FORCE_INDEX("JANUSGRAPH_STORAGE_BACKEND_QUERY_FORCE_INDEX"),

    /**
     * True or False for activate consistency check execution on local side (e.g client side).
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_ONLY_USE_LOCAL_CONSISTENCY_FOR_SYSTEM_OPERATIONS("JANUSGRAPH_STORAGE_BACKEND_CQL_ONLY_USE_LOCAL_CONSISTENCY_FOR_SYSTEM_OPERATIONS"),

    /**
     * Cassandra consistency level for read operations.
     * Type of level regarding the read consistency to apply (e.g Amazon keyspaces only supports LOCAL_QUORUM consistency).
     * Example of supported values: LOCAL_QUORUM
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_READ_CONSISTENCY_LEVEL("JANUSGRAPH_STORAGE_BACKEND_CQL_READ_CONSISTENCY_LEVEL"),

    /**
     * Cassandra consistency level for write operations.
     * Type of level regarding the write consistency to apply (e.g Amazon keyspaces only supports LOCAL_QUORUM consistency).
     * Example of supported values: LOCAL_QUORUM
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_WRITE_CONSISTENCY_LEVEL("JANUSGRAPH_STORAGE_BACKEND_CQL_WRITE_CONSISTENCY_LEVEL"),

    /**
     * True of False about metadata to disable or to enable on graph schema.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_METADATA_SCHEMA_ENABLED("JANUSGRAPH_STORAGE_BACKEND_CQL_METADATA_SCHEMA_ENABLED"),

    /**
     * True of False to enable or disable metadata on tokens.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_METADATA_TOKEN_MAP_ENABLED("JANUSGRAPH_STORAGE_BACKEND_CQL_METADATA_TOKEN_MAP_ENABLED"),

    /**
     * Valid partitioner-names are: Murmur3Partitioner, RandomPartitioner, and DefaultPartitioner.
     */
    JANUSGRAPH_STORAGE_BACKEND_CQL_PARTITIONER_NAME("JANUSGRAPH_STORAGE_BACKEND_CQL_PARTITIONER_NAME"),

    /**
     * True or False.
     */
    JANUSGRAPH_LOG_KEY_CONSISTENCY("JANUSGRAPH_LOG_KEY_CONSISTENCY"),

    /**
     * True of False.
     */
    JANUSGRAPH_LOG_TX_KEY_CONSISTENCY("JANUSGRAPH_LOG_TX_KEY_CONSISTENCY"),

    /**
     * Type of indexing engine used with the storage backend.
     * Supported values are: elasticsearch, solr, lucene
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND("JANUSGRAPH_INDEX_SEARCH_BACKEND"),

    /**
     * List of unique or multiple hostnames (ip addresses).
     * For example: 100.100.101.1, 100.100.101.2
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND_HOSTNAMES("JANUSGRAPH_INDEX_SEARCH_BACKEND_HOSTNAMES"),

    /**
     * True or False.
     * Configuration in client mode only of integration with Elasticsearch.
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND_ES_CLIENT_ONLY("JANUSGRAPH_INDEX_SEARCH_BACKEND_ES_CLIENT_ONLY"),

    /**
     * Solr mode for connection to Solr instance.
     * See <a href="https://docs.janusgraph.org/index-backend/solr/">Solr configuration documentation</a>.
     * Supported values are: http, cloud
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND_SOLR_MODE("JANUSGRAPH_INDEX_SEARCH_BACKEND_SOLR_MODE")
    ;

    /**
     * Name of this environment variable currently hosted by the system environment.
     */
    private final String name;

    /**
     * Default constructor of a configuration variable that is readable from the
     * system environment variables set.
     *
     * @param aName Mandatory name of the environment variable that is readable from
     *              the current system environment (e.g defined by the runtime
     *              container or operating system).
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    ConfigurationVariable(String aName) throws IllegalArgumentException {
        if (aName == null || "".equalsIgnoreCase(aName))
            throw new IllegalArgumentException("The name of this variable shall be defined!");
        this.name = aName;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
