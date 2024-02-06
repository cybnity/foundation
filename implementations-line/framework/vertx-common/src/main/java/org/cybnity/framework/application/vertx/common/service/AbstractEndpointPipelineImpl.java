package org.cybnity.framework.application.vertx.common.service;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.Context;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.application.vertx.common.AbstractMessageConsumerEndpoint;
import org.cybnity.framework.application.vertx.common.routing.GatewayRecipientsManagerObserver;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.IPresenceObservability;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.event.ProcessingUnitPresenceAnnouncedEventFactory;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.UISAdapterImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common generic pipeline implementation class supporting all standard functions relative to common pipeline behaviors.
 */
public abstract class AbstractEndpointPipelineImpl extends AbstractMessageConsumerEndpoint implements StreamObserver, IPresenceObservability, ConfigurablePipeline {

    /**
     * Functional status based on the operational state of all consumers started and active.
     */
    private PresenceState currentPresenceStatus = PresenceState.UNAVAILABLE;

    /**
     * Client managing interactions with Users Interactions Space.
     */
    protected final UISAdapter uisClient;

    /**
     * Collection of fact event consumers (observing DIS entry items) of streams managed by this worker.
     */
    private final Collection<StreamObserver> entryPointStreamConsumers = new ArrayList<>();

    /**
     * Collection of fact event consumers of pub/sub topics listened by this worker.
     */
    private final Collection<ChannelObserver> topicsConsumers = new ArrayList<>();

    /**
     * Default constructor.
     *
     * @throws UnoperationalStateException When problem of context configuration (e.g missing environment variable defined to join the UIS or DIS).
     */
    public AbstractEndpointPipelineImpl() throws UnoperationalStateException {
        try {
            // Prepare client configured for interactions with the UIS
            // according to the defined environment variables (autonomous connection from worker to UIS)
            // defined on the runtime server executing this worker
            uisClient = new UISAdapterImpl(new Context() /* Current context of adapter runtime*/);
        } catch (IllegalArgumentException iae) {
            // Problem of context read
            throw new UnoperationalStateException(iae);
        }
    }

    /**
     * Enhance the default start process relative to observed channels and/or streams, with the send of event announcing the presence of this processing unit to other systems (e.g domain IO Gateway).
     */
    @Override
    public void start() {
        // Tag the current operational and active status
        currentPresenceStatus = PresenceState.AVAILABLE;

        // Execute by default the start operations relative to channels and streams consumers
        super.start();

        // Notify any other component about the processing unit presence in operational status
        try {
            // Promote announce about the supported event types consumed this pipeline
            announcePresence(currentState(), /* None previous origin event because it's the first start of this component start or restart */ null);
        } catch (Exception me) {
            // Normally shall never arrive; so notify implementation code issue
            Logger logger = logger();
            if (logger != null)
                logger.log(Level.SEVERE, me.getMessage(), me);
        }
    }

    /**
     * Enhance the default stop process relative to previously observed channels and/or streams, with the send of event notifying the end of processing unit presence (e.g to domain IO Gateway that should stop to forward feature execution requests to this component).
     */
    @Override
    public void stop() {
        // Tag the current operational as ended and no active status
        currentPresenceStatus = PresenceState.UNAVAILABLE;

        // Notify any other component about the processing unit presence in end of lifecycle status
        try {
            // Promote announce about the supported event types consumption end by this pipeline
            announcePresence(currentState(), null);
        } catch (Exception me) {
            // Normally shall never arrive; so notify implementation code issue
            Logger logger = logger();
            if (logger != null)
                logger.log(Level.SEVERE, me.getMessage(), me);
        }

        // Execute by default the stop operations relative to channels and streams previously observed
        super.stop();
    }

    @Override
    protected void cleanConsumersResources() {
        // Clean stream consumers set
        entryPointStreamConsumers.clear();
        // Clean channel consumers set
        topicsConsumers.clear();
        // Clean resource allowed by UIS client
        uisClient.freeUpResources();
    }

    /**
     * Get technical logging supporting this module.
     *
     * @return A dedicated and customized logger instance singleton.
     */
    protected abstract Logger logger();

    /**
     * Read the current operational presence status.
     *
     * @return Available state when all consumers are in operational status. Else return PresenceState.UNAVAILABLE status.
     */
    @Override
    public PresenceState currentState() {
        return currentPresenceStatus;
    }

    /**
     * Subscribe to observed channels, and send announce to domain gateway as feature presence start allowing to dynamically register routing path of event type supported by this feature implementation component.
     * Do nothing when none channel is returned by the featureGatewayRoutingPlanChangesChannel() call.
     */
    @Override
    protected void startChannelConsumers() {
        Channel recipientsManagerOutputsNotifiedOver = proxyRoutingPlanChangesChannel();
        if (recipientsManagerOutputsNotifiedOver != null) {
            // Listen the confirmation of routes registered by the domain gateway (as CollaborationEventType.PROCESSING_UNIT_ROUTING_PATHS_REGISTERED.name() event)
            // From the gateway's managed topic
            // Listening of acknowledges (announced presence confirmed registration) able to dynamically manage the eventual need retry to perform about the feature unit registration into the gateway's recipients list
            GatewayRecipientsManagerObserver recipientsManagerOutputsObserver = new GatewayRecipientsManagerObserver(recipientsManagerOutputsNotifiedOver, this);
            topicsConsumers.add(recipientsManagerOutputsObserver);
            Logger logger = logger();
            try {
                // Register observers on space
                uisClient.subscribe(topicsConsumers, getMessageMapperProvider().getMapper(String.class, IDescribed.class));
                if (logger != null)
                    logger.fine(featureModuleLogicalName() + " channel consumers started with success by worker (workerDeploymentId: " + this.deploymentID() + ")");
            } catch (UnoperationalStateException e) {
                if (logger != null)
                    logger.severe(e.getMessage());
            }
        }
    }

