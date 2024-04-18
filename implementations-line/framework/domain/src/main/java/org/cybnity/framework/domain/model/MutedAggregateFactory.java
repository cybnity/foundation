package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Identifier;

import java.util.List;

/**
 * Factory of an aggregate that provide instance prepared specifically according to a type of aggregate.
 */
public interface MutedAggregateFactory {

    /**
     * Prepare and return a new instance of an aggregate.
     * This method ensure execution of a mutate process from the changes history onto the prepared instance of aggregate to return.
     *
     * @param instanceId     Mandatory identifier of the aggregate instance to create.
     * @param changesHistory Mandatory change events that shall be mutated during the instance preparation to rehydrate it.
     * @return A rehydrated instance of an aggregate.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When there is not a minimum one change event into the changes history parameter.
     *                                  When any change event of the history is not about the same aggregate identifier which is rehydrated.
     */
    public Aggregate instanceOf(Identifier instanceId, List<DomainEvent> changesHistory) throws IllegalArgumentException;
}
