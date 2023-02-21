package org.cybnity.framework.immutable;

import java.util.Collection;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * In historical model, it's not strictly necessary to identify one predecessor
 * as the owner of a successor. The Ownership pattern documents a strict
 * parent-child relationship between a successor and one of its predecessors.
 * Each child belongs to only one parent. The pattern is base on convention and
 * not on structured relation (e.g based on structural hierarchy of entities).
 * Ownership does not apply to a collection of individual entities that are
 * later grouped after they are constructed. The Ownership pattern encourages
 * multi-tenancy. When ownership needs to be transferred, consider the
 * Membership pattern instead.
 * 
 * The Ownership pattern is a special case of the Entity pattern, where the
 * entity's identifiers include the identity of an owner.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface Ownership {

    /**
     * Query for child facts given a parent.
     * 
     * @param parent Predecessor of children to find.
     * @return List of child facts (all successors that have an equals parent
     *         identifier), or null.
     */
    public Collection<ChildFact> childrenOfParent(HistoricalFact parent);

}
