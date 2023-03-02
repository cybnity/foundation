package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a member of a logical group into a membership relation.
 * 
 * Related pattern: usable in a Membership context
 * ({@link org.cybnity.framework.immutable.Membership}) regarding types of
 * members that can be grouped.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface IMember extends IHistoricalFact {

    /**
     * Location-independent unique identifier of this fact.
     * 
     * @return Unique based identifier, or derived identifier based on the multiple
     *         identification informations combined from {@link#identifiers()}.
     */
    public Identifier identified();

}
