package org.cybnity.framework.domain.event;

/**
 * Factory of correlation identifier reusable into command event.
 */
public class CorrelationIdFactory {

    /**
     * Generate unique correlation identifier value.
     *
     * @param salt Optional differentiation value to be added as salt during the generated value.
     * @return Generated value that optionally used the salt during generation process.
     */
    public static String generate(String salt) {
        return RandomUUIDFactory.generate(salt);
    }
}
