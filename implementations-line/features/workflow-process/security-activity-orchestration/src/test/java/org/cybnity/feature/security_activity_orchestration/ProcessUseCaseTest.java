package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of Process behaviors regarding its instantiation and supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class ProcessUseCaseTest extends AbstractProcessEvaluation {

	/**
	 * Check that instantiation with minimum name defined is created with success,
	 * including basic properties.
	 */
	@Test
	public void givenNamedProcess_whenConstructor_thenSuccessInstantiation() throws Exception {
		// Define a described process
		Process p = new Process(/* predecessor */ company, processIdentity.identified(),
				new HashMap<>(processDesc.currentValue()));
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
		assertFalse(p.activation().isActive());// Not active by default
		// Check that default completion state is defined and equals to zero percentage
		assertNotNull(p.completion());
		assertEquals(Float.valueOf(0.0f), p.completion().percentage());
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
		ProcessDescriptor invalidDescription = TestSampleFactory.createProcessDescription(processEntity,
				/* undefined */ null);

		// Check the description which not include minimum attributes required is not
		// accepted by constructor (conformity failure)
		assertThrows(IllegalArgumentException.class, () -> {
			new Process(/* predecessor of the process is the existing tenant domain entity */tenant.parent(),
					processEntity.identified(), new HashMap<String, Object>(invalidDescription.currentValue()));
		});
	}

	/**
	 * Check that detection of minimum set of description valued attributes is
	 * supported during the instantiation of process.
	 */
	@Test
	public void givenInvalidEmptyDescription_whenConstructor_thenIllegalArgumentExceptionThrown() {
		DomainEntity processEntity = TestSampleFactory.createIdentity();
		ProcessDescriptor invalidDescription = TestSampleFactory.createProcessDescription(processEntity,
				/* empty name */ "");

		// Check the description which include minimum attribute but that is empty is
		// not accepted by constructor (conformity failure)
		assertThrows(IllegalArgumentException.class, () -> {
			new Process(/* predecessor of the process is the existing tenant domain entity */company,
					processEntity.identified(), new HashMap<String, Object>(invalidDescription.currentValue()));
		});
	}

}
