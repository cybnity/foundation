package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
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
     * Get the manageable version of this type of event allowing multiple versions
     * of a domain event operated concurrently by a system.
     */
    public Long versionUID();
}
