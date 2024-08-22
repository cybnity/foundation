package org.cybnity.infrastructure.technical.registry.adapter.api.event;

import org.cybnity.framework.domain.event.IEventType;

/**
 * Type of API query event supported by a Knowledge repository.
 * Each query name shall be a verb.
 */
public enum DataViewQueryName implements IEventType {
    /**
     * Search of a subject.
     */
    FIND;
}
