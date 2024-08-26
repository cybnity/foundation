package org.cybnity.framework.domain.model;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a contract as interested to be notified when one or
 * several types of facts are changed.
 * <br>
 * For example; a persistence service can monitor the changes (e.g commitment
 * events) observed on aggregates and automatically store them into an event
 * stores as EventsPersistenceSubscriber.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IDomainEventSubscriber<T> {

    /**
     * Handle of notified event.
     *
     * @param event Event about a topic which interest this subscriber.
     */
    public void handleEvent(T event);

    /**
     * Get the type of event class that is observed by this subscriber.
     *
     * @return A type of event (e.g DomainEvent.class) or null when multiple event types are sources of interest.
     */
    public Class<?> subscribeToEventType();

}
