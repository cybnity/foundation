package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Define the possible state of decision taken by a user regarding a previous
 * property value concurrently changed and that a user defined which version
 * shall be historized as official new current version, as archived version
 * because used during a merge between several anterior changes request.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public enum HistoryState {
    ARCHIVED, MERGED, COMMITTED, CANCELLED
}
