package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Listening of messages published to channel that match one or more patterns.
 */
public interface ChannelObserver {

    /**
     * Pattern regarding observation of all channel messages.
     * Default value of pattern is equals to "*".
     */
    String DEFAULT_OBSERVATION_PATTERN = "*";

    /**
     * Get the channel listened.
     *
     * @return A channel that does not include an observation pattern regarding its channel name.
     */
    Channel observed();

    /**
     * Get the pattern of listening regarding the sub-names() of the observed channel.
     *
     * @return A pattern (e.g ".xxx.*" wildcard) to apply regarding the observed channel's name; or null.
     */
    String observationPattern();

    /**
     * Notify this observer regarding a stream entry (e.g command or domain event) promoted over the observed channel.
     *
     * @param event Event. Ignored when null.
     */
    void notify(Object event);
}
