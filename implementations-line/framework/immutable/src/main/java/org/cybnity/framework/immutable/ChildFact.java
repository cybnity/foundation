package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Historical fact referencing (e.g as an aggregate) a parent as predecessor.
 * Low-cost way to documenting the desired owner relationship. The identity of
 * the parent is part of the identity of this child. Because prerequisites are
 * immutable, a child cannot be moved to another parent. Ownership relation
 * between a child and a parent is non-transferable. The parent must exist
 * before the child can be created.
 * 
 * The Ownership pattern (supporting a Child Fact existence) is a special case
 * of the Entity pattern, where the entity's identifiers include the identity of
 * an owner.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class ChildFact implements HistoricalFact {

    /**
     * Predecessor fact of this child entity's identifier. The identity of a root
     * owner tends to become part of the identities of most other entities durign
     * their creation.
     * 
     * @return A predecessor of the child.
     */
    public abstract HistoricalFact parent();

}
