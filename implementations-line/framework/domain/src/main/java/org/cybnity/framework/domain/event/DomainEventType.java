package org.cybnity.framework.domain.event;

/**
 * Type of API event supported by the framework model.
 * Each event name shall be an adjective representing a fact state.
 */
public enum DomainEventType implements IEventType {

    /**
     * Event about a tenant domain object created into the domain layer.
     */
    TENANT_CREATED,

    /**
     * Event about an existing tenant upgraded into the domain layer.
     */
    TENANT_CHANGED,

    /**
     * Event about a tenant deleted from the domain layer.
     */
    TENANT_DELETED

}
