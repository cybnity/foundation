package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Subscription contract allowing to receive notification about event changes.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface Subscribable {

    /**
     * Add a listener as interested to be notified about facts.
     * 
     * @param aSubscriber The mandatory subscriber to inform about changes.
     */
    public <T> void subscribe(DomainEventSubscriber<T> aSubscriber);

}
