package org.cybnity.framework.domain.application;

import org.cybnity.framework.domain.NotificationLog;
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
public interface NotificationService {

    public NotificationLog currentNotificationLog();

    /**
     * Find a notification lof from an identifier.
     * 
     * @param aNotificationlogId Identifier to search.
     * @return A found log. Or null if not found.
     * @throws IllegalArgumentException When mandatory log parameter is not defined.
     */
    public NotificationLog notificationLog(Identifier aNotificationlogId) throws IllegalArgumentException;

    /**
     * Publish individual Notification instances over a messaging mechanism.
     */
    public void publishNotifications();

}
