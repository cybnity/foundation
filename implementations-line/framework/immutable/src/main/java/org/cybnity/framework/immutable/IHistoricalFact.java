package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A historical fact that can contain only identifying information(s) or that is
 * not identifiable (e.g suspect and not planed).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface IHistoricalFact extends Unmodifiable, IVersionable, Serializable {

    /**
     * A time when the fact was created or observed.
     * 
     * @return An immutable time.
     */
    OffsetDateTime occurredAt();

}
