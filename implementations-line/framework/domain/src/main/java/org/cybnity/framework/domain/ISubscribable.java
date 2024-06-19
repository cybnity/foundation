package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Subscription contract allowing to receive notification about fact events.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface ISubscribable {

    /**
     * Add a listener as interested to be notified about facts.
     * 
     * @param aSubscriber The mandatory subscriber to inform about changes.
     * @param <T> Specialized subscriber type.
     */
    <T> void subscribe(DomainEventSubscriber<T> aSubscriber);

    /**
     * Remove a subscriber of the register if existing.
     * 
     * @param aSubscriber The mandatory subscriber to remove from register.
     * @param <T> Specialized subscriber type.
     */
    <T> void remove(DomainEventSubscriber<T> aSubscriber);
}
