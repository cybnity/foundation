package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.INaming;

/**
 * Messages channel managed by the space in a Pub/Sub approach.
 * A channel allow implementation of Publish/Subscribe messaging paradigm where senders (publishers) are not programmed to send their messages to specific receivers (subscribers). Rather, published messages are characterized into channels, without knowledge of what (if any) subscribers there may be.
 * A channel is not persistent. A message is will be delivered once if at all. If subscriber is unable to handler the message, the message is forever lost.
 * For example, a channel can be a capability domain channel (e.g real-time collaboration between several user) that does not need persistence guarantee.
 */
public class Channel implements INaming {

    /**
     * Type of attribute allowing to define a channel specification.
     */
    public enum Specification {
        /**
         * Attribute name defining a channel's entrypoint path (e.g domain name) which can be used to define (e.g into a command event) a recipient channel name.
         */
        CHANNEL_ENTRYPOINT_PATH_NAME,

        /**
         * Key name regarding the identification of any fact record published, and retrieved by a Channel.
         */
        FACT_RECORD_ID_KEY_NAME,

        /**
         * Key name regarding an information (e.g event payload) regarding any fact record published, and retrieved by a Channel.
         */
        MESSAGE_PAYLOAD_KEY_NAME;
    }

    private final String name;

    /**
     * Default constructor.
     *
     * @param channelName Mandatory name of this channel.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public Channel(String channelName) throws IllegalArgumentException {
        if (channelName == null || channelName.isEmpty())
            throw new IllegalArgumentException("Channel name parameter is required!");
        this.name = channelName;
    }

    /**
     * Get the name of this channel.
     *
     * @return A name.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Equals implementation based on the stream name.
     *
     * @param obj To check.
     * @return False by default. True when equals stream name.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Channel) {
            Channel other = (Channel) obj;
            return this.name().equals(other.name());
        }
        return false;
    }
}
