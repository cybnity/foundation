package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.RelationType;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a graph of domain elements (e.g structure of data views relative to a domain scope) and implementation capabilities regarding schema manipulation, graph data update, and graph queries via the selected query framework (e.g Tinkerpop).
 */
public abstract class AbstractDomainGraphImpl {

    /**
     * Repository logger.
     */
    private final Logger logger;

    /**
     * Source of this graph.
     */
    protected Graph graph;

    /**
     * Is transactions are supported by this graph.
     */
    protected boolean supportsTransactions;

    /**
     * Is schema supported by this graph.
     */
    protected boolean supportsSchema;

    /**
     * Is geo data supported by this graph.
     */
    protected boolean supportsGeoshape;

    /**
     * Context of this domain graph.
     */
    private final IContext context;

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public AbstractDomainGraphImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        this.context = ctx;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public boolean isSupportsTransactions() {
        return supportsTransactions;
    }

    public boolean isSupportsSchema() {
        return supportsSchema;
    }

    public boolean isSupportsGeoshape() {
        return supportsGeoshape;
    }

    /**
     * Get the original context defined during the instantiation of this domain graph.
     *
     * @return A context.
     */
    protected IContext context() {
        return this.context;
    }

    /**
     * Get the logger instance regarding this repository.
     *
     * @return A logger instance.
     */
    protected Logger logger() {
        return this.logger;
    }

    /**
     * Opens the graph instance.
     * If the graph instance does not exist, a new graph instance is initialized.
     * If this graph previously already opened with success (and close() method not called), return previously same opened traversal source.
     *
     * @return Graph traversal source instance.
     * @throws ConfigurationException When problem is occurred regarding the graph configuration elements to use for instance creation.
     */
    public GraphTraversalSource open() throws ConfigurationException {
        if (graph != null) return graph.traversal(); // Return already opened graph instance

        logger().info("Opening graph (" + graphName() + ")");
        // Read the storage backend configuration settings
        Map<ReadModelConfigurationVariable, String> storageBackendConfigVariables = graphConfiguration();
        JanusGraphFactory.Builder factory = JanusGraphFactory.build();
        try {
            // Add configuration settings
            String refConfigName, configValue;
            for (Map.Entry<ReadModelConfigurationVariable, String> property : storageBackendConfigVariables.entrySet()) {
                // Read current
                refConfigName = property.getKey().getReferenceConfigurationName();
                configValue = property.getValue();
                if (configValue != null && !configValue.isEmpty()) {
                    // Set configuration on factory
                    factory.set(refConfigName, configValue);
                }
            }
        } catch (Exception e) {
            // Invalid configuration set defined by child class
            throw new ConfigurationException(e);
        }

        // Open the graph database instance
        graph = factory.open();
        return graph.traversal();
    }

    /**
     * Closes the graph instance.
     *
     * @throws UnoperationalStateException Problem occurred during the attempt to close the graph connection.
     */
    public final void close() throws UnoperationalStateException {
        logger().info("Closing graph (" + graphName() + ")");
        try {
            if (graph != null) {
                try {
                    graph.traversal().close();
                } catch (Exception e) {
                    throw new UnoperationalStateException(e);
                }
            }
            if (graph != null) {
                try {
                    graph.close();
                } catch (Exception e) {
                    throw new UnoperationalStateException(e);
                }
            }
        } finally {
            graph = null;
        }
    }

    /**
     * Get the graph instance.
     *
     * @return JanusGraph instance.
     */
    protected JanusGraph janusGraph() {
        return (JanusGraph) graph;
    }

    /**
     * Get the graph instance.
     *
     * @return JanusGraph instance.
     */
    public Graph graph() {
        return graph;
    }

    /**
     * Drops the graph instance.
     *
     * @throws UnoperationalStateException Problem occurred during the attempt to close the graph deletion.
     */
    public void drop() throws UnoperationalStateException {
        JanusGraph toDrop = janusGraph();
        if (toDrop != null) {
            try {
                JanusGraphFactory.drop(toDrop);
            } catch (Exception e) {
                throw new UnoperationalStateException(e);
            }
        }
    }

