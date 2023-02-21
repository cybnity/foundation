package org.cybnity.framework.immutable;

import java.time.OffsetDateTime;
import java.util.Collection;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * A historical fact contains only identifying information(s)
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface HistoricalFact extends Unmodifiable {

    /**
     * A fact shall use location-independent identity. It cannot use
     * auto-incremented IDs, URLS, or any other location-dependent identifier.
     * 
     * @return One or multiple identification informations.
     */
    public Collection<Identifier> identifiers();

    /**
     * Location-independent unique identifier of this fact.
     * 
     * @return Unique based identifier, or derived identifier based on the multiple
     *         identification informations combined from {@link#identifiers()}.
     */
    public Identifier identified();

    /**
     * A time when the fact was created or observed.
     * 
     * @return An immutable time.
     */
    public OffsetDateTime occurredAt();
}
