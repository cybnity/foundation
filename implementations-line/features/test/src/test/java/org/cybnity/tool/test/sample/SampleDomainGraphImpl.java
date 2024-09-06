package org.cybnity.tool.test.sample;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.janusgraph.core.schema.JanusGraphManagement;

/**
 * Example of graph covering the Sample domain and scope of Vertex types managed in this area (e.g objects in relation with other subdomains).
 */
public class SampleDomainGraphImpl extends AbstractDomainGraphImpl {

    /**
     * Example of graph label.
     */
    private static final String GRAPH_NAME = "Sample subdomain";

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public SampleDomainGraphImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx);
        // Configure supported capabilities
        this.supportsSchema = true;
        this.supportsTransactions = true;
        this.supportsGeoshape = true;
    }

    @Override
    protected void createProperties(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is required!");
    }

    @Override
    protected void createVertexLabels(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is required!");
        // Define specification of each data-view (e.g vertex) manipulable into this graph (e.g relative to domain perimeter)
        management.makeVertexLabel(SampleDataView.class.getSimpleName()).make(); // Domain object data-view (type of vertex)
    }

    @Override
    protected void createEdgeLabels(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is required!");
    }

    @Override
    protected void createCompositeIndexes(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is required!");
    }

    @Override
    protected void createMixedIndexes(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is required!");
    }

    @Override
    public String graphName() {
        return GRAPH_NAME;
    }

}