    /**
     * Start the listening of the entrypoint channel allowing to treat the feature realization requests.
     */
    @Override
    protected void startStreamConsumers() {
        // Create each entrypoint stream observed by this worker
        entryPointStreamConsumers.add(this);// Feature entrypoint observer

        // Define usable mapper supporting the read of stream message received from the Users Interactions Space and translated into domain event types
        MessageMapper eventMapper = getMessageMapperProvider().getMapper(StreamMessage.class, IDescribed.class);
        Logger logger = logger();

        try {
            // Register all consumers of observed channels
            uisClient.register(entryPointStreamConsumers, eventMapper);
            if (logger != null)
                logger.fine(featureModuleLogicalName() + " entrypoint stream consumers started with success by worker (workerDeploymentId: " + this.deploymentID() + ")");
        } catch (UnoperationalStateException e) {
            if (logger != null)
                logger.severe(e.getMessage());
        }
    }

    @Override
    protected void stopStreamConsumers() {
        // Stop each entrypoint stream previously observed by this worker
        uisClient.unregister(entryPointStreamConsumers);
        Logger logger = logger();
        if (logger != null)
            logger.fine(featureModuleLogicalName() + " entrypoint stream consumers un-registered with success by worker (workerDeploymentId: " + this.deploymentID() + ")");
    }

    /**
     * Unsubscribe to topics, and send announce to domain IO Gateway as feature presence end.
     */
    @Override
    protected void stopChannelConsumers() {
        // Stop each observed channel by this worker
        uisClient.unsubscribe(topicsConsumers);
        Logger logger = logger();
        if (logger != null)
            logger.fine(featureModuleLogicalName() + " consumers unsubscribed with success by worker (workerDeploymentId: " + this.deploymentID() + ")");
    }

    /**
     * Assembly of the events pipeline steps as a singleton responsibility chain and return it.
     *
     * @return A usable stateless pipelined process.
     */
    protected abstract FactBaseHandler pipelinedProcess();

    /**
     * Define and execute the pipelined commands according to a responsibility chain pattern.
     * Default entrypoint processing chain executed for each fact event received via the feature stream.
     * This implementation is a long-time running process executed into the current thread.
     *
     * @param event To process.
     */
    @Override
    public void notify(IDescribed event) {
        try {
            FactBaseHandler pipe = pipelinedProcess();
            if (pipe != null)
                // Execute the feature execution process/pipeline according to the received event type
                pipe.handle(event);
        } catch (Exception e) {
            // UnoperationalStateException or IllegalArgumentException thrown by responsibility chain members
            Logger logger = logger();
            if (logger != null)
                logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Prepare and publish a feature processing unit presence event over the Users Interactions Space allowing to other components to collaborate with the capability pipeline (e.g gateway forwarding command events).
     * This method announces the supported entry point event types when a proxy and routing paths are defined. Else announce is not published.
     *
     * @param presenceState Optional presence current status to announce. When null, this pipeline instance's current status of presence is assigned as default value equals to PresenceState.AVAILABLE.
     * @param priorEventRef Optional origin event (e.g request of announce renewal received from domain IO Gateway) that was prior to new event to generate and to publish.
     * @throws Exception When problem during the message preparation of build.
     */
    @Override
    public void announcePresence(PresenceState presenceState, EntityReference priorEventRef) throws Exception {
        if (presenceState == null) {
            // Define the current status of this pipeline which could be announced as available (because it's running instance)
            presenceState = PresenceState.AVAILABLE;
        }

        // Read the supported event type per channel type
        Map<IEventType, ICapabilityChannel> supportedEventTypesToRoutingPath = supportedEventTypesToRoutingPath();

        if (supportedEventTypesToRoutingPath != null && !supportedEventTypesToRoutingPath.isEmpty()) {
            // Prepare event to presence announcing channel
            ProcessingUnitPresenceAnnounced presenceAnnounce = new ProcessingUnitPresenceAnnouncedEventFactory().create(supportedEventTypesToRoutingPath, featureServiceName(), priorEventRef, presenceState);
            Channel domainIOGateway = proxyAnnouncingChannel();
            if (domainIOGateway != null)
                // Publish event to channel
                uisClient.publish(presenceAnnounce, domainIOGateway, getMessageMapperProvider().getMapper(presenceAnnounce.getClass(), String.class));
        }
    }

    /**
     * Get the event types supported by channels which can be announced by this pipeline as routable events.
     *
     * @return A set of routing path defined per event type. Null or empty list when none events and entry point channels are exposed by this pipeline.
     */
    protected abstract Map<IEventType, ICapabilityChannel> supportedEventTypesToRoutingPath();
}
