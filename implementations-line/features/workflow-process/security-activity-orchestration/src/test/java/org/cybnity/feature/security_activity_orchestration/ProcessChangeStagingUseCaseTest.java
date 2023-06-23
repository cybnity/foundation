package org.cybnity.feature.security_activity_orchestration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.LinkedList;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.immutable.HistoryState;
import org.junit.jupiter.api.Test;

/**
 * Test about the change management of versioned process steps.
 * 
 * @author olivier
 *
 */
public class ProcessChangeStagingUseCaseTest extends AbstractProcessEvaluation {

	/**
	 * Check that detection of missing step defining the process staging.
	 */
	@Test
	public void givenNullStep_whenChange_thenIllegalArgumentExceptionThrown() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			// Define a basic process that not include steps
			Process p = new Process(/* predecessor */ company, processIdentity.identified(),
					new HashMap<>(processDesc.currentValue()));
			p.changeStaging(null);
		});
	}

	/**
	 * Check that detection of minimum one step defining the process staging.
	 */
	@Test
	public void givenEmptyStepList_whenChange_thenIllegalArgumentExceptionThrown() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			// Define a basic process that not include steps
			Process p = new Process(/* predecessor */ company, processIdentity.identified(),
					new HashMap<>(processDesc.currentValue()));
			HashMap<String, Object> propertyCurrentValue = new HashMap<>();
			propertyCurrentValue.put(PropertyAttributeKey.Steps.name(),
					/* define invalid empty list of steps */ new LinkedList<>());
			Staging staging = new Staging(p.root().getEntity(), null, HistoryState.COMMITTED);
			p.changeStaging(staging);
		});
	}

	/**
	 * Check that a default original process instance (by default does not include
	 * steps) is updated with an set of valid steps (which are checked with success
	 * by conformity method).
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidStepList_whenChange_thenProcessSuccesslyUpdatedWith() throws Exception {
		// Create basis process including empty steps list by default
		Process p = new Process(company, processIdentity.identified(), new HashMap<>(processDesc.currentValue()));
		// Prepare valid steps defining an acceptable workflow staging
		Staging staging = TestSampleFactory.createStaging(p.root().getEntity());
		// Change process staging
		p.changeStaging(staging);
		// Verify that last version was updated
		assertNotNull(p.staging());
		assertFalse(p.staging().steps().isEmpty());
	}

}
