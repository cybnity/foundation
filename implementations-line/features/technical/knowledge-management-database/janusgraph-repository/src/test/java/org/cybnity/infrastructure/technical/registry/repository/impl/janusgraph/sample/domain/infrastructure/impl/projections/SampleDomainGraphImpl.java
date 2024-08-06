package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.ContextualizedJanusGraphActiveTestContainer;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;
import org.janusgraph.core.schema.JanusGraphManagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Example of graph covering the Access Control domain and scope of Vertex types managed in this area (e.g Tenants, in relation with other subdomains).
 */
public class SampleDomainGraphImpl extends AbstractDomainGraphImpl {

    /**
     * Example of graph label.
     */
    private static final String GRAPH_NAME = "Security subdomain";

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
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
    }

    @Override
    protected void createVertexLabels(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
        // Define specification of each data-view (e.g vertex) manipulable into this graph (e.g relative to domain perimeter)
        management.makeVertexLabel(SampleDataView.class.getSimpleName()).make(); // Domain object data-view (type of vertex)
    }

    @Override
    protected void createEdgeLabels(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
    }

    @Override
    protected void createCompositeIndexes(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
    }

    @Override
    protected void createMixedIndexes(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
    }

    @Override
    public String graphName() {
        return GRAPH_NAME;
    }

    @Override
    protected Map<ReadModelConfigurationVariable, String> storageBackendConfiguration() {
        Map<ReadModelConfigurationVariable, String> config = new HashMap<>();
        // Add only mandatory backend type (normally based on simulated environment variable equals to "inmemory" because defined by the test container)
        config.put(ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND, ContextualizedJanusGraphActiveTestContainer.STORAGE_BACKEND_TYPE);
        return config;
    }
}
