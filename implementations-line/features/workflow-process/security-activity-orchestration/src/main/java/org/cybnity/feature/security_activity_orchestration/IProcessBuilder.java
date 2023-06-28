package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Build services contract regarding a type of process.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface IProcessBuilder {
	/**
	 * Creation of the process instance according to the attributes defined into
	 * this builder component.
	 * 
	 * @return A new instance of process.
	 * @throws ImmutabilityException    When impossible use of immutable version of
	 *                                  a build content.
	 * @throws IllegalArgumentException when mandatory content is missing to execute
	 *                                  the build process and shall be completed
	 *                                  before to call this method.
	 */
	public Process build() throws ImmutabilityException, IllegalArgumentException;
}
