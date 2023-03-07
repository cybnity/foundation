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
     * Redefined hash code calculation method which include the functional contents
     * hash code values into the returned number.
     */
    @Override
    public int hashCode() {
	// Read the contribution values of functional equality regarding this instance
	String[] functionalValues = valueHashCodeContributors();
	int hashCodeValue = +(169065 * 179);
	if (functionalValues != null && functionalValues.length > 0) {
	    for (String s : functionalValues) {
		if (s != null) {
		    hashCodeValue = +s.hashCode();
		}
	    }
	} else {
	    // Keep standard hashcode value calculation default implementation
	    hashCodeValue = super.hashCode();
	}
	return hashCodeValue;
    }

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
    public abstract String[] valueHashCodeContributors();

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
