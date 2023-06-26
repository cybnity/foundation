package org.cybnity.framework.immutable;

import java.io.Serializable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Identifying information (e.g natural key, GUID, time stamp, or some
 * combination of those and other location-independent identifiers).
 * 
 * A location-independent identity can be generated from any system node, is
 * immutable and can be compared.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface Identifier extends Unmodifiable, Serializable {

    /**
     * A name that identify this identity instance (e.g type of class implementing
     * this identifier category).
     * 
     * @return A nature of this identifier. For example, java.lang.String regarding
     *         a value chain that represent the identity; or java.lang.Long
     *         regarding a number. For example equals to "sku" regarding a stock
     *         keeping unit.
     */
    public String name();

    /**
     * The value regarding this identity.
     * 
     * @return A identification value.
     */
    public Serializable value();

    /**
     * This method has the same contract as valueEquality() method in that all
     * values that are functionally equal also produce equal hash code value. This
     * method is called by default hashCode() method of this ValueObject instance
     * and shall provide the list of values contributing to define the unicity of
     * this instance (e.g also used for valueEquality() comparison).
     * 
     * @return The unique functional values used to idenfity uniquely this instance.
     *         Or empty array.
     */
    public String[] valueHashCodeContributors();
}
