package org.cybnity.framework.domain.application;

import java.security.InvalidParameterException;

import org.cybnity.framework.domain.Subscribable;
import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Persistence system of events that return the stream of events associated
 * which an aggregate instance allowing to reply the events to recreate the
 * state of the aggregate.
 * 
 * Maintain a store of all domain events for a single bounded context (e.g
 * discrete storing of event for every model command behavior that is ever
 * executed). It's like a queue for publishing all domain events through a
 * messaging infrastructure allowing integration between bounded contexts, where
 * remote subscribers react to the events in terms of their own contextual
 * needs.
 * 
 * A store can also examine a historical record of the result of every command
 * that has ever been executed on the model (e.g helpful tracking system as
 * audit logs for debugging).
 * 
 * The event store is used to reconstitue each Aggregate instance when it is
 * retrieved from its repository, implementing the concept of Event Sourcing. To
 * do that, the store apply to an Aggregate instance all previously stored
 * Events in chronological order. Production of snapshots of any number of store
 * events (e.g group of 100) can optimize instance reconstitution.
 * 
 * The store is ablbe to handle multiple versions of an event type and
 * aggregates.
 * 
 * When an event is stored (via append method), the store shall publishe event
 * after it have been saved (e.g allowing to read model store to automatically
 * refresh dependent contents).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class EventStore implements Subscribable {

    /**
     * Default constructor managing the store configuration during its
     * instantiation. To be defined by the child class implementing a storage
     * persistence system.
     */
    protected EventStore() {
    }

    /**
     * Add an event into the store.
     * 
     * @param event Mandatory event to store.
     * @throws InvalidParameterException When event to store is not compatible to be
     *                                   stored (e.g missing mandatory content into
     *                                   the event to store).
     * @throws ImmutabilityException     When problem of immutable version of stored
     *                                   event is occurred.
     */
    public abstract void append(DomainEvent event) throws InvalidParameterException, ImmutabilityException;

}
