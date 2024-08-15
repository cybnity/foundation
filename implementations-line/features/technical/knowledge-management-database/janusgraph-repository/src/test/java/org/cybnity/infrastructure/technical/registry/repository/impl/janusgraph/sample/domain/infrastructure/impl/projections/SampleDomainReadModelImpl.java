package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractReadModelProjectionsSet;

/**
 * Example of projections collection relative to a domain perimeter (e.g Access Control) or to a specific domain object type (e.g a domain aggregate).
 */
public class SampleDomainReadModelImpl extends AbstractReadModelProjectionsSet {

    /**
     * Default constructor.
     *
     * @param ctx                     Mandatory context.
     * @param rootGraph               Mandatory origin graph that is manipulated by this read-model projections.
     * @param readModelOwnership      Mandatory owner of the perimeter of this read-model in terms of data-view responsibility scope.
     * @param readModelChangeObserver Optional observer of changes occurred onto the real-model projection (e.g at end of data view transaction execution).
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     */
    public SampleDomainReadModelImpl(IContext ctx, AbstractDomainGraphImpl rootGraph, IDomainModel readModelOwnership, ITransactionStateObserver readModelChangeObserver) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx);
        // Initialize read model scope
        initDataViewProjections(rootGraph, readModelOwnership, readModelChangeObserver);
    }

    /**
     * Initialize the perimeter of data views managed by this implementation model via projections set.
     *
     * @param rootGraph           Mandatory origin graph.
     * @param ownership           Mandatory owner of the data-view projections perimeter.
     * @param observer            Optional observer of the transaction state evolution (e.g to be notified about progress or end of performed transaction).
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When problem during an attempt of prepared projection activation.
     */
    private void initDataViewProjections(AbstractDomainGraphImpl rootGraph, IDomainModel ownership, ITransactionStateObserver observer) throws IllegalArgumentException, UnoperationalStateException {
        // Prepare the set of managed projections defining this read model perimeter that is maintained onto the root graph for STATE UPDATE TRANSACTIONS
        this.addProjection(new SampleDataViewStateTransactionImpl(ownership, rootGraph, observer));

        // ... other projections relative to other objects type and/or relations managed by this read-model perimeter
    }

}
