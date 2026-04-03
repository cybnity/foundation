package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.ITransactionStateObserver;

/**
 * Implementation class template representing an unit transaction (e.g create of a data-view version),
 * that is specific to a Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a change operation.
 */
public abstract class AbstractDataViewVersionTransactionImpl {

    /**
     * Eligible to notification about result of transaction execution end.
     */
    private ITransactionStateObserver notifiable;

    /**
     * Default constructor.
     *
     * @param notifiable Optional observer of transaction execution end which can be notified.
     */
    public AbstractDataViewVersionTransactionImpl(ITransactionStateObserver notifiable) {
        this.notifiable = notifiable;
    }

    /**
     * Get a notifiable observer of this transaction.
     *
     * @return A notifiable or null.
     */
    protected ITransactionStateObserver getNotifiable() {
        return this.notifiable;
    }

    /**
     * Define an observer of this transaction.
     *
     * @param subscriber A notifiable observer.
     */
    protected void setNotifiable(ITransactionStateObserver subscriber) {
        this.notifiable = subscriber;
    }

    /**
     * Notify observer of a transaction event when identified.
     *
     * @param dataViewEvent Event regarding this data view version (e.g change transaction execution step, or end, and/or including data view version information like identifier, version, result of change...). Ignored when null.
     */
    protected void notifyEvent(DomainEvent dataViewEvent) {
        if (dataViewEvent != null) {
            ITransactionStateObserver notifiable = getNotifiable();
            if (notifiable != null) {
                // Notify the event
                notifiable.notifyTransactionState(dataViewEvent);
            }
        }
    }
}
