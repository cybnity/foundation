package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.util.Collection;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a providing contract regarding the description of a state (e.g a
 * process step) based on a collection of attributes.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface IState {

	/**
	 * Get the properties regarding this object.
	 * 
	 * @return A set of properties or null.
	 */
	public Collection<Attribute> properties();
}
