package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;

/**
 * Utility class providing hydration feature on fact.
 */
public interface HydrationCapability {

    /**
     * Execute a mutation operation onto a subject according to a type of change event.
     * This method implementation shall have a "magic" approach that detect the type of change event, and delegate the change execution to a "When" handlers supported by the modified subject.
     *
     * @param change Mandatory change to apply on subject according to the change type (e.g attribute add, upgrade, delete operation).
     * @throws IllegalArgumentException When any mandatory parameter is not defined.
     */
    public void mutateWhen(DomainEvent change) throws IllegalArgumentException;

    /**
     * Bring this instance state up-to-date with the events that occurred since the latest snapshot.
     *
     * @param history Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    public void replayEvents(EventStream history);
}
