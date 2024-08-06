package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.cybnity.framework.domain.AbstractDataViewVersionTransactionImpl;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IProjectionTransaction;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event.SampleDomainEventType;

import java.util.Set;

/**
 * Example of utility class implementing the Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a change directive.
 * Implementation about DomainEventType.SAMPLE_AGGREGATE_CHANGED domain event type.
 */
public class UpgradeSampleDataViewVersion extends AbstractDataViewVersionTransactionImpl implements IProjectionTransaction {

    /**
     * Manipulated graph model.
     */
    private final AbstractDomainGraphImpl graph;

    /**
     * Default constructor.
     *
     * @param notifiable Optional observer of transaction execution end which can be notified.
     * @param graph      Mandatory manipulable graph.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public UpgradeSampleDataViewVersion(AbstractGraphDataViewTransactionImpl notifiable, AbstractDomainGraphImpl graph) throws IllegalArgumentException {
        super(notifiable);
        if (graph == null) throw new IllegalArgumentException("Graph parameter is required!");
        this.graph = graph;
    }

    /**
     * Example of only creation change event that is supported by this transaction, as a directive to operate on data-view model.
     *
     * @return A set including only DomainEventType.SAMPLE_AGGREGATE_CHANGED event type as source of interest.
     */
    @Override
    public Set<IEventType> observerOf() {
        return Set.of(SampleDomainEventType.SAMPLE_AGGREGATE_CHANGED);
    }

    @Override
    public void when(DomainEvent event) {
        if (event != null) {
            // TODO implement the graph data view change and

            // Notify the changed graph status
            // TODO create a transaction end notification about execution finalized (e.g identified data-view changed, version...) with/without result
            DomainEvent dataViewChanged = null;
            notifyEvent(dataViewChanged);
        }
    }

}
