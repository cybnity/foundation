package org.cybnity.feature.defense_template.domain.model;

import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.framework.immutable.ImmutabilityException;
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

	private IProcessBuilder builder;

	public ProcessBuildDirector(IProcessBuilder builder) throws IllegalArgumentException {
		// Initialize the default builder
		change(builder);
	}

	/**
	 * Change the builder that director shall use for process instance build.
	 * 
	 * @param builder Mandatory builder instance.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 */
	public void change(IProcessBuilder builder) throws IllegalArgumentException {
		if (builder == null)
			throw new IllegalArgumentException("Builder parameter is required!");
		this.builder = builder;
	}

	/**
	 * 
	 * @throws ImmutabilityException When impossible
	 */
	public void make() throws ImmutabilityException {
		// Execute the unique or multiple steps of build managed by the builder
		this.builder.build();
	}
}
