package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.read;

import org.cybnity.framework.domain.AbstractDataViewVersionTransactionImpl;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IProjectionRead;
import org.cybnity.framework.domain.IQueryResponse;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event.SampleDomainQueryEventType;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Example of utility class implementing the Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a query directive.
 * Implementation about DomainEventType.SAMPLE_AGGREGATE_FIND_BY_LABEL domain query command type.
 */
public class FindSampleDataViewVersionByEqualsLabel extends AbstractDataViewVersionTransactionImpl implements IProjectionRead {

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
    public FindSampleDataViewVersionByEqualsLabel(AbstractGraphDataViewTransactionImpl notifiable, AbstractDomainGraphImpl graph) throws IllegalArgumentException {
        super(notifiable);
        if (graph == null) throw new IllegalArgumentException("Graph parameter is required!");
        this.graph = graph;
    }

    /**
     * Example of only read query event that is supported by this transaction, as a directive to operate on data-view model.
     *
     * @return A set including only DomainEventType.SAMPLE_AGGREGATE_FIND_BY_LABEL event type as source of interest.
     */
    @Override
    public Set<IEventType> observerOf() {
        return Set.of(SampleDomainQueryEventType.SAMPLE_AGGREGATE_FIND_BY_LABEL);
    }

    @Override
    public void when(Command command, CompletableFuture<IQueryResponse> completableFuture) throws IllegalArgumentException, UnsupportedOperationException {
        if (command != null) {
            // TODO implement the graph data view read

        }
    }

}
