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
import org.cybnity.infrastructure.technical.message_bus.adapter.api.ICapabilityChannel;

import java.util.Map;
import java.util.UUID;

/**
 * Factory of processing unit operational status change announcing event.
 */
public class ProcessingUnitPresenceAnnouncedEventFactory {

    /**
     * Prepare a domain event relative to a processing unit presence status that was changed.
     *
     * @param supportedEventTypesToRoutingPath Mandatory set of routing map regarding supported event types by PU, and entrypoint channel paths.
     * @param puServiceName                    Optional logical name of the processing unit that is able to treat the announced event types.
     * @param currentStatus                    Mandatory current status of presence which is defined by the built announce.
     * @return A prepared instance of change event.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public ProcessingUnitPresenceAnnounced create(Map<IEventType, ICapabilityChannel> supportedEventTypesToRoutingPath, String puServiceName, EntityReference priorEventRef, IPresenceObservability.PresenceState currentStatus) throws IllegalArgumentException {
        if (supportedEventTypesToRoutingPath == null || supportedEventTypesToRoutingPath.isEmpty())
            throw new IllegalArgumentException("supportedEventTypesToRoutingPath parameter is required and shall not be empty!");
        if (currentStatus == null) throw new IllegalArgumentException("currentStatus parameter is required!");

        // Create new event identity
        String uidValue = UUID.randomUUID().toString();
        DomainEntity identifiedBy = new DomainEntity(new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), uidValue));

        // Prepare announcing event
        ProcessingUnitPresenceAnnounced announced = new ProcessingUnitPresenceAnnounced(identifiedBy, CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED);

        // Create a description of each PU supported event type and routing path
        for (Map.Entry<IEventType, ICapabilityChannel> route : supportedEventTypesToRoutingPath.entrySet()) {
            if (route.getKey() != null && route.getValue() != null) {
                // Add definition of routing map supporting a type of event which can be treated by the PU
                announced.appendEventRoutingPath(new Attribute(route.getKey().name(), route.getValue().shortName()));
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
