package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Standard channel interface relative to a channel managing users interactions (e.g over UI middleware like UIS).
 */
public interface ICapabilityChannel {

    /**
     * Get the short label (e.g acronym) regarding this channel.
     *
     * @return An acronym or null.
     */
    String shortName();
}
