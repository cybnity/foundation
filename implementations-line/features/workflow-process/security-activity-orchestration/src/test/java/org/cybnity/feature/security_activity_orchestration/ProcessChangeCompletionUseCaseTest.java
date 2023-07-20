package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.model.CompletionState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test about the change management of versioned process completion state.
 * 
 * @author olivier
 *
 */
public class ProcessChangeCompletionUseCaseTest extends AbstractProcessEvaluation {

	private Process validProcess;

	@BeforeEach
	public void init() throws ImmutabilityException {
		validProcess = new Process(/* predecessor */ TestSampleFactory.createOrganization(null),
				processIdentity.identified(), new HashMap<String, Object>(processDesc.currentValue()));
	}

	@AfterEach
	public void clean() {
		validProcess = null;
	}

	/**
	 * Verify that update of state is refused with a status that is not property
	 * with same owner than the updated process.
	 */
	@Test
	public void givenNotEqualsOwner_whenChangeProcessState_thenIllegalArgumentThrown() throws ImmutabilityException {
		// Use a valid process instance

		// Prepare a new version of completion state BUT THAT IS NOT ABOUT THE SAME
		// PROCESS IDENTITY
		CompletionState s = new CompletionState(
				/* difference owner identity */TestSampleFactory.createIdentity().reference(), "InvalidOwnerState",
				/* valid completion rate */ Float.valueOf(0.20f), /* prior version */ validProcess.completion());

		// Try to update with invalid status
		assertThrows(IllegalArgumentException.class, () -> {
			validProcess.changeCompletion(s);
		});
	}

	/**
	 * Verify that negative completion rate that is not valid, is detected as cause
	 * of change reject.
	 */
	@Test
	public void givenNegativeCompletionPercentage_whenUpdateAttempt_thenIllegalArgumentThrown()
			throws ImmutabilityException {
		// Use a valid process instance

		// Prepare a new version of completion state BUT THAT IS HAVE NEGATIVE
		// PERCENTAGE OF COMPLETION
		CompletionState s = new CompletionState(validProcess.root(), "ValidOwnerState", Float.valueOf(-0.20f),
				/* prior version */ validProcess.completion());
		// Try to update with invalid status
		assertThrows(IllegalArgumentException.class, () -> {
			validProcess.changeCompletion(s);
		});
	}

	/**
	 * Verify that positive or zero completion rate is detected as valid for update
	 * of process.
	 */
	@Test
	public void givenValidCompletionPercentage_whenUpdate_thenSuccessConformityCheck() throws ImmutabilityException {
		// Use a valid process instance

		// Prepare a new version of completion state including a zero percentage rate
		Float v1 = Float.valueOf(0.1f); // 10% of progress
		CompletionState s = new CompletionState(validProcess.root(), "ValidOwnerState", v1,
				/* prior version */ validProcess.completion());
		validProcess.changeCompletion(s);

		// Check that process completion have been upgraded and last completion
		// saved
		CompletionState lastState = validProcess.completion();
		assertTrue(lastState.changesHistory().size() > 0);
		// Check value upgrade regarding the completion rate
		assertTrue(v1.compareTo(lastState.percentage()) == 0);

		// Update to new version with positive percentage
		Float v2 = Float.valueOf(0.6f);// 60% of completed progress
		CompletionState st2 = new CompletionState(validProcess.root(), "ValidOwnerState", v2,
				/* prior version */ validProcess.completion());
		validProcess.changeCompletion(st2);
		lastState = validProcess.completion();
		assertTrue(lastState.changesHistory().size() > 0);
		// Check that new version is positive
		assertTrue(v2.compareTo(lastState.percentage()) == 0);

		// Check previous version
		CompletionState previousV1 = (CompletionState) lastState.changesHistory().iterator().next();
		assertEquals(v1, previousV1.percentage());

	}
}
