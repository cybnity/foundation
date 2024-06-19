package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent a graph of domain elements.
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
     * Traversal version of this graph.
     */
    protected GraphTraversalSource g;

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
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public AbstractDomainGraphImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        logger = LoggerFactory.getLogger(this.getClass());
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
     * Closes the graph instance.
     */
    public final void closeGraph() throws UnoperationalStateException {
        logger().info("closing graph ");
        try {
            if (g != null) {
                try {
                    g.close();
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
            g = null;
            graph = null;
        }
    }

    /**
     * Drops the graph instance. The default implementation does nothing.
     */
    abstract public void dropGraph() throws UnoperationalStateException;

    /**
     * Creates the graph schema. The default implementation does nothing.
     */
    abstract public void createSchema();

    /**
     * Returns the geographical coordinates as a float array.
     */
    protected float[] getGeoFloatArray(final float lat, final float lon) {
        return new float[]{lat, lon};
    }
}
