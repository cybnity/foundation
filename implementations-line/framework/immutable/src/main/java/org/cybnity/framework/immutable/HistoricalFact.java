package org.cybnity.framework.immutable;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * A historical fact that can contain only identifying information(s) or that is
 * not identifiable (e.g suspect and not planed).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface HistoricalFact extends Unmodifiable, Serializable {

    /**
     * A time when the fact was created or observed.
     * 
     * @return An immutable time.
     */
    public OffsetDateTime occurredAt();

}