    /**
     * Creates explicitly defined schema of this graph.
     * See <a href="https://docs.janusgraph.org/v0.4/basics/schema/">JanusGraph Schema and data modeling documentation</a> for help.
     * <br>
     * This method is managing the call to each schema modification method in a global transaction like:
     * - createProperties(management)
     * - createVertexLabels(management)
     * - createEdgeLabels(management)
     * - createCompositeIndexes(management)
     * - createMixedIndexes(management)
     *
     * @throws UnoperationalStateException Problem occurred during the attempt to modify the graph.
     */
    public final void createSchema() throws UnoperationalStateException {
        JanusGraph db = janusGraph();
        if (db != null) {
            final JanusGraphManagement management = db.openManagement();
            try {
                // naive check if the schema was previously created
                if (management.getRelationTypes(RelationType.class).iterator().hasNext()) {
                    management.rollback();
                    return;
                }
                logger().info("Creating schema of graph (" + graphName() + ")");
                createProperties(management);
                createVertexLabels(management);
                createEdgeLabels(management);
                createCompositeIndexes(management);
                createMixedIndexes(management);
                management.commit();
            } catch (Exception e) {
                management.rollback();
                throw new UnoperationalStateException(e);
            }
        }
    }

    /**
     * Creates the properties for vertices, edges, and meta-properties.
     *
     * @param management Mandatory means to manage the schema modification.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    abstract protected void createProperties(final JanusGraphManagement management) throws IllegalArgumentException;

    /**
     * Creates the vertex labels.
     *
     * @param management Mandatory means to manage the schema modification.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    abstract protected void createVertexLabels(final JanusGraphManagement management) throws IllegalArgumentException;

    /**
     * Creates the edge labels.
     *
     * @param management Mandatory means to manage the schema modification.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    abstract protected void createEdgeLabels(final JanusGraphManagement management) throws IllegalArgumentException;

    /**
     * Creates the composite indexes. A composite index is best used for exact match lookups.
     *
     * @param management Mandatory means to manage the schema modification.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    abstract protected void createCompositeIndexes(final JanusGraphManagement management) throws IllegalArgumentException;

    /**
     * Creates the mixed indexes. A mixed index requires that an external
     * indexing backend is configured on the graph instance. A mixed index
     * is best for full text search, numerical range, and geospatial queries.
     *
     * @param management Mandatory means to manage the schema modification.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    abstract protected void createMixedIndexes(final JanusGraphManagement management) throws IllegalArgumentException;

    /**
     * Get the logical name regarding this graph model.
     *
     * @return A label.
     */
    abstract public String graphName();

    /**
     * Get the map configuration of JanusGraph settings values.
     * For example, the map can be generated from current static context, properties file, or operating system's current environment variables dynamically read.
     * This default implementation is reading org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable variables from current environment's variables to build and return the found values.
     * This default implementation can be redefined by subclass which could require the collect of graph configuration values from another type fo resource provider (e.g configuration server, external files...).
     *
     * @return A map of configuration values relative to this graph, or empty set when none configuration value have been found as defined via environment's variables.
     */
    protected Map<ReadModelConfigurationVariable, String> graphConfiguration() {
        Map<ReadModelConfigurationVariable, String> config = new HashMap<>();

        // --- READ FROM CONTEXT EACH DEFINED SETTING AS CONFIGURATION FOR GRAPH MANAGEMENT/USING ---
        EnumSet<ReadModelConfigurationVariable> graphConfigurationProperties = EnumSet.allOf(ReadModelConfigurationVariable.class);
        IContext ctx = this.context();

        for (ReadModelConfigurationVariable prop : graphConfigurationProperties) {
            // Search in context if a graph configuration value is defined in environment as usable setting
            String propValue = ctx.get(prop); // Not null when defined by environment
            if (propValue != null && !propValue.isEmpty()) {
                // Add found configuration value into storage backend configuration to return
                config.put(prop, propValue);
            }
        }
        return config;
    }

    /**
     * Find geographical coordinates version
     *
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @return the geographical coordinates as a float array.
     */
    protected float[] getGeoFloatArray(final float lat, final float lon) {
        return new float[]{lat, lon};
    }

    /**
     * Stop allocated resources specific to this domain graph.
     */
    public void freeResources() {
    }

}
