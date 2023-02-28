package org.cybnity.framework.domain.model;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a interest contract regarding a component which is interested to be
 * notified when a type of event is changed and notified.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class DomainEventSubscriber<T> {

    /**
     * Handle of notified event.
     * 
     * @param event Event about a topic which interest this subcriber.
     */
    public abstract void handleEvent(T event);

    /**
     * Get the type of event class that is interested by this subscriber.
     * 
     * @return A type of event (e.g DomainEvent.class).
     */
    public abstract Class<?> subscribeToEventType();

}
