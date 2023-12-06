package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Referential of naming conventions regarding elements managed via the message bus (e.g channel name separator).
 */
public class NamingConventions {

    /**
     * Define the standardized character usable as separator between text included into a stream path name.
     * The separator defined as standard is ":".
     */
    public static String STREAM_NAME_SEPARATOR = ":";

    /**
     * Define the standardized character usable as separator between text included into a channel path name.
     * The separator defined as standard is ":".
     */
    public static String CHANNEL_NAME_SEPARATOR = STREAM_NAME_SEPARATOR;

}
