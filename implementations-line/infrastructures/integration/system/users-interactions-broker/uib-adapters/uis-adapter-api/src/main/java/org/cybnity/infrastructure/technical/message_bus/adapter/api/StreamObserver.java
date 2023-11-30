package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;

/**
 * Listening of messages published to a stream that match one or more patterns.
 */
public interface StreamObserver {

    /**
     * Get the stream listened.
     *
     * @return A stream.
     */
    public Channel observed();

    /**
     * Get the pattern of listening regarding the messages detected into the observed stream.
     *
     * @return A pattern as filter, or null.
     */
    public String observationPattern();

    /**
     * Notify this observer regarding a fact event promoted over the observed stream.
     *
     * @param event Event. Ignored when null.
     */
    public void notify(DomainEvent event);

    /**
     * Notify this observer regarding a command event promoted over the observed stream.
     *
     * @param command Event. Ignored when null.
     */
    public void notify(Command command);
}
