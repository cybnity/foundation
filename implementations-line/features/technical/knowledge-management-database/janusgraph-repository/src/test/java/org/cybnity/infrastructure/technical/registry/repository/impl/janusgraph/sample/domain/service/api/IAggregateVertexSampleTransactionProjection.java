package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api;

import org.cybnity.framework.domain.IReadModelProjection;

/**
 * Example representing an optimized read-model projection allowing query and read of denormalized version of Sample transactions.
 * This interface contract is covering a perimeter of Sample read-model and provide SampleTransaction read capabilities.
 */
public interface IAggregateVertexSampleTransactionProjection extends IReadModelProjection  {
    /**
     * Search existing sample which have an equals name.
     *
     * @param label Mandatory logical name of tenant to search.
     * @param ctx   Mandatory context.
     * @return An identified tenant, or null.
     * @throws IllegalArgumentException When any mandatory parameter is not valid.
     */
    //public SampleDataView findByLabel(String label, ISessionContext ctx) throws IllegalArgumentException;
}
