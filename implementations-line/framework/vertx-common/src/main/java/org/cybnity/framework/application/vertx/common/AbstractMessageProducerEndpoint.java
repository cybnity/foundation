package org.cybnity.framework.application.vertx.common;

import io.vertx.core.AbstractVerticle;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.IMessageMapperProvider;

/**
 * Verticle supporting stream event production to one or several streams (e.g channel or stream over a Users Interactions Space).
 * This abstract class can be redefined by subclass providing tasks with automatic production of messages.
 * It's an implementation of producer endpoint type that produce fact events to channel(s) (e.g Redis's stream or topic).
 * It's a specialized Channel Adapter pattern that has been custom developed for and integrated into its application.
 */
public abstract class AbstractMessageProducerEndpoint extends AbstractVerticle {

    /**
     * This default implementation method start the event producer and start this worker instance which shall include the execution of the complete() action on the startPromise parameter.
     */
    @Override
    public void start() {
        // Start event producers feeding channels by this instance
        startMessageProducers();
    }

    /**
     * Resource freedom (e.g undeploy of all event producer instances).
     */
    @Override
    public void stop() {
        // Stop the messages producers
        stopMessageProducers();
    }

    /**
     * Start producers managed by this worker instance.
     */
    abstract protected void startMessageProducers();

    /**
     * Stop producers (e.g threads) managed by this instance.
     */
    abstract protected void stopMessageProducers();

    /**
     * Get a factory of message mapper that support the message types supported by the endpoint (e.g common event types and/or custom types specific to a domain).
     *
     * @return A message mapper provider supporting custom and/or common event types of fact and messages.
     */
    abstract protected IMessageMapperProvider getMessageMapperProvider();
}
