package org.cybnity.framework.application.vertx.common.routing;

import org.cybnity.framework.application.vertx.common.event.AttributeName;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.CollaborationEventType;
import org.cybnity.framework.domain.event.DomainEventFactory;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Channel;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.ChannelObserver;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.MessageMapperFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Listener of processing unit announces implementing a domain recipients list pattern for dynamic routes registration.
 * It's observation class simplify the implementation of dynamic routes registration by any domain gateway.
 */
public class ProcessingUnitAnnouncesObserver implements ChannelObserver, IEventProcessingManager {

    /**
     * Logical name of the component managing routing paths.
     */
    private final String dynamicRoutingServiceName;

    /**
     * Technical logging
     */
    protected static final Logger logger = Logger.getLogger(ProcessingUnitAnnouncesObserver.class.getName());

    /**
     * Control channel allowing to receive the computation unit announces of presence which can be registered as eligible execution recipients for event types treatment.
     * It's a control channel of this router (Dynamic Router pattern) that can self-configure based on special configuration messages from participating destinations (e.g other independent UI capability components ready for processing of event over the UIS).
     */
    private final Channel dynamicRoutersControlChannel;

    /**
     * Map of announced processing units.
     * Identify existing path (e.g UIS stream recipient) to the remote service components which are eligible as delegate for event treatment
     * based on RecipientList pattern implementation according to the fact event type name.
     */
    private final RouteRecipientList delegatesDestinationMap = new RouteRecipientList();

    /**
     * Client to Users Interactions Space allowing notifications of changes regarding the routing plan.
     */
    private final UISAdapter uisClient;

    /**
     * Topic channel(s) where dynamic routing path change events are promoted.
     */
    private final Collection<Channel> registeredRoutingPathChange;

    /**
     * Default constructor.
     *
     * @param dynamicRoutersControlChannel      Mandatory control channel that shall be observed to be notified of processing unit presence announces.
     * @param dynamicRoutingServiceName         Optional name regarding the logical component which own this routing service.
     * @param uisClient                         Optional Users Interactions Space adapter usable for notification of registered routes over UIS channel. When null, the recipients list updated are not notified.
     * @param dynamicRoutingChangesNotifiedOver Optional channel where event can be promoted when the managed recipients list have been changed.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public ProcessingUnitAnnouncesObserver(Channel dynamicRoutersControlChannel, String dynamicRoutingServiceName, UISAdapter uisClient, Channel dynamicRoutingChangesNotifiedOver) throws IllegalArgumentException {
        if (dynamicRoutersControlChannel == null)
            throw new IllegalArgumentException("Dynamic routers control channel parameter is required!");
        this.dynamicRoutersControlChannel = dynamicRoutersControlChannel;
        this.dynamicRoutingServiceName = dynamicRoutingServiceName;
        this.uisClient = uisClient;

        // Define the collection of topics where recipients list changes shall be promoted
        registeredRoutingPathChange = new ArrayList<>();
        if (dynamicRoutingChangesNotifiedOver != null)
            // Only promote recipients list changes when notification channels is defined
            registeredRoutingPathChange.add(dynamicRoutingChangesNotifiedOver);
    }

    /**
     * Get the control channel received the announces of presence declared by the processing units eligible as event treatment delegates.
     *
     * @return A channel.
     */
    @Override
    public Channel observed() {
        return dynamicRoutersControlChannel;
    }

    @Override
    public String observationPattern() {
        return null;
    }

