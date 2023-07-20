package org.cybnity.feature.defense_template.domain.model.sample.writemodel;

import java.util.UUID;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;

/**
 * Factory of domain object samples usable for unit test.
 */
public class TestSampleFactory {

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
