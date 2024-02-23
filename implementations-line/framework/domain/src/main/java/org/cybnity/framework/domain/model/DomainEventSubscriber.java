package org.cybnity.framework.domain.model;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a contract as interested to be notified when one or
 * several types of facts are changed.
 * 
 * For example; a persistence service can monitor the changes (e.g commitment
 * events) observed on aggregates and automatically store them into an event
 * stores as EventsPersistenceSubscriber.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class DomainEventSubscriber<T> {

    /**
     * Handle of notified event.
     * 
     * @param event Event about a topic which interest this subscriber.
     */
    public abstract void handleEvent(T event);

    /**
     * Get the type of event class that is observed by this subscriber.
     * 
     * @return A type of event (e.g DomainEvent.class).
     */
    public abstract Class<?> subscribeToEventType();

}
