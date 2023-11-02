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

    /**
     * Search existing specification attribute based no specific name.
     *
     * @param criteriaName  Mandatory name of attribute to find.
     * @param specification Mandatory list of specification attributes where the criteria shall be search.
     * @return Found attribute or null.
     */
    static public Attribute findSpecificationByName(String criteriaName, Collection<Attribute> specification) {
        Attribute found = null;
        if ((criteriaName != null && !criteriaName.isEmpty()) && specification != null) {
            // Possible search execution
            for (Attribute at : specification) {
                if (at != null && at.name() != null && at.name().equalsIgnoreCase(criteriaName)) {
                    found = at;
                    break; // Stop search
                }
            }
        }
        return found;
    }
}
