package org.cybnity.feature.defense_template.domain.model.process;

import java.util.LinkedHashSet;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessBuilder;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Builder implementation class creating NIST RMF process instance that is
 * customized (as template) according to the NIST RMF standard;
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class NISTRMFProcessBuilder extends ProcessBuilder {

	/**
	 * Default Constructor of process from unique or multiple identifiers to use for
	 * the process identity.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @param processName        Mandatory name of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	private NISTRMFProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName) throws IllegalArgumentException {
		super(processIdentifiers, processParent, processName);
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity Mandatory identity of the process to build.
	 * @param processParent   Mandatory predecessor of the process to build.
	 * @param processName     Mandatory name of the process to build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static ProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName) throws IllegalArgumentException {
		return new NISTRMFProcessBuilder(processIdentifiers, processParent, processName);
	}

	/**
	 * Creation of the process instance according to the attributes defined into
	 * this builder component.
	 * 
	 * @throws ImmutabilityException    When impossible use of immutable version of
	 *                                  a build content.
	 * @throws IllegalArgumentException when mandatory content is missing to execute
	 *                                  the build process and shall be completed
	 *                                  before to call this method.
	 */
	public void build() throws ImmutabilityException, IllegalArgumentException {
		// Create standard basis instance of process
		super.build();

		// Get the built instance to be customized
		Process instance = super.getResult();

		// Customize the instance regarding its structure (e.g step), behavior (e.g
		// states machine and change events promoted)

		// TODO coder preparation NIST RFM template customisé

		// Update the built instance as ready to return
		setResult(instance);
	}
}
