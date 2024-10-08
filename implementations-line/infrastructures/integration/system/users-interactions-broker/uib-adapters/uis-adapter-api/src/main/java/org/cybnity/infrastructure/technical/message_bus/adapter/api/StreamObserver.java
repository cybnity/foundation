package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Listening of messages published to a stream that match one or more patterns.
 */
public interface StreamObserver {

    /**
     * Pattern regarding observation of all stream messages.
     * Default value of pattern is equals to "0-0".
     */
    String DEFAULT_OBSERVATION_PATTERN = "0-0";

    /**
     * Get the stream listened.
     *
     * @return A stream.
     */
    Stream observed();

    /**
     * Get the pattern of listening regarding the messages detected into the observed stream.
     *
     * @return A pattern as filter, or null.
     */
    String observationPattern();

    /**
     * Get the group name that this observer is member.
     *
     * @return A group name or null.
     */
    String consumerGroupName();

    /**
     * Notify this observer regarding a stream entry (e.g command or domain event) promoted over the observed stream.
     *
     * @param event Event. Ignored when null.
     */
    void notify(Object event);
}
