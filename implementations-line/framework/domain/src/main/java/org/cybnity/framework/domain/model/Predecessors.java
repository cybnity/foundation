package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;

/**
 * Utility class providing capabilities supporting the management of
 * predecessors and dependent contents.
 * 
 * @author olivier
 *
 */
public class Predecessors {

    /**
     * Generate a combinated identifier based on a prior entities.
     * 
     * @param predecessor     Mandatory base predecessor.
     * @param childOriginalId Optional identifier of a child that identifier need to
     *                        be included into the generated combinated identifier.
     * @return A combinated identifier based on the predecessors identifiers and
     *         optional the child original identifier.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
	    throws IllegalArgumentException {
	if (predecessor == null)
	    throw new IllegalArgumentException(
		    "Mandatory predecessor parameter is required to generate a child identifier!");
	StringBuffer value = new StringBuffer();
	if (childOriginalId != null && childOriginalId.value() != null) {
	    value.append(childOriginalId.value().toString());
	    value.append("_");// add logical separator (e.g as convention of multiples identifiers
			      // combination)
	}
	// Use predecessor identifying information(s)
	for (Identifier parentId : predecessor.identifiers()) {
	    value.append(parentId.value().toString());
	}
	String childIdName = null;
	if (childOriginalId != null)
	    childIdName = childOriginalId.name(); // Same name of identifier for the child as its parent
	if (childIdName == null || childIdName.equals("")) {
	    // Define a specific new convention name
	    childIdName = BaseConstants.IDENTIFIER_ID.name();
	}

	// Create new identifier from origin concatened with parent identifying
	// information
	return new IdentifierStringBased(childIdName, value.toString());
    }
}
