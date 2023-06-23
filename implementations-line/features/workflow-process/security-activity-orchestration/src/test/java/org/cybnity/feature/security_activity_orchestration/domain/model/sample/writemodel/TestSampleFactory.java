package org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.Step;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Factory of domain object samples usable for unit test.
 */
public class TestSampleFactory {

	/**
	 * 
	 * @param processIdentity
	 * @param processName
	 * @return A description in HistoryState.COMMITTED state, including a name
	 *         attribute.
	 */
	public static ProcessDescriptor createProcessDescription(DomainEntity processIdentity, String processName) {
		HashMap<String, Object> descriptorAttributes = new HashMap<>();
		descriptorAttributes.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), processName);
		return new ProcessDescriptor(processIdentity, descriptorAttributes, HistoryState.COMMITTED);
	}

	public static DomainEntity createIdentity() {
		return new DomainEntity(
				new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
	}

	/**
	 * Create a sample of staging (NIST RMF example) including valid steps attached
	 * to the process as property owner.
	 * 
	 * @param processIdentity Mandatory property owner.
	 * @return A staging containing list of steps containing more than one minimum
	 *         steps.
	 * @throws ImmutabilityException Problem of identity parameter cloning.
	 */
	public static Staging createStaging(Entity processIdentity) throws ImmutabilityException {
		List<Step> steps = new LinkedList<>();
		HashMap<String, Object> propertyCurrentValue = new HashMap<>();
		// Create a step without handling and that does not include subtask
		String[] stepNames = { "PREPARE", "CATEGORIZE", "SELECT", "IMPLEMENT", "ASSESS", "AUTHORIZE", "MONITOR" };

		for (String name : stepNames) {
			steps.add(
					new Step(processIdentity.reference(), name, /*
																 * processor allowing the step to interpret the commands
																 * handling relative to the step and/or to its subtasks
																 */ null));
		}

		propertyCurrentValue.put(PropertyAttributeKey.Steps.name(), steps);
		return new Staging(processIdentity, propertyCurrentValue, HistoryState.COMMITTED);
	}

	/**
	 * 
	 * @param companyIdentity When null, generate sample identity for organization
	 *                        returned.
	 * @return
	 */
	public static Organization createOrganization(DomainEntity companyIdentity) {
		if (companyIdentity == null) {
			companyIdentity = new DomainEntity(
					new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
		}
		return new Organization(companyIdentity.identified());
	}
}
