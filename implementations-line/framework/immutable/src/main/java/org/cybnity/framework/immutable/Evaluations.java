package org.cybnity.framework.immutable;

/**
 * Utility class regarding evaluation reusable capabilities.
 * 
 * @author olivier
 *
 */
public class Evaluations {

    /**
     * Evaluate equality of two historical facts based on their identifiers.
     * 
     * @param fact      To compare.
     * @param otherFact To compare.
     * @return True if this fact is based on the same identifier(s) as the fact
     *         argument; false otherwise.
     */
    static public boolean isIdentifiedEquals(HistoricalFact fact, HistoricalFact otherFact) {
	if (fact != null && otherFact != null) {
	    // Compare equality based on each instance's identifier (unique or based on
	    // identifying informations combination)
	    return fact.identified().equals(otherFact.identified());
	}
	return false;
    }
}
