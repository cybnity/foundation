package org.cybnity.framework.domain.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Factory behavior test.
 */
public class CorrelationIdFactoryUseCaseTest {

    /**
     * Check that uuid generated includes original salt hashcode when defined.
     */
    @Test
    public void givenFactory_whenSaltDefined_thenIncludedIntoGeneratedID() {
        String salt = "cybnity";
        String id = CorrelationIdFactory.generate(salt);
        Assertions.assertFalse(id.isEmpty(), "Shall have be generated!");
        Assertions.assertTrue(id.contains(Integer.toString(salt.hashCode())), "salt hashcode version shall have been included!");
    }

    /**
     * Check that uuid generated without required salt parameter.
     */
    @Test
    public void givenNoneSalt_whenGenerateCorrelationIdentifier_thenUniqueIdProvided() {
        String id = CorrelationIdFactory.generate(null);
        Assertions.assertFalse(id.isEmpty(), "Shall have been generated!");
    }
}
