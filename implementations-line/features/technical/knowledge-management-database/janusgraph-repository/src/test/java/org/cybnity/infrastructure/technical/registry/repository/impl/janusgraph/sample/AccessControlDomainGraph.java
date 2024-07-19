package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.ReadModelConfigurationVariable;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.janusgraph.core.schema.JanusGraphManagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Example of graph covering the Access Control domain and scope of Vertex types managed in this area (e.g Tenants, in relation with other subdomains).
 */
public class AccessControlDomainGraph extends AbstractDomainGraphImpl {
    /**
     * Example of graph label.
     */
    private static final String GRAPH_NAME = "Access Control domain";

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public AccessControlDomainGraph(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx);
    }

    @Override
    protected void createProperties(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
    }

    @Override
    protected void createVertexLabels(JanusGraphManagement management) throws IllegalArgumentException {
        if (management == null) throw new IllegalArgumentException("management parameter is require!");
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
        config.put(ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND, context().get(ReadModelConfigurationVariable.JANUSGRAPH_STORAGE_BACKEND));
        return config;
    }
}
