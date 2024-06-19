package org.cybnity.framework.domain.event;

/**
 * Type of system collaboration event or command supported by a domain that allow coordination between multiple systems.
 */
public enum CollaborationEventType implements IEventType {

    /**
     * Event about a processing unit presence change status that is announced.
     */
    PROCESSING_UNIT_PRESENCE_ANNOUNCED,

    /**
     * Event about a request to processing unit for send presence announce.
     * This type of event is usable by any domain gateway which need to be notified about current active processing units (e.g features).
     */
    PROCESSING_UNIT_PRESENCE_ANNOUNCE_REQUESTED,

    /**
     * Event about a registered routing paths (e.g path to PU entrypoint channel) to a processing unit (e.g considered as eligible to delegation of command events treatment).
     */
    PROCESSING_UNIT_ROUTING_PATHS_REGISTERED

}
