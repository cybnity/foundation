package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.domain.IDescribed;

/**
 * Listening of messages published to channel that match one or more patterns.
 */
public interface ChannelObserver {

    /**
     * Get the channel listened.
     *
     * @return A channel.
     */
    public Channel observed();

    /**
     * Get the pattern of listening regarding the messages detected into the observed channel.
     *
     * @return A pattern or null.
     */
    public String observationPattern();

    /**
     * Get the group name that this observer is member.
     *
     * @return A group name or null.
     */
    public String consumerGroupName();

    /**
     * Notify this observer regarding a command or domain event promoted over the observed channel.
     *
     * @param event Event. Ignored when null.
     */
    public void notify(IDescribed event);
}
