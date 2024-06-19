package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Referential of naming conventions regarding elements managed via the space (e.g pub/sub or stream channel name separator).
 */
public class NamingConventions {

    /**
     * Define the standardized character usable as separator between text included into a stream path name.
     * The separator defined as standard is "-".
     */
    public static String STREAM_NAME_SEPARATOR = "-";

    /**
     * Define the standardized character usable as separator between text included into a channel path name.
     * The separator defined as standard is ".".
     */
    public static String CHANNEL_NAME_SEPARATOR = ".";

    /**
     * Define the standardized character usable as separator between text included into a data key name.
     * The separator defined as standard is ":".
     */
    public static String KEY_NAME_SEPARATOR = ":";

    /**
     * Define the standardized character usable as separator between text included into a logical name of component in interaction with the UIS.
     * The separator defined as standard is "-".
     */
    public static String SPACE_ACTOR_NAME_SEPARATOR = "-";
}
