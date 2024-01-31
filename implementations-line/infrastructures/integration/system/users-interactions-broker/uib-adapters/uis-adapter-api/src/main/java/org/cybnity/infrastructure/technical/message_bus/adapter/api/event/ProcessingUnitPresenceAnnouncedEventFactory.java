package org.cybnity.infrastructure.technical.message_bus.adapter.api.event;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IPresenceObservability;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.CollaborationEventType;
import org.cybnity.framework.domain.event.CorrelationIdFactory;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.ICapabilityChannel;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Factory of processing unit operational status change announcing event.
 */
public class ProcessingUnitPresenceAnnouncedEventFactory {

    /**
     * Prepare a domain event relative to a processing unit presence status that was changed.
     *
     * @param supportedEventTypesToRoutingPath Optional set of routing map regarding supported event types by the announced PU, and including entrypoint channel paths.
     * @param puServiceName                    Optional logical name of the processing unit that is able to treat the announced event types.
     * @param currentStatus                    Mandatory current status of presence which is defined by the built announce.
     * @return A prepared instance of change event.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException When impossible read of priorEventRef identity.
     */
    public ProcessingUnitPresenceAnnounced create(Map<IEventType, ICapabilityChannel> supportedEventTypesToRoutingPath, String puServiceName, EntityReference priorEventRef, IPresenceObservability.PresenceState currentStatus) throws IllegalArgumentException, ImmutabilityException {
        if (currentStatus == null) throw new IllegalArgumentException("Current presence status parameter is required!");

        // Create new event identity based on current time, and random technical value
        String uidValue = CorrelationIdFactory.generate(null);
        LinkedHashSet<Identifier> identificationValues = new LinkedHashSet<>();
        identificationValues.add(new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), uidValue)); // Basic technical id of event
        if (priorEventRef!=null) {
            // Add complementary identifier of prior event
            identificationValues.add(priorEventRef.getEntity().identified());
        }
        // Prepare announce event final identity
        DomainEntity identifiedBy = new DomainEntity(identificationValues);

        // Prepare announcing event
        ProcessingUnitPresenceAnnounced announced = new ProcessingUnitPresenceAnnounced(identifiedBy, CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED);

        if (supportedEventTypesToRoutingPath != null) {
            // Create a description of each PU supported event type and routing path
            for (Map.Entry<IEventType, ICapabilityChannel> route : supportedEventTypesToRoutingPath.entrySet()) {
                if (route.getKey() != null && route.getValue() != null) {
                    // Add definition of routing map supporting a type of event which can be treated by the PU
                    announced.appendEventRoutingPath(new Attribute(route.getKey().name(), route.getValue().shortName()));
                }
            }
        }

        // Optionally add reference to previous origin event
        if (priorEventRef != null) {
            announced.setChangeCommandRef(priorEventRef);
        }

        // Add announced presence status
        announced.setPresenceStatus(currentStatus);

        // Add optional name of processing unit notifier of the change
        if (puServiceName != null && !puServiceName.isEmpty())
            announced.setServiceName(puServiceName);

        // Generate a correlation identifier about the announced presence, that can be reused by PU's presence observers and referenced in case of child event promoted (e.g about presence registration realized as delegate processing unit)
        announced.appendSpecification(new Attribute(Command.CORRELATION_ID, CorrelationIdFactory.generate(uidValue /* event uid as salt */)));

        return announced;
    }
}
