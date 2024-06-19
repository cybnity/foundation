package org.cybnity.framework.domain.event;

/**
 * Type of standard attribute which can be supported by events and commands.
 * Referential that can be used to identify a type of specification attribute with a value.
 */
public enum AttributeName {

    /**
     * Logical name of a service provider (e.g domain feature name which notify an event)
     */
    SERVICE_NAME,

    /**
     * Type of processing result cause.
     */
    OUTPUT_CAUSE_TYPE,

    /**
     * Identifier of a Tenant.
     */
    TENANT_ID
}
