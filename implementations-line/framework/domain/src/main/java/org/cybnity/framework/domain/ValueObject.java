package org.cybnity.framework.domain;

import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Measures, quantifies or describes a thing in a domain that can be maintained
 * as immutable. Represents a conceptual whole by composing related attributes
 * as an integral unit. It is completely replaceable when the measurement or
 * description changes, that can be compared with others using Value equality or
 * hash code value.
 * <p>
 * A Value Object doesn't have any identity, it is entirely identified by its
 * value and is immutability.
 * <p>
 * A value object is a simple object whose equality isn't based on identity.
 * Wraps one or more values or value objects that provides evidence of the
 * correctness of these values.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class ValueObject<T> implements Unmodifiable {

    /**
     * Implement value equality redefined method that ensure the functional
     * comparison of the instance about compared types of both objects and their
     * attributes.
     */
    @Override
    public boolean equals(Object obj) {
        boolean equalsObject = false;
        if (obj == this)
            return true;
        if (obj != null && this.getClass() == obj.getClass()) {
            // Compare all the functional contributors, so comparison based on hashcode
            equalsObject = (obj.hashCode() == this.hashCode());
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
                    hashCodeValue += s.hashCode();
                }
            }
        } else {
            // Keep standard hashcode value calculation default implementation
            hashCodeValue = super.hashCode();
        }
        return hashCodeValue;
    }

    /**
     * This method get all values that are functionally equal also produce equal
     * hash code value. This method is called by default hashCode() method of this
     * ValueObject instance and shall provide the list of values contributing to
     * define the unicity of this instance (e.g used as functional comparison).
     *
     * @return The unique functional values used to identify uniquely this instance.
     * Or empty array.
     */
    public abstract String[] valueHashCodeContributors();

}
