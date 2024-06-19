package org.cybnity.framework.domain;

import java.util.Collection;

/**
 * Contract regarding the capability to be described by one or several type of
 * attributes. For example, allow a fact to be specialized in a generic way (e.g
 * without specialization implemented by extend) with inclusion of specific
 * additional attributes (e.g event type name dynamically defined by
 * customizable template).
 *
 * @author olivier
 */
public interface IDescribed {

    /**
     * Get attributes set representing a definition of the subject.
     *
     * @return Attributes immutable collection or null.
     */
    Collection<Attribute> specification();

    /**
     * Add an attribute contributing to the definition of the subject.
     *
     * @param specificationCriteria A valid description attribute.
     * @return True when criteria have been added on the subject description. Else
     * return false (e.g if same named attribute is already existing and
     * can't be modified by the new version. Return false when parameter is
     * null).
     */
    boolean appendSpecification(Attribute specificationCriteria);

    /**
     * Get the type specification of the subject.
     *
     * @return A type or null.
     */
    Attribute type();

}
