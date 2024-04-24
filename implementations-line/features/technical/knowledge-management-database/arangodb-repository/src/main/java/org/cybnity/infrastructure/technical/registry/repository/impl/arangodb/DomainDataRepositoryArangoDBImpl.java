package org.cybnity.infrastructure.technical.registry.repository.impl.arangodb;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.infrastructure.IDomainRepository;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.infrastructure.technical.registry.adapter.api.KNRAdapter;
import org.cybnity.infrastructure.technical.registry.adapter.impl.arangodb.KNRAdapterArangoDBImpl;

/**
 * Domain data repository implementation class using ArangoDB database resources.
 */
public abstract class DomainDataRepositoryArangoDBImpl implements IDomainRepository<Aggregate> {
    /**
     * Adapter to ArangoDB registry system.
     */
    private final KNRAdapter adapter;

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public DomainDataRepositoryArangoDBImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        this.adapter = new KNRAdapterArangoDBImpl(ctx);
    }

    @Override
    public void freeResources() {
        // Freedom of resources allocated by the adapter
        this.adapter.freeUpResources();
    }

}
