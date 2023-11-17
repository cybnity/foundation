package org.cybnity.framework.domain;

import org.cybnity.framework.immutable.IdentifiableFact;

/**
 * Utility class providing a hashing standardized capability.
 */
public class EventHashingCapability {

    /**
     * Calculate and return a hash code regarding a fact event.
     *
     * @param fact Mandatory fact.
     * @return Calculated hash code.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public int getHashCode(IdentifiableFact fact) throws IllegalArgumentException {
        if (fact == null) throw new IllegalArgumentException("Fact parameter is required!");
        // Read the contribution values of functional equality regarding this instance
        String[] functionalValues = fact.valueHashCodeContributors();
        int hashCodeValue = +(169065 * 179);
        if (functionalValues != null && functionalValues.length > 0) {
            for (String s : functionalValues) {
                if (s != null) {
                    hashCodeValue += s.hashCode();
                }
            }
        } else {
            // Keep standard hashcode value calculation default implementation
            hashCodeValue = super.hashCode();
        }
        return hashCodeValue;
    }
}
