package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Represent a means to determine how the uniqueness of a subject (e.g a fact)
 * regarding one or several informations (e.g combination of multiple properties
 * defining an object).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public interface IUniqueness {

    /**
     * Get the field(s) which define the uniqueness of this object. Uniqueness can
     * be based on only one object's field (e.g an Identifier) or can be based on
     * the combination of several fields (e.g an Identifier, combined to another
     * Identifier of reference external object, and combined to another String field
     * regarding logical name of the instance).
     * 
     * @return A set of fields combination representing the unique version of this
     *         object.
     */
    Set<Field> basedOn();
}
