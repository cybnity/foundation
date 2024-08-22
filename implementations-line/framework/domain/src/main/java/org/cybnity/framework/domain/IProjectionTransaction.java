package org.cybnity.framework.domain;

import org.cybnity.framework.domain.event.IEventType;

import java.util.Set;

/**
 * Transaction relative to a projection that can be executed to apply change on data-view projection (e.g according to the supported query language of a graph or database technology).
 */
public interface IProjectionTransaction {

    /**
     * Type of event type monitored and that is source of change execution over this transaction on read-model projection(s).
     *
     * @return Types of interest source, or null.
     */
    public Set<IEventType> observerOf();

    /**
     * Notify event that can be analyzed by this transaction to decide changes to perform on projection.
     *
     * @param event Source of interest.
     */
    public void when(DomainEvent event);
}
