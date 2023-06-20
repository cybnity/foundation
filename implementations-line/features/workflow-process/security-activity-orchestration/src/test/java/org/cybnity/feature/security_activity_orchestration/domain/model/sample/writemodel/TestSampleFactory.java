package org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel;

import java.util.HashMap;
import java.util.UUID;

import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.HistoryState;

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
