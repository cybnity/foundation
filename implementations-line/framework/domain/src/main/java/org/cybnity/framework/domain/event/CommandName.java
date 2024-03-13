package org.cybnity.framework.domain.event;

/**
 * Type of API command event supported by the model.
 * Each command name shall be a verb.
 */
public enum CommandName implements IEventType {

    /**
     * Registration command of a tenant eligible to become a perimeter of access control.
     */
    CREATE_TENANT,

    /**
     * Upgrade an existing tenant.
     */
    MODIFY_TENANT,

    /**
     * Delete an existing tenant.
     */
    DELETE_TENANT;
}
