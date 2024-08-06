package org.cybnity.infrastructure.technical.registry.adapter.api.event;

import org.cybnity.framework.domain.event.IEventType;

/**
 * Type of API domain event supported by a Knowledge domain repository.
 * Each event name shall be an adjective representing a fact state.
 */
public enum DataViewEventType implements IEventType {

    /**
     * Event about a created subject into the knowledge data view layer.
     */
    DATAVIEW_ADDED,

    /**
     * Event about an existing subject upgraded into the knowledge data view layer.
     */
    DATAVIEW_CHANGED,

    /**
     * Event about a subject deleted from the knowledge data view layer.
     */
    DATAVIEW_REMOVED;
}
