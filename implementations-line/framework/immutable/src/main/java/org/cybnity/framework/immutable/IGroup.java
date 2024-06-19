package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Logical group regarding a type of entity.
 * 
 * Related pattern: usable in a Membership context
 * ({@link org.cybnity.framework.immutable.Membership}) regarding some members
 * to organize logically.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface IGroup extends IHistoricalFact {
    /**
     * Location-independent unique identifier of this fact.
     * 
     * @return Unique based identifier, or derived identifier based on the multiple
     *         identification information combined from identifiers() method.
     */
    Identifier identified();

}
