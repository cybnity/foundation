package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.UUID;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.Organization;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.OrganizationDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.OrganizationDescriptor.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.Tenant;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.HistoryState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of Process behaviors regarding its instantiation and supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class ProcessUseCaseTest {

	private Tenant tenant;
	private OrganizationDescriptor organizationDesc;
	private DomainEntity companyEntity;
	private Organization company;
	private ProcessDescriptor processDesc;

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
		organizationDesc = new OrganizationDescriptor(company, organisationAttr, HistoryState.COMMITTED);
		tenant.setOrganization(organizationDesc);
		processDesc = TestSampleFactory.createProcessDescription(TestSampleFactory.createIdentity(), "NIST RMF");

	}

	@AfterEach
	public void cleanSample() throws Exception {
		tenant = null;
		company = null;
		organizationDesc = null;
		companyEntity = null;
		processDesc = null;
	}

	/**
	 * Check that instantiation with minimum name defined is created with success,
	 * including basic properties.
	 */
	@Test
	public void givenNamedProcess_whenConstructor_thenSuccessInstantiation() throws Exception {
		// Define a described process
		Process p = new Process(/* predecessor */ company, TestSampleFactory.createIdentity().identified(),
				processDesc);
		assertNotNull(p);
		assertNotNull(p.name());
		assertNotNull(p.description());
		assertNotNull(p.identified());
		assertNotNull(p.immutable());
		assertNotNull(p.occurredAt());
		assertEquals(company, p.parent(), "Invalid predecessor defined as prerequisite parent of the process!");
		assertNotNull(p.root());
		assertNotNull(p.versionHash());
	}

	/**
	 * Check that detection of null description is supported during the
	 * instantiation of process.
	 */
	@Test
	public void givenInvalidNullDescription_whenConstructor_thenIllegalArgumentExceptionThrown() {
		DomainEntity processEntity = TestSampleFactory.createIdentity();

		// Check that unknown description is refused
		assertThrows(IllegalArgumentException.class, () -> {
			new Process(/* predecessor of the process is the existing tenant domain entity */company,
					processEntity.identified(), null);
		});
	}

	/**
	 * Check that detection of minimum set of description attributes is supported
	 * during the instantiation of process.
	 */
	@Test
	public void givenInvalidMinimumDescription_whenConstructor_thenIllegalArgumentExceptionThrown() {
		DomainEntity processEntity = TestSampleFactory.createIdentity();
		ProcessDescriptor invalidDescription = TestSampleFactory
				.createProcessDescription(TestSampleFactory.createIdentity(), /* undefined */ null);

		// Check the description which not include minimum attributes required is not
		// accepted by constructor (conformity failure)
		assertThrows(IllegalArgumentException.class, () -> {
			new Process(/* predecessor of the process is the existing tenant domain entity */tenant.parent(),
					processEntity.identified(), invalidDescription);
		});
	}

	/**
	 * Check that detection of minimum set of description valued attributes is
	 * supported during the instantiation of process.
	 */
	@Test
	public void givenInvalidEmptyDescription_whenConstructor_thenIllegalArgumentExceptionThrown() {
		DomainEntity processEntity = TestSampleFactory.createIdentity();
		ProcessDescriptor invalidDescription = TestSampleFactory
				.createProcessDescription(TestSampleFactory.createIdentity(), /* empty name */ "");

		// Check the description which include minimum attribute but that is empty is
		// not accepted by constructor (conformity failure)
		assertThrows(IllegalArgumentException.class, () -> {
			new Process(/* predecessor of the process is the existing tenant domain entity */company,
					processEntity.identified(), invalidDescription);
		});
	}

}