    /**
     * Interpret only ProcessingUnitPresenceAnnounced even type including a defined presence status.
     * When notified presence is about unavailable processing unit (e.g stopped processing unit which does not continue to deliver services), it's possible that another processing unit instance (e.g in case of multiple parallel processors managing events from a same stream endpoint) continue to support the event types previously registered as eligible to forwarding.
     * In this supported case, this implementation does not remove potential previously declared recipient routing paths.
     *
     * @param event Treated event. When presence status is not defined by the ProcessingUnitPresenceAnnounced event received, the event is ignored.
     */
    @Override
    public void notify(Object event) {
        if (event instanceof ProcessingUnitPresenceAnnounced) {
            ProcessingUnitPresenceAnnounced puEvent = (ProcessingUnitPresenceAnnounced) event;
            Attribute presenceState = puEvent.presenceStatus();

            if (presenceState != null) {
                IPresenceObservability.PresenceState declaredPresence = IPresenceObservability.PresenceState.valueOf(presenceState.value());
                if (IPresenceObservability.PresenceState.AVAILABLE == declaredPresence) {
                    // It's an event promoted by a processing unit regarding its presence and availability for delegation of event treatment
                    Collection<Attribute> eventsRoutingPathsCollection = puEvent.eventsRoutingPaths();

                    // Register the paths per supported event type into the dynamic controlled recipients list
                    String recipientPath;
                    String supportedEventTypeName;
                    boolean changedRecipientsContainer = false;
                    for (Attribute routeDefinition : eventsRoutingPathsCollection) {
                        supportedEventTypeName = routeDefinition.name();
                        recipientPath = routeDefinition.value();
                        if (supportedEventTypeName != null && !supportedEventTypeName.isEmpty()) {
                            // Update the dynamic recipient list regarding event type definition
                            // (add of new path, upgrade of existing path, deletion of previous path)
                            if (delegatesDestinationMap.addRoute(supportedEventTypeName.trim(), recipientPath)) {
                                changedRecipientsContainer = true;
                            }
                        }
                    }

                    // Notify confirmed dynamic recipients list changes
                    // when promotion channel is defined
                    if (changedRecipientsContainer && !registeredRoutingPathChange.isEmpty()) {
                        notifyDynamicRecipientListChanged(puEvent, uisClient);
                    }
                } else if (IPresenceObservability.PresenceState.UNAVAILABLE == declaredPresence) {
                    // It's notified presence as unavailable (e.g stopped processing unit which does not continue to deliver services)
                    // but it's possible that another processing unit instance (e.g in case of multiple parallel processors managing events from a same stream endpoint) continue to support the event types previously registered as eligible to forwarding
                    // So don't remove potential declared recipient routing paths
                }
            } // else It's an unknown presence status; so ignore event
        } else {
            // Invalid type of notification event received into the control channel
            logger.severe("Reception of invalid event type into the control channel (" + observed().name() + ") which shall only receive " + ProcessingUnitPresenceAnnounced.class.getSimpleName() + " supported event!");
        }
    }

    /**
     * Notify the changed recipients list regarding delegation path registered to a processing unit.
     * The notifications are only performed when UIS client is defined.
     *
     * @param origin Original cause of routing map change. When null, none notification promoted.
     * @param client Adapter of UIS to feed with CollaborationEventType.PROCESSING_UNIT_ROUTING_PATH_REGISTERED event notification. When null, none notification promoted.
     */
    private void notifyDynamicRecipientListChanged(DomainEvent origin, UISAdapter client) {
        if (client != null && origin != null) {
            // Notify registered routing map to service managed topic's observers
            try {
                LinkedHashSet<Identifier> childEventIdentifiers = new LinkedHashSet<>();
                // Define a unique identifier of the new event
                childEventIdentifiers.add(new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
                // Reference parent identifier origin of this fact
                Identifier parentId = origin.identified();
                if (parentId != null)
                    childEventIdentifiers.add((Identifier) parentId.immutable());
                DomainEntity identifiedBy = new DomainEntity(childEventIdentifiers);

                // Define optional descriptions relative to the gateway notifying the routing map management
                Collection<Attribute> specification = new ArrayList<>();
                updateWithDefaultSpecificationCriteria(specification); // Add default common specification criteria

                // Optional origin event's correlationId if defined (e.g allowing a transactional response interpretation by the subscriber that previously notified its proposal as processing unit)
                Attribute correlationId = origin.correlationId();
                if (correlationId != null) {
                    EventSpecification.appendSpecification(correlationId, specification);
                }

                // Notify the recipients list changed when a promotion channel name is managed
                if (!registeredRoutingPathChange.isEmpty()) {
                    DomainEvent puRegisteredNotification = DomainEventFactory.create(CollaborationEventType.PROCESSING_UNIT_ROUTING_PATHS_REGISTERED.name(), identifiedBy, specification, /* priorCommandRef */ origin.reference(), null /* domain changedModelElementRef */);
                    // Create change event and publish notification on channel(s) potential observed by other domain components (e.g PU which updated the API recipients list about its delegation routing plan)
                    client.publish(puRegisteredNotification, registeredRoutingPathChange, new MessageMapperFactory().getMapper(IDescribed.class, String.class));
                }
            } catch (ImmutabilityException ie) {
                logger.warning(ie.getMessage());
            } catch (Exception e) {
                // Problem during the publishing of change event to UIS channel(s)
            }
        }
    }

    /**
     * Define common specification criteria as Attribute into a specification of event.
     *
     * @param specification The updated specification attributes list (added AttributeName.ServiceName.name() and AttributeName.SourceChannelName.name()).
     */
    private void updateWithDefaultSpecificationCriteria(Collection<Attribute> specification) {
        if (specification != null) {
            if (this.dynamicRoutingServiceName != null && !this.dynamicRoutingServiceName.isEmpty())
                // Owner of the dynamic routing service
                EventSpecification.appendSpecification(new Attribute(AttributeName.ServiceName.name(), dynamicRoutingServiceName), specification);

            // Original path of received registration request
            EventSpecification.appendSpecification(new Attribute(AttributeName.SourceChannelName.name(), dynamicRoutersControlChannel.name()), specification);
        }
    }

    @Override
    public RouteRecipientList delegateDestinations() {
        return delegatesDestinationMap;
    }

}
