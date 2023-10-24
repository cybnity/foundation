package org.cybnity.framework.domain.event;

import org.cybnity.framework.domain.Attribute;

import java.util.Collection;

/**
 * Utility class regarding event specification.
 */
public class EventSpecification {

    /**
     * Add attribute to the specification container.
     *
     * @param specificationCriteria Command attribute to add as command specification.
     * @param specification         Specification set to update.
     * @return True when defined criteria have been added to the specification and that not previously existing. Else return false.
     */
    static public boolean appendSpecification(Attribute specificationCriteria, Collection<Attribute> specification) {
        boolean appended = false;
        if (specificationCriteria != null && specification != null) {
            // Check that equals named attribute is not already defined in the specification
            if (!specification.contains(specificationCriteria)) {
                // Append the criteria
                specification.add(specificationCriteria);
                appended = true;
            }
        }
        return appended;
    }
}
