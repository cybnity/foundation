package org.cybnity.framework.application.vertx.common.event;

/**
 * Type of system collaboration event supported by the a domain that allow coordination between multiple systems.
 */
public enum CollaborationEventType {

    /**
     * Event about a processing unit presence change status that is announced.
     */
    PROCESSING_UNIT_PRESENCE_ANNOUNCED,

    /**
     * Event about a registered routing paths (e.g path to PU entrypoint channel) to a processing unit (e.g considered as eligible to delegation of command events treatment).
     */
    PROCESSING_UNIT_ROUTING_PATHS_REGISTERED;
}
