package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * A contract allowing to manage reference regarding a subject.
 * 
 * @author olivier
 *
 */
public interface IReferenceable {

	/**
	 * Get a reference regarding this subject.
	 * 
	 * @return A reference or null when the subject is not referenceable by a valid
	 *         identifier.
	 * @throws ImmutabilityException When impossible reference creation (e.g caused
	 *                               by serialization or instance cloning problem).
	 */
	@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
    EntityReference reference() throws ImmutabilityException;

}
