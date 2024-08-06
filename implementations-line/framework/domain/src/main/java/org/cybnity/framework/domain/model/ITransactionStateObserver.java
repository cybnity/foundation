package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;

/**
 * Contract of notification supported by a component observing transactions status.
 */
public interface ITransactionStateObserver {

    /**
     * Notify about transaction state evolution (e.g end of transaction execution with/without result).
     *
     * @param notification Notification event.
     */
    public void notifyTransactionState(DomainEvent notification);
}
