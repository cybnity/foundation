package org.cybnity.framework;

import java.util.Map;

/**
 * Configuration abstraction layer encapsulating the logic of retrieving and managing configuration properties.
 * This promotes loose coupling and makes it easier to switch between different configuration sources.
 */
public interface ConfigurationSource {
    /**
     * Get configuration resources.
     *
     * @return A set of properties and values, or null.
     * @throws UnoperationalStateException When impossible providing of properties (e.g; file based source read problem).
     */
    Map<String, String> getProperties() throws UnoperationalStateException;
}
