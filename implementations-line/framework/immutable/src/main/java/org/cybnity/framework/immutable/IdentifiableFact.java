package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Identification contract regarding an immutable object.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface IdentifiableFact {

    /**
     * Location-independent unique identifier of this fact.
     * 
     * @return Unique based identifier, or derived identifier based on the multiple
     *         identification information combined from {@link#identifiers()}.
     * @throws ImmutabilityException When problem to create immutable copy of this
     *                               fact.
     */
    public Identifier identified() throws ImmutabilityException;

    /**
     * This method has the same contract as valueEquality() method in that all
     * values that are functionally equal also produce equal hash code value. This
     * method is called by default hashCode() method of this ValueObject instance
     * and shall provide the list of values contributing to define the unit of
     * this instance (e.g also used for valueEquality() comparison).
     * 
     * @return The unique functional values used to identify uniquely this instance.
     *         Or empty array.
     */
    public String[] valueHashCodeContributors();
}
