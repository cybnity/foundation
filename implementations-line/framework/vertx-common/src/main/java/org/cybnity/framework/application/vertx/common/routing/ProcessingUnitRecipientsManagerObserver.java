package org.cybnity.framework.application.vertx.common.routing;

import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.event.CollaborationEventType;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Channel;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.ChannelObserver;

import java.util.logging.Logger;

/**
 * Listener of recipients list management component (e.g domain IO gateway) that publish events regarding several type of changes (e.g confirmation of presence registered) or requests (e.g renewal of announces by active processing units).
 */
public abstract class ProcessingUnitRecipientsManagerObserver implements ChannelObserver {

    /**
     * Technical logging
     */
    protected static final Logger logger = Logger.getLogger(ProcessingUnitRecipientsManagerObserver.class.getName());

    /**
     * Topic channel(s) where dynamic routing path change events are promoted.
     */
    private final Channel recipientsManagerObservedChannels;

    /**
     * Default constructor.
     *
     * @param recipientsManagerOutputsNotifiedOver Optional channel where event can be collected.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public ProcessingUnitRecipientsManagerObserver(Channel recipientsManagerOutputsNotifiedOver) throws IllegalArgumentException {
        if (recipientsManagerOutputsNotifiedOver == null)
            throw new IllegalArgumentException("Recipients manager outputs channel parameter is required!");
        // Define the collection of topics where recipients list changes shall be promoted
        recipientsManagerObservedChannels = recipientsManagerOutputsNotifiedOver;
    }

    @Override
    public Channel observed() {
        return recipientsManagerObservedChannels;
    }

    @Override
    public String observationPattern() {
        return null;
    }

    /**
     * Implementation of detection relative to processing unit presence announce requested, of confirmation of previous routing paths registration.
     *
     * @param event To treat.
     */
    @Override
    public void notify(IDescribed event) {
        if (event != null && event.type() != null) {
            // Identify the type of supported event
            String factEventTypeName = (event.type().value() != null && !event.type().value().isEmpty()) ? event.type().value() : null;
            if (factEventTypeName != null) {
                if (CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCE_REQUESTED.name().equals(factEventTypeName)) {
                    // Observed routing path manager is requesting a renewal of routes declarations
                    // Announce the routes supported
                    announcePresence(event);
                } else if (CollaborationEventType.PROCESSING_UNIT_ROUTING_PATHS_REGISTERED.name().equals(factEventTypeName)) {
                    // A previous routing path declaration have been registered by the recipients manager (e.g domain IO Gateway)
                    // It can be the routes declaration by this or by other feature components
                    acknowledgedRoutingPath(event);
                }
            }
        }
    }

    /**
     * Prepare and publish a presence event over the Users Interactions Space allowing to other component to collaborate.
     *
     * @param origin Origin request of presence announcing (e.g IO gateway demand of announce renewal).
     */
    protected abstract void announcePresence(IDescribed origin);

    /**
     * Manage a routing path registration confirmed.
     *
     * @param event A CollaborationEventType.PROCESSING_UNIT_ROUTING_PATHS_REGISTERED confirmation event.
     */
    protected abstract void acknowledgedRoutingPath(IDescribed event);
}
