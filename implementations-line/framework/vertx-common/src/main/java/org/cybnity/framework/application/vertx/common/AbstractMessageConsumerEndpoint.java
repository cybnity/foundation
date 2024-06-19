package org.cybnity.framework.application.vertx.common;

import io.vertx.core.AbstractVerticle;

/**
 * Verticle supporting channel message (e.g event) consumption as an entry point (e.g channels observed from a Users Interactions Space).
 * This abstract class can be redefined by subclass providing tasks with automatic control check (e.g security control, event conformity and value check, transport metadata check) on received messages (e.g as filter, processor type...).
 * It's an implementation of consumer endpoint type that consumes messages from stream(s) and/or channel(s) monitored, and that execute treatment (e.g filtering, dispatching, processing) on it according to a specific domain feature realization (e.g domain application layer entry point) depending on a set of conditions.
 * It's a specialized Channel Adapter pattern that has been custom developed for and integrated into its application.
 */
public abstract class AbstractMessageConsumerEndpoint extends AbstractVerticle implements IChannelAdapter {

    /**
     * This default implementation method start the observed streams and/or channels.
     * This implementation start this worker instance which shall include the execution of the complete() action on the startPromise parameter.
     */
    @Override
    public void start() {
        // Start consumers listening the observed stream (as entry point) by this instance
        startStreamConsumers();
        startChannelConsumers();
    }

    /**
     * Undeploy of all streams and channels consumer instances regarding started consumers.
     */
    @Override
    public void stop() {
        // Stop the consumers listening
        stopStreamConsumers();
        stopChannelConsumers();
    }

    /**
     * Start consumers managed by this worker instance and their observation point.
     */
    abstract protected void startStreamConsumers();

    /**
     * Start consumers managed by this worker instance and their observation point.
     */
    abstract protected void startChannelConsumers();

    /**
     * Stop stream listeners (e.g threads) managed by this instance.
     */
    abstract protected void stopStreamConsumers();

    /**
     * Stop channel listeners managed by this instance.
     */
    abstract protected void stopChannelConsumers();

    /**
     * Clean all resources (e.g interaction middleware clients) used specifically by the consumers.
     */
    abstract protected void cleanConsumersResources();
}
