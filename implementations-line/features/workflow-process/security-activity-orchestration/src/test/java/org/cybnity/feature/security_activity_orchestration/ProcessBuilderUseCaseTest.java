package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashSet;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.Test;

/**
 * Unit test of process builder component behaviors regarding instantiation of
 * process instance.
 * 
 * @author olivier
 *
 */
public class ProcessBuilderUseCaseTest extends AbstractProcessEvaluation {

	/**
	 * Check that build of process instance with minimum name defined is delivered
	 * with success, including basic properties.
	 */
	@Test
	public void givenNamedProcess_whenBuild_thenSuccessInstanceDelivery() throws Exception {
		// Define a described process
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());
		ProcessBuilder builder = ProcessBuilder.instance(processIds, /* predecessor */ company, processDesc.name());
		builder.build();
		Process p = builder.getResult();
		// Verify the instance conformity
		ProcessBuilder.validateConformity(p);

		assertNotNull(p);
		assertNotNull(p.name());
		assertNotNull(p.description());
		assertNotNull(p.identified());
		assertNotNull(p.immutable());
		assertNotNull(p.occurredAt());
		assertEquals(company, p.parent(), "Invalid predecessor defined as prerequisite parent of the process!");
		assertNotNull(p.root());
		assertNotNull(p.versionHash());
		// Check that default not active state is defined
		assertNotNull(p.activation());
		assertFalse(p.activation().isActive()); // not active by default
		// Check that default completion state is defined and equals to zero percentage
		assertNotNull(p.completion());
		assertEquals(Float.valueOf(0.0f), p.completion().percentage());
	}

	/**
	 * Check that default process state is replaced by a custom status during the
	 * build of process instance.
	 */
	@Test
	public void givenCustomActivation_whenBuild_thenSuccessDefinedAsDefault() throws Exception {
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());
		Float customCompletionRate = Float.valueOf(0.36f);
		Boolean customActivation = Boolean.TRUE;
		ProcessBuilder builder = ProcessBuilder.instance(processIds, /* predecessor */ company, processDesc.name())
				.withActivation(customActivation).withCompletion("customCompletionName", customCompletionRate);
		builder.build();
		Process p = builder.getResult();
		// Check that custom values had been taken
		assertEquals(customActivation, p.activation().isActive());
		assertEquals(customCompletionRate, p.completion().percentage());
	}

	/**
	 * Check that a process based on a template referenced as origin structure,
	 * include a description identifying the original source template reference.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenProcessTemplate_whenBuild_thenProcessDescriptionIncludingTemplateReference() throws Exception {
		// Define template domain entity reference
		DomainEntity templateDomainEntityRef = TestSampleFactory.createIdentity();
		// Define a process identifiers
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());
		ProcessBuilder builder = ProcessBuilder.instance(processIds, /* predecessor */ company, processDesc.name())
				.withTemplateEntityReference(templateDomainEntityRef.reference());
		builder.build();
		Process p = builder.getResult();
		// Verify the instance conformity
		ProcessBuilder.validateConformity(p);
		// Check template reference saved
		assertEquals(templateDomainEntityRef, p.description().templateEntityRef().getEntity());
	}

	/**
	 * Check that detection of null description is supported during the build of
	 * process instance.
	 */
	@Test
	public void givenInvalidNullProcessName_whenBuild_thenIllegalArgumentExceptionThrown() {
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());

		assertThrows(IllegalArgumentException.class, () -> {
			// Check that unknown name is refused
			ProcessBuilder.instance(processIds,
					/* predecessor of the process is the existing tenant domain entity */ company,
					/* Undefined process name */ null);
		});
	}

	/**
	 * Check that detection of minimum set of description valued attributes is
	 * supported during the build of process instance.
	 */
	@Test
	public void givenInvalidEmptyProcessName_whenBuild_thenIllegalArgumentExceptionThrown() {
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());

		assertThrows(IllegalArgumentException.class, () -> {
			// Check that unknown name is refused
			ProcessBuilder.instance(processIds,
					/* predecessor of the process is the existing tenant domain entity */ company,
					/* Empty process name */ "");
		});
	}

}
