package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;

import java.util.Collection;

/**
 * Utility class providing capabilities supporting the management of
 * predecessors and dependent contents.
 *
 * @author olivier
 */
public class Predecessors {

    /**
     * Generate a combined identifier based on an original entity identifier (if
     * known) and predecessors identifiers.
     *
     * @param predecessor     Mandatory base predecessor.
     * @param childOriginalId Optional identifier of a child that identifier need to
     *                        be included into the generated combined identifier.
     * @return A combined identifier based on the predecessors identifiers and
     * optional the child original identifier.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
            throws IllegalArgumentException {
        if (predecessor == null)
            throw new IllegalArgumentException(
                    "Mandatory predecessor parameter is required to generate a child identifier!");
        // Read identifier relative to predecessor
        String parentIdValue = IdentifierStringBased.build(predecessor.identifiers()).value().toString();

        StringBuilder value = new StringBuilder();
        boolean isParentIdAlreadyIncluded = false;
        // Manage inclusion of child origin identifier
        if (childOriginalId != null && childOriginalId.value() != null) {
            String originId = childOriginalId.value().toString();
            value.append(originId);

            // FILTERING : quality control avoiding multiple usage of predecessor id value in the final build value
            // when already included into the original identifier of child
            if (!originId.contains(parentIdValue)) {
                // Add parent identifier extension
                value.append("_");// add logical separator (e.g as convention of multiples identifiers combination)
            } else {
                // parent is already member of child identifier AND SHALL NOT BE ADDING AGAIN (DE-DUPLICATION RULE)
                isParentIdAlreadyIncluded = true;
            }
        }
        if (!isParentIdAlreadyIncluded)
            // Use predecessor's identifying information(s) and add them to global original
            // identifier
            value.append(parentIdValue);

        /*
         * for (Identifier parentId : predecessor.identifiers()) {
         * value.append(parentId.value().toString()); }
         */

        // Define name of identity key
        String childIdName = null;
        if (childOriginalId != null)
            childIdName = childOriginalId.name(); // Same name of identifier for the child as its parent
        if (childIdName == null || childIdName.isEmpty()) {
            // Define a specific new convention name
            childIdName = BaseConstants.IDENTIFIER_ID.name();
        }

        // Create new identifier from origin joined with parent identifying
        // information
        return new IdentifierStringBased(childIdName, value.toString());
    }

    /**
     * Generate a combined identifier based on a prior entities.
     *
     * @param predecessor      Mandatory base predecessor.
     * @param childOriginalIds Optional identifier of a child that identifier need to
     *                         be included into the generated combined identifier.
     * @return A combined identifier based on the predecessors identifiers and
     * optional the child original identifier.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static Identifier generateIdentifierPredecessorBased(Entity predecessor, Collection<Identifier> childOriginalIds)
            throws IllegalArgumentException {
        return Predecessors.generateIdentifierPredecessorBased(predecessor,
                IdentifierStringBased.build(childOriginalIds));
    }
}
