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
     *         identification informations combined from {@link#identifiers()}.
     */
    public abstract Identifier identified();
}
