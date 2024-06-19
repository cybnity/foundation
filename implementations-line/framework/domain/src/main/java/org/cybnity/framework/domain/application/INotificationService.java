package org.cybnity.framework.domain.application;

import org.cybnity.framework.domain.model.EventStore;
import org.cybnity.framework.domain.model.NotificationLog;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Consider the requirements for publishing events from an event store via a
 * messaginig infrastructure (e.g middleware product supporting
 * publish-subscribe capability also call "fanout exchange"). The main supported
 * process is to query all domain events objects from an event store (that have
 * not yet beend published) with ordering in ascending way by their sequenced
 * unique identity; and iterat to send them to the exchange zone; and when the
 * messaging system indicates that the message was successfully published, to
 * track the domain event as having been published through that exchange
 * (without wait if subscribers confirmed reception, but allow only allow the
 * messaging mechanism to guarantee delivery).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface INotificationService {

    NotificationLog currentNotificationLog();

    /**
     * Find a notification log from an identifier.
     * 
     * @param aNotificationlogId Identifier to search.
     * @param eventStore         Log sourcing store.
     * @return A found log. Or null if not found.
     * @throws IllegalArgumentException When mandatory log parameter is not defined.
     */
    NotificationLog findNotificationLog(Identifier aNotificationlogId, EventStore eventStore)
	    throws IllegalArgumentException;

    /**
     * Publish unpublished notification event instances over a messaging mechanism.
     */
    void publishNotifications();

}