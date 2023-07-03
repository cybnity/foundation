package org.cybnity.feature.defense_template.domain.model;

import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Responsible of coordination regarding the build of several types of
 * processes.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ProcessBuildDirector {

	public void createProcess(IProcessBuilder builder) throws IllegalArgumentException {
		// Set customized names and contents of the process to build
		// as configuration

		// return the specialized class type of NISTRMF
	}
}
