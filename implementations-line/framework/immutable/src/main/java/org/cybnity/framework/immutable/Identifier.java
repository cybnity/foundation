package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Identifying information (e.g natural key, GUID, timestamp, or some
 * combination of those and other location-independent identifiers).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface Identifier {

    /**
     * A name that identify this identity instance.
     * 
     * @return A name. For example equals to "sku" regarding a stock keeping unit.
     */
    public String identifierName();

    /**
     * The class type of this identity .
     * 
     * @return A nature of this identifier. For example, java.lang.String regarding
     *         a value chain that represent the identity; or java.lang.Long
     *         regarding a number.
     */
    public Class<?> value();
}
