package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.model.sample.AccessControlDomain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Behavior test regarding read model projection descriptor.
 */
public class ReadModelProjectionDescriptorUseCaseTest {

    /**
     * Verify that a descriptor defined by the minimum required properties in valid type allow success descriptor instantiation.
     */
    @Test
    public void givenMinimumRequiredProperties_whenInstantiate_CompletenessAndConformityConfirmed() {
        // Create valid definition regarding types and defined values
        String label = "tenantByName";
        IDomainModel domain = new AccessControlDomain();
        ReadModelProjectionDescriptor desc = ReadModelProjectionDescriptor.instanceOf(label, domain);
        Assertions.assertNotNull(desc);
        Assertions.assertEquals(label,desc.label());
        Assertions.assertEquals(domain, desc.ownership());
    }

    /**
     * Verify that a definition of descriptor based on an invalid type of minimum expected property is rejected.
     */
    @Test
    public void givenInvalidPropertyTypes_whenInstantiate_ConformityRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Create valid defined property but including invalid value type (e.g IDomainModel type)
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.LABEL.name(), "tenantByName");
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.OWNERSHIP.name(), Boolean.FALSE /* invalid type of expected value */);
            new ReadModelProjectionDescriptor(properties);
        });
    }

    /**
     * Verify that a definition based on an missing property value (e.g label) is rejected.
     */
    @Test
    public void givenMissingMinimumPropertyValue_whenInstantiate_CompletenessRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Create valid defined property but including invalid value
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.LABEL.name(), "tenantByName");
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.OWNERSHIP.name(), null /* invalid undefined value */);
            new ReadModelProjectionDescriptor(properties);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            // Create valid defined property but including invalid value
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.LABEL.name(), "" /* invalid undefined value */);
            properties.put(ReadModelProjectionDescriptor.PropertyAttributeKey.OWNERSHIP.name(), new AccessControlDomain());
            new ReadModelProjectionDescriptor(properties);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            // Create empty property set
            HashMap<String, Object> properties = new HashMap<>();
            new ReadModelProjectionDescriptor(properties);
        });
    }
}
