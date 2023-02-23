package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * To mitigate accidental deletion, two methods are employed: confirmation, or
 * restoration of deleted entity. Restoration may begin in a recycle bin that
 * lists all of the deleted entities. Or it may only available immediately after
 * deletion in the form of undo. When the Deletion Fact does not have an
 * additional identifier (e.g timestamp of occured fact), then the entity can
 * only be deleted and restored once.
 * 
 * A restoration fact references a prior deletion.
 * 
 * By convention, it appends the word Restoration to the name of the entity. The
 * deletion has an extra identifier, usually a timestamp but has no extra
 * identifiers.
 * 
 * Related patterns: Restore is an extension of the Delete pattern.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface RestorationFact extends HistoricalFact {

    /**
     * Predecessor deletion fact.
     * 
     * @return Predecessor deletion fact that could be restored.
     */
    public DeletionFact deletion();

}
