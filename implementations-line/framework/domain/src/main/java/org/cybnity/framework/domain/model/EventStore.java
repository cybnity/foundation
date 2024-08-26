package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.ISubscribable;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Persistence system of implementing Event Sourcing pattern that return the stream of events associated
 * which an aggregate instance allowing to reply the events to recreate the
 * state of the aggregate.
 * <p>
 * Maintain a store of all domain events for a single bounded context (e.g
 * discrete storing of event for every model command behavior that is ever
 * executed). It's like a queue for publishing all domain events through a
 * messaging infrastructure allowing integration between bounded contexts, where
 * remote subscribers react to the events in terms of their own contextual
 * needs.
 * <p>
 * A store can also examine a historical record of the result of every command
 * that has ever been executed on the model (e.g helpful tracking system as
 * audit logs for debugging).
 * <p>
 * The event store is used to reconstitute each Aggregate instance when it is
 * retrieved from its repository, implementing the concept of Event Sourcing. To
 * do that, the store apply to an Aggregate instance all previously stored
 * Events in chronological order. Production of snapshots of any number of store
 * events (e.g group of 100) can optimize instance reconstitution.
 * <p>
 * The store is able to handle multiple versions of an event type and
 * aggregates.
 * <p>
 * When an event is stored (via append method), the store shall publish event
 * after it have been saved (e.g allowing to read model store to automatically
 * refresh dependent contents).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class EventStore implements ISubscribable, IEventStore {

    /**
     * Delegated capability regarding promotion of stored events changes.
     */
    private final DomainEventPublisher promotionManager;

    /**
     * Default constructor managing the store configuration during its
     * instantiation. To be defined by the child class implementing the initialization of storage
     * persistence system.
     * This constructor is ensuring the instantiation of the promotionManager component which is container of publishing of events.
     */
    protected EventStore() {
        // Initialize a delegate for promotion of events changes (e.g to read model's
        // repository)
        this.promotionManager = DomainEventPublisher.instance();
    }

    /**
     * Get the promotion manager managing the notification of registered subscribers regarding any change performed by the store.
     *
     * @return A publisher.
     */
    protected DomainEventPublisher subscribersManager() {
        return this.promotionManager;
    }

    @Override
    public <T> void subscribe(IDomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            // Add listener interested by stored events
            subscribersManager().subscribe(aSubscriber);
    }

    @Override
    public <T> void remove(IDomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            subscribersManager().remove(aSubscriber);
    }

}
