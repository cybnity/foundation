package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.Organization;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.OrganizationDescriptor.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.Tenant;
import org.cybnity.framework.domain.model.TenantDescriptor;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.HistoryState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.UUID;

/**
 * Shared test class providing common process sample contents reused by child
 * test case.
 * 
 * @author olivier
 *
 */
public class AbstractProcessEvaluation {

	protected Tenant tenant;
	protected TenantDescriptor organizationDesc;
	protected DomainEntity companyEntity;
	protected Organization company;
	protected ProcessDescriptor processDesc;
	protected DomainEntity processIdentity;

	@BeforeEach
	public void initSamples() throws Exception {
		// Create named tenant as owner of the templated process which is a company
		// tenant
		companyEntity = new DomainEntity(
				new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
		company = TestSampleFactory.createOrganization(companyEntity);
		tenant = new Tenant(company, /*
										 * Simulate auto-assigned parent identifier without extension of the child id
										 * generation based on identifiers and minimum quantity of length
										 */ null, Boolean.TRUE /* active tenant */);

		HashMap<String, Object> organisationAttr = new HashMap<String, Object>();
		organisationAttr.put(PropertyAttributeKey.Name.name(), "CYBNITY France");
		organizationDesc = new TenantDescriptor(company, organisationAttr, HistoryState.COMMITTED);
		tenant.setLabel(organizationDesc);
		processIdentity = TestSampleFactory.createIdentity();
		processDesc = TestSampleFactory.createProcessDescription(processIdentity, "NIST RMF");
	}

	@AfterEach
	public void cleanSample() throws Exception {
		tenant = null;
		company = null;
		organizationDesc = null;
		companyEntity = null;
		processDesc = null;
		processIdentity = null;
	}
}
