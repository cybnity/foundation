package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.junit.jupiter.api.Test;

/**
 * Test about the change management of versioned process activity state.
 * 
 * @author olivier
 *
 */
public class ProcessChangeActivationUseCaseTest extends AbstractProcessEvaluation {

	/**
	 * Verify that update of state is refused with a status that is not property
	 * with same owner than the updated process.
	 */
	@Test
	public void givenNotEqualsOwner_whenChangeProcessState_thenIllegalArgumentThrown() throws ImmutabilityException {
		// Create a process
		DomainEntity processIdentity = TestSampleFactory.createIdentity();
		Process p = new Process(/* predecessor */ TestSampleFactory.createOrganization(null),
				processIdentity.identified(), new HashMap<String, Object>(
						TestSampleFactory.createProcessDescription(processIdentity, "NIST RMF").currentValue()));
		// Prepare a new version of activity state BUT THAT IS NOT ABOUT THE SAME
		// PROCESS IDENTITY
		ActivityState s = new ActivityState(TestSampleFactory.createIdentity().reference(), Boolean.TRUE,
				/* prior version */ p.activation());

		// Try to update with invalid description owner
		assertThrows(IllegalArgumentException.class, () -> {
			p.changeActivation(s);
		});

	}

}
