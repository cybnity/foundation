package org.cybnity.infrastructure.technical.registry.adapter.api.event;

import org.cybnity.framework.domain.event.IEventType;

/**
 * Type of API command event supported by a Knowledge repository.
 * Each command name shall be a verb.
 */
public enum DataViewCommandName implements IEventType {

    /**
     * Add command of a subject.
     */
    ADD,

    /**
     * Update command of existing subject.
     */
    UPDATE,

    /**
     * Remove command of existing subject.
     */
    REMOVE;
}
