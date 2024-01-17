package org.cybnity.framework.application.vertx.common.routing;

import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.IPresenceObservability;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.IReferenceable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Channel;

import java.util.logging.Level;

/**
 * Listening implementation class of a domain IO Gateway allowing collaboration synchronization about routing paths.
 */
public class DomainIOGatewayRecipientsManagerObserver extends ProcessingUnitRecipientsManagerObserver {

    /**
     * Owner of this observation service.
     */
    private final IPresenceObservability observationOwner;

    /**
     * Default constructor.
     *
     * @param recipientsManagerOutputsNotifiedOver Mandatory channel to observe regarding recipients manager which need to be listened.
     * @param observationOwner                     Mandatory owner of this observer instance, that can be contributor of presence status detection (e.g in case of presence renewal requested by a recipients manager).
     * @throws IllegalArgumentException When a mandatory parameter is missing.
     */
    public DomainIOGatewayRecipientsManagerObserver(Channel recipientsManagerOutputsNotifiedOver, IPresenceObservability observationOwner) throws IllegalArgumentException {
        super(recipientsManagerOutputsNotifiedOver);
        if (observationOwner == null) throw new IllegalArgumentException("Observation owner parameter is required!");
        this.observationOwner = observationOwner;
    }

    /**
     * Called by parent when an event is received from observed channel.
     * This implementation execute the publication of a new event relative to the declaration of routing path and event types supported by a service.
     *
     * @param originRequest Request event (e.g CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCE_REQUESTED type) which is origin of the treatment need.
     */
    @Override
    protected void announcePresence(IDescribed originRequest) {
        if (originRequest != null) {
            // Identify the entity reference of the origin event
            EntityReference priorEventRef = null;
            if (IReferenceable.class.isAssignableFrom(originRequest.getClass())) {
                try {
                    IReferenceable referencableFact = (IReferenceable) originRequest;
                    priorEventRef = referencableFact.reference();
                } catch (ImmutabilityException ie) {
                    // Immutable version build problem
                    logger.log(Level.SEVERE, ie.getMessage(), ie);
                }
            }
            try {
                // Read the current status of presence regarding the owner of this service instance
                // and publish a collaboration event notifying the current presence status
                // (eventual restarted domain IO Gateway, that need to be again notified of this component entrypoint event types/routes supported)
                observationOwner.announcePresence(observationOwner.currentState(), priorEventRef);
            } catch (Exception e) {
                // Potential mapping problem during announce presence message build
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Called by parent when an event is received from observed channel.
     * This implementation manage any optional additional treatment (e.g finalized dynamic routing configuration log relative to this service provider) which can be executed after a registered routing paths from other system (e.g domain IO Gateway).
     *
     * @param originRequest Request event (e.g CollaborationEventType.PROCESSING_UNIT_ROUTING_PATHS_REGISTERED type) which is origin of the optional treatment need.
     */
    @Override
    protected void acknowledgedRoutingPath(IDescribed originRequest) {
        if (originRequest != null) {
            // Delegate the interpretation of the declaration result to the computation unit
            this.observationOwner.manageDeclaredPresenceAcknowledge(originRequest);
        }
    }

}
