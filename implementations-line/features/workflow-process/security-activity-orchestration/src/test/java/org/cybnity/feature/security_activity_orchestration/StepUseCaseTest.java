package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of Step behaviors regarding its instantiation and supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class StepUseCaseTest extends AbstractProcessEvaluation {

	private Process process;

	@BeforeEach
	public void initProcess() throws Exception {
		// Define a described process
		process = new Process(/* predecessor */ company, processIdentity.identified(),
				new HashMap<>(processDesc.currentValue()));
	}

	public void clean() {
		this.process = null;
	}

	/**
	 * Check that instantiation with minimum values defined is created with success,
	 * including basic properties.
	 */
	@Test
	public void givenNamedStep_whenConstructor_thenSuccessInstantiation() throws Exception {
		// Define a described step
		Step s = new Step(process.root(), "PREPARE", null);

		assertNotNull(s);
		assertNotNull(s.name());
		assertNotNull(s.immutable());
		assertNotNull(s.occurredAt());
		assertEquals(process.root().getEntity(), s.owner(),
				"Invalid predecessor defined as prerequisite owner of the step!");
		assertNotNull(s.ownerReference());
		assertNotNull(s.versionHash());
		// Check that default not active state is defined
		assertNotNull(s.activation());
		assertFalse(s.activation().isActive());// Not active by default
		// Check that default completion state is defined and equals to zero percentage
		assertNotNull(s.completion());
		assertEquals(Float.valueOf(0.0f), s.completion().percentage());
	}

}
