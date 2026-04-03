package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IReadModelProjection;

/**
 * Stereotype of implementation class defining a read-model projection with automatic managed lifecycle (e.g refreshed/upgraded according to data view source changes; queryable via explicit command).
 * Its ensure handling of any domain event change observed (e.g event store regarding a scope of domain aggregated that are source of projection managed by this data view perimeter), to decide if data view perimeter under its responsibility shall be refreshed/re-calculated as new read-model projection state
 */
public abstract class AbstractRealModelDataViewProjection implements IReadModelProjection, IDomainEventSubscriber<DomainEvent>, ITransactionStateObserver {

    /**
     * Description elements of this projection.
     */
    private ReadModelProjectionDescriptor description;

    /**
     * Eligible notifiable observer of this transaction execution.
     */
    private ITransactionStateObserver observer;

    /**
     * Default constructor regarding a standard read model projection.
     *
     * @param label            Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership        Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @param observer         Optional observer of the transaction state evolution (e.g to be notified about progress or end of performed transaction).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public AbstractRealModelDataViewProjection(String label, IDomainModel ownership, ITransactionStateObserver observer) throws IllegalArgumentException {
        // Create description of this projection based on parameters
        prepareDescription(label, ownership);
        // Reference the potential observer of this data view projection changes
        this.observer = observer;
    }

    /**
     * Prepare expected description of this read-model projection.
     * Called during the instance creation.
     *
     * @param label            Mandatory logical label regarding this projection.
     * @param ownership        Mandatory domain which is owner of this projection.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    protected void prepareDescription(String label, IDomainModel ownership) throws IllegalArgumentException {
        // Create description instance of this projection
        this.description = ReadModelProjectionDescriptor.instanceOf(label, ownership);
    }

    /**
     * Get observer of this transaction.
     *
     * @return A subscriber or null.
     */
    protected ITransactionStateObserver getObserver() {
        return this.observer;
    }

    /**
     * Define an observer of this transaction execution steps.
     *
     * @param subscriber Eligible observer or null.
     */
    protected void setObserver(ITransactionStateObserver subscriber) {
        this.observer = subscriber;
    }

    @Override
    public ReadModelProjectionDescriptor description() {
        return this.description;
    }
}
