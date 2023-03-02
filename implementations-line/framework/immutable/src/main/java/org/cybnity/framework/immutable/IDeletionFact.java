package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Historical facts that can be neither modifier nor destroyed, the Delete
 * pattern of historical facts represents a deletion fact as the creation of a
 * new fact. A deletion fact taks the entity that it deletes as a predecessor.
 * If the deletion fact has no identifiers to distinguish it from other
 * deletions of the same entity, then the entity can only be deleted once. To
 * allow future restoring of the entity, a distinguishing identifier and a
 * timestamp are added.
 * 
 * It represent the deletion of an entity.
 * 
 * By convention, the name of the deletion fact is Deletion appended to the name
 * of the entity (e.g <<EntityName>>Deletion).
 * 
 * Related pattern: when deletion should be reversible, consider using the
 * Restore pattern.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface IDeletionFact extends IHistoricalFact {

    /**
     * Predecessor fact that was deleted through this deletion act.
     * 
     * @return Predecessor fact deleted.
     */
    public Entity deleted();
}
