package org.cybnity.framework.domain.event;

import java.util.UUID;

/**
 * Factory of unique technical identifier (e.g reusable as identification value of event) based on Java random function.
 */
public class RandomUUIDFactory {

    /**
     * Generate unique randomUUID value.
     *
     * @param salt Optional differentiation value to be added as salt during the generated value.
     * @return Generated value that optionally used the salt during generation process. Unique id is based on concatenated variable information relative to the current JVM time, and pseudo randomly generated code value from UUID.randomUUID().
     */
    public static String generate(String salt) {
        // Prepare unique id based on concatenated variable information relative to the current processing computer
        StringBuilder value = new StringBuilder();

        // Add a unique value based on JVM time
        value.append(System.currentTimeMillis());

        if (salt != null && !salt.isEmpty())
            // Add salt value hash code
            value.append(salt.hashCode());

        // Add a pseudo randomly generated code value
        value.append(UUID.randomUUID());

        return value.toString();
    }
}
