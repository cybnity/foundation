package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Factory of correlation identifier reusable into command event.
 */
public class CorrelationIdFactory {
    /**
     * Generate unique correlation identifier value.
     *
     * @param salt Optional client differentiation value to be added as salt during the generated value.
     * @return Generated value that optionally used the salt during generation process.
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
