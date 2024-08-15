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
 * <p>
 * The current supported config attributes of this enum set is aligned with the v1.0 Latest version of JanusGraph Configuration Reference.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum ReadModelConfigurationVariable implements IReadableConfiguration {

    /**
     * Type of indexing system used with the persistence engine.
     * Example of supported value: elasticsearch
     */
    JANUSGRAPH_INDEX_SEARCH_BACKEND("JANUSGRAPH_INDEX_SEARCH_BACKEND", "index.search.backend"),

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
    JANUSGRAPH_INDEX_SEARCH_SOLR_MODE("JANUSGRAPH_INDEX_SEARCH_SOLR_MODE", "index.search.solr.mode"),

    /// CACHE ///
    /**
     * Whether to enable JanusGraph's database-level cache, which is shared across all transactions. Enabling this option speeds up traversals by holding hot graph elements in memory, but also increases the likelihood of reading stale data. Disabling it forces each transaction to independently fetch graph elements from storage before reading/writing them.
     * Default value is FALSE.
     */
    JANUSGRAPH_CACHE_DB_CACHE("JANUSGRAPH_CACHE_DB_CACHE", "cache.db-cache"),

    /**
     * How long, in milliseconds, database-level cache will keep entries after flushing them. This option is only useful on distributed storage backends that are capable of acknowledging writes without necessarily making them immediately visible.
     * Default value is 50.
     */
    JANUSGRAPH_CACHE_DB_CACHE_CLEAN_WAIT("JANUSGRAPH_CACHE_DB_CACHE_CLEAN_WAIT", "cache.db-cache-clean-wait"),

    /**
     * Size of JanusGraph's database level cache. Values between 0 and 1 are interpreted as a percentage of VM heap, while larger values are interpreted as an absolute size in bytes.
     * Default value is 0.3
     */
    JANUSGRAPH_CACHE_DB_CACHE_SIZE("JANUSGRAPH_CACHE_DB_CACHE_SIZE", "cache.db-cache-size"),

    /**
     * Default expiration time, in milliseconds, for entries in the database-level cache. Entries are evicted when they reach this age even if the cache has room to spare. Set to 0 to disable expiration (cache entries live forever or until memory pressure triggers eviction when set to 0).
     * Default value is 10000.
     */
    JANUSGRAPH_CACHE_DB_CACHE_TIME("JANUSGRAPH_CACHE_DB_CACHE_TIME", "cache.db-cache-time"),

    /**
     * Maximum size of the transaction-level cache of recently-used vertices.
     * Default value is 20000.
     */
    JANUSGRAPH_CACHE_TX_CACHE_SIZE("JANUSGRAPH_CACHE_TX_CACHE_SIZE", "cache.tx-cache-size"),

    /**
     * Initial size of the transaction-level cache of uncommitted dirty vertices. This is a performance hint for write-heavy, performance-sensitive transactional workloads. If set, it should roughly match the median vertices modified per transaction.
     * No default value.
     */
    JANUSGRAPH_CACHE_TX_DIRTY_SIZE("JANUSGRAPH_CACHE_TX_DIRTY_SIZE", "cache.tx-dirty-size"),

    /// CLUSTER ///
    /**
     * The number of virtual partition blocks created in the partitioned graph. This should be larger than the maximum expected number of nodes in the JanusGraph graph cluster. Must be greater than 1 and a power of 2.
     * Default value equals to 32.
     */
    JANUSGRAPH_CLUSTER_MAX_PARTITIONS("JANUSGRAPH_CLUSTER_MAX_PARTITIONS", "cluster.max-partitions"),

    // COMPUTER ///
    /**
     * How the graph computer should return the computed results. 'persist' for writing them into the graph, 'localtx' for writing them into the local transaction, or 'none' (default)
     */
    JANUSGRAPH_COMPUTER_RESULT_MODE("JANUSGRAPH_COMPUTER_RESULT_MODE", "computer.result-mode"),

    /// GRAPH ///
    /**
     * Whether non long-type vertex ids are allowed. set-vertex-id must be enabled in order to use this functionality. Currently, only string-type is supported. This does not prevent users from using custom ids with long type. If your storage backend does not support unordered scan, then some scan operations will be disabled. You cannot use this feature with Berkeley DB. EXPERT FEATURE - USE WITH GREAT CARE.
     * Default value if FALSE.
     */
    JANUSGRAPH_GRAPH_ALLOW_CUSTOM_VID_TYPES("JANUSGRAPH_GRAPH_ALLOW_CUSTOM_VID_TYPES", "graph.allow-custom-vid-types"),

    /**
     * Whether to allow the local and storage-backend-hosted copies of the configuration to contain conflicting values for options with any of the following types: FIXED, GLOBAL_OFFLINE, GLOBAL. These types are managed globally through the storage backend and cannot be overridden by changing the local configuration. This type of conflict usually indicates misconfiguration. When this option is true, JanusGraph will log these option conflicts, but continue normal operation using the storage-backend-hosted value for each conflicted option. When this option is false, JanusGraph will log these option conflicts, but then it will throw an exception, refusing to start.
     * Default value is TRUE.
     */
    JANUSGRAPH_GRAPH_ALLOW_STALE_CONFIG("JANUSGRAPH_GRAPH_ALLOW_STALE_CONFIG", "graph.allow-stale-config"),

    /**
     * Setting this to true will allow certain fixed values to be updated such as storage-version. This should only be used for upgrading.
     * Default value if FALSE.
     */
    JANUSGRAPH_GRAPH_ALLOW_UPGRADE("JANUSGRAPH_GRAPH_ALLOW_UPGRADE", "graph.allow-upgrade"),

    /**
     * Whether to use JanusGraph generated client-side timestamp in mutations if the backend supports it. When enabled, JanusGraph assigns one timestamp to all insertions and another slightly earlier timestamp to all deletions in the same batch. When this is disabled, mutation behavior depends on the backend. Some might use server-side timestamp (e.g. HBase) while others might use client-side timestamp generated by driver (CQL).
     * Default value is TRUE.
     */
    JANUSGRAPH_GRAPH_ASSIGN_TIMESTAMP("JANUSGRAPH_GRAPH_ASSIGN_TIMESTAMP", "graph.assign-timestamp"),

    /**
     * This config option is an optional configuration setting that you may supply when opening a graph. The String value you provide will be the name of your graph. If you use the ConfigurationManagement APIs, then you will be able to access your graph by this String representation using the ConfiguredGraphFactory APIs.
     * None default value.
     */
    JANUSGRAPH_GRAPH_GRAPHNAME("JANUSGRAPH_GRAPH_GRAPHNAME", "graph.graphname"),

    /**
     * If a JanusGraph instance with the same instance identifier already exists, the usage of this configuration option results in the opening of this graph anyway.
     * Default value is FALSE.
     */
    JANUSGRAPH_GRAPH_REPLACE_INSTANCE_IF_EXISTS("JANUSGRAPH_GRAPH_REPLACE_INSTANCE_IF_EXISTS", "graph.replace-instance-if-exists"),

    /**
     * Whether user provided vertex ids should be enabled and JanusGraph's automatic vertex id allocation be disabled. Useful when operating JanusGraph in concert with another storage system that assigns long ids but disables some of JanusGraph's advanced features which can lead to inconsistent data. For example, users must ensure the vertex ids are unique to avoid duplication. Must use graph.getIDManager().toVertexId(long) to convert your id first. Once this is enabled, you have to provide vertex id when creating new vertices. EXPERT FEATURE - USE WITH GREAT CARE.
     * Default value is FALSE.
     */
    JANUSGRAPH_GRAPH_SET_VERTEX_ID("JANUSGRAPH_GRAPH_SET_VERTEX_ID", "graph.set-vertex-id"),

    /**
     * The version of JanusGraph storage schema with which this database was created. Automatically set on first start of graph. Should only ever be changed if upgrading to a new major release version of JanusGraph that contains schema changes.
     * None default value.
     */
    JANUSGRAPH_GRAPH_STORAGE_VERSION("JANUSGRAPH_GRAPH_STORAGE_VERSION", "graph.storage-version"),

    /**
     * The timestamp resolution to use when writing to storage and indices. Sets the time granularity for the entire graph cluster. To avoid potential inaccuracies, the configured time resolution should match those of the backend systems. Some JanusGraph storage backends declare a preferred timestamp resolution that reflects design constraints in the underlying service. When the backend provides a preferred default, and when this setting is not explicitly declared in the config file, the backend default is used and the general default associated with this setting is ignored. An explicit declaration of this setting overrides both the general and backend-specific defaults.
     * Default value is MICRO.
     */
    JANUSGRAPH_GRAPH_TIMESTAMPS("JANUSGRAPH_GRAPH_TIMESTAMPS", "graph.timestamps"),

    /**
     * Unique identifier for this JanusGraph instance. This must be unique among all instances concurrently accessing the same stores or indexes. It's automatically generated by concatenating the hostname, process id, and a static (process-wide) counter. Leaving it unset is recommended.
     * None default value.
     */
    JANUSGRAPH_GRAPH_UNIQUE_INSTANCE_ID("JANUSGRAPH_GRAPH_UNIQUE_INSTANCE_ID", "graph.unique-instance-id"),

    /**
     * When this is set and unique-instance-id is not, this JanusGraph instance's unique identifier is generated by concatenating the hex encoded hostname to the provided number.
     * None default value.
     */
    JANUSGRAPH_GRAPH_UNIQUE_INSTANCE_ID_SUFFIX("JANUSGRAPH_GRAPH_UNIQUE_INSTANCE_ID_SUFFIX", "graph.unique-instance-id-suffix"),
    /**
     * When this is set, this JanusGraph's unique instance identifier is set to the hostname. If unique-instance-id-suffix is also set, then the identifier is set to.
     * Default value is FALSE.
     */
    JANUSGRAPH_GRAPH_USE_HOSTNAME_FOR_UNIQUE_INSTANCE_ID("JANUSGRAPH_GRAPH_USE_HOSTNAME_FOR_UNIQUE_INSTANCE_ID", "graph.use-hostname-for-unique-instance-id"),

    /// GRAPH SCRIPT EVAL ///
    /**
     * Whether to enable Gremlin script evaluation. If it is enabled, a gremlin script engine will be instantiated together with the JanusGraph instance, with which one can use eval method to evaluate a gremlin script in plain string format. This is usually only useful when JanusGraph is used as an embedded Java library.
     * Default value is FALSE.
     */
    JANUSGRAPH_GRAPH_SCRIPT_EVAL_ENABLED("JANUSGRAPH_GRAPH_SCRIPT_EVAL_ENABLED", "graph.script-eval.enabled"),
    /**
     * Full class name of script engine that implements GremlinScriptEngine interface. Following shorthands can be used:
     * - GremlinLangScriptEngine (A script engine that only accepts standard gremlin queries. Anything else including lambda function is not accepted. We recommend using this because it's generally safer, but it is not guaranteed that it has no security problem.)- GremlinGroovyScriptEngine (A script engine that accepts arbitrary groovy code. This can be dangerous and you should use it at your own risk. See https://tinkerpop.apache.org/docs/current/reference/#script-execution for potential security problems.)
     * Default value is GremlinLangScriptEngine
     */
    JANUSGRAPH_GRAPH_SCRIPT_EVAL_ENGINE("JANUSGRAPH_GRAPH_SCRIPT_EVAL_ENGINE", "graph.script-eval.engine"),

    /// GREMLIN ///
    /**
     * The implementation of graph factory that will be used by gremlin server.
     * Default value is org.janusgraph.core.JanusGraphFactory
     */
    JANUSGRAPH_GREMLIN_GRAPH("JANUSGRAPH_GREMLIN_GRAPH", "gremlin.graph"),

    /// METRICS ///
    /**
     * Whether to enable basic timing and operation count monitoring on backend.
     * Default value is FALSE.
     */
    JANUSGRAPH_METRICS_ENABLED("JANUSGRAPH_METRICS_ENABLED", "metrics.enabled"),
    /**
     * Whether to aggregate measurements for the edge store, vertex index, edge index, and ID store.
     * Default value is TRUE.
     */
    JANUSGRAPH_METRICS_MERGE_STORES("JANUSGRAPH_METRICS_MERGE_STORES", "metrics.merge-stores"),
    /**
     * The default name prefix for Metrics reported by JanusGraph.
     * Default value is org.janusgraph
     */
    JANUSGRAPH_METRICS_PREFIX("JANUSGRAPH_METRICS_PREFIX", "metrics.prefix"),

    /// STORAGE ///
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
    JANUSGRAPH_STORAGE_HOSTNAMES("JANUSGRAPH_STORAGE_HOSTNAMES", "storage.hostname");

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
