package org.cybnity.framework.application.vertx.common;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.IMessageMapperProvider;

/**
 * Channel Adapter pattern allow to access to an application's API or data to push messages on a channel based on the data and that likewise can receive messages and invoke functionality inside the application.
 * The adapter acts as a messaging client to a messaging system and invokes application functions via an application-supplied interface.
 * Likewise, a Channel Adapter can listen to application-internal events and invoke the messaging system in response to these events.
 * The Channel Adapter can connect to different layers of the application's architecture, depending on that architecture and the type of data the messaging system needs to access.
 * It can be materialized by implementation classes like User Interface Adapter, Business Logic Adapter, Database Adapter.
 */
public interface IChannelAdapter {

    /**
     * Get a factory of message mapper that support the message types supported by the endpoint (e.g common event types and/or custom types specific to a domain).
     *
     * @return A message mapper provider supporting custom and/or common event types of fact and messages.
     */
    public IMessageMapperProvider getMessageMapperProvider();
}
