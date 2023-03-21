package org.cybnity.framework.immutable;

import java.time.OffsetDateTime;

/**
 * Utility class regarding evaluation reusable capabilities.
 * 
 * @author olivier
 *
 */
public class Evaluations {

    /**
     * Evaluate equality of two historical facts based on their identifiers when
     * existing.
     * 
     * @param fact      To compare.
     * @param otherFact To compare with.
     * @return True if this fact is based on the same identifier(s) as the fact
     *         argument; false otherwise (e.g when one fact is not identifiable).
     * @throws ImmutabilityException If impossible creation of immutable version of
     *                               identifier.
     */
    static public boolean isIdentifiedEquals(IdentifiableFact fact, IdentifiableFact otherFact)
	    throws ImmutabilityException {
	if (fact != null && otherFact != null) {
	    // Check if reference are equals
	    if (fact == otherFact)
		return true;
	    // Check equality based on identifier
	    Identifier id1 = fact.identified();
	    Identifier id2 = otherFact.identified();
	    if (id1 != null && id2 != null) {
		// Compare equality based on each instance's identifier (unique or based on
		// identifying informations combination)
		return id1.equals(id2);
	    } else {
		// Not possible to compare in a sure way about their equality
	    }
	}
	return false;
    }

    /**
     * Evaluate equality of two date without nanosecond considered. This method use
     * the epoch second evaluation for identity the equality.
     * 
     * @param aDate   To compare.
     * @param another To compare with.
     * @return True if dates are equals (ignoring their nanosecond precision). Else
     *         return false.
     */
    static public boolean isEpochSecondEquals(OffsetDateTime aDate, OffsetDateTime another) {
	if (aDate != null && another != null) {
	    // Compare the time without the nanosecond consideration
	    return Long.valueOf(aDate.toEpochSecond()).equals(Long.valueOf(another.toEpochSecond()));
	}
	return false;
    }
}
