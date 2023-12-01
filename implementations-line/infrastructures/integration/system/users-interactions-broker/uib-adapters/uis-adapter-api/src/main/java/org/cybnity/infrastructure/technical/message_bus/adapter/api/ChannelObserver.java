package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;

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
     * Notify this observer regarding a fact event promoted over the observed channel.
     *
     * @param event Event. Ignored when null.
     */
    public void notify(DomainEvent event);

    /**
     * Notify this observer regarding a command event promoted over the observed channel.
     *
     * @param command Event. Ignored when null.
     */
    public void notify(Command command);
}
