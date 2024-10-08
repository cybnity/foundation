package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a structural versioning of a fact type.
 * 
 * Several versions of a same event type (e.g gain/lose of a property in its
 * latest version, internal property changes regarding property type of values
 * range) can be supported by a system. An event requiring a operating of its
 * under several version shall declare this versioning capability to manage an
 * imperative version number.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IVersionable {

    /**
     * Get the manageable version of this type of event structure allowing multiple
     * versions of a fact operated concurrently by a system.
     * @return Value for hash.
     */
    String versionHash();
}
