package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Measures, quantifies or describes a thing in a domain that can be maintained
 * as immutable. Represents a conceptual whole by composing related attributes
 * as an integral unit.. It is completely replaceable when the measurement or
 * description changes, that can be compared with others using Value equality.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class ValueObject<T> {

    /**
     * Implement value equality redefinition with automatic call to
     * valueEquality(ValueObject obj) included.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
	boolean equalsObject = false;
	if (obj == this)
	    return true;
	if (obj != null && this.getClass() == obj.getClass()) {
	    // Call value equality
	    equalsObject = this.valueEquality((ValueObject<T>) obj);
	}
	return equalsObject;
    }

    /**
     * Called by equals(Object obj) method that ensure the functional comparison of
     * the instance about compared types of both objects and then their attributes.
     * 
     * @param obj Compared instance.
     * @return False by default. When both the types and their attributes are equal,
     *         the Values are considered equal.
     */
    protected abstract boolean valueEquality(ValueObject<T> obj);
}
