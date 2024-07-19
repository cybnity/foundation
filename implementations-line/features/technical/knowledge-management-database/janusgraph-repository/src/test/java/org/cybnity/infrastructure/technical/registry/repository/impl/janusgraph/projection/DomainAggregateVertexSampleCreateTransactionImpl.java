package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;

import java.util.concurrent.CompletableFuture;

/**
 * Example of domain aggregate view (domain object data view projected) creation transaction into a graph.
 */
public class DomainAggregateVertexSampleCreateTransactionImpl extends AbstractGraphDataViewTransactionImpl {

    /**
     * Default constructor regarding a graph read model projection.
     *
     * @param label     Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @param dataModel Mandatory database model that can be manipulated by this transaction about its data view(s).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public DomainAggregateVertexSampleCreateTransactionImpl(String label, IDomainModel ownership, AbstractDomainGraphImpl dataModel) throws IllegalArgumentException {
        super(label, ownership, dataModel);
    }

    @Override
    public void when(CompletableFuture<Command> directive) {
        if (directive !=null) {

            // TODO implement logical query or change directives switch/execution
        }
    }
}
