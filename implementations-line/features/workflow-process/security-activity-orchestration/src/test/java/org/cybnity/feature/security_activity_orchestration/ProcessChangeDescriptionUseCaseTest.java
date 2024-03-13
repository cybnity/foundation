package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.feature.security_activity_orchestration.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test about the change management of versioned process description.
 * 
 * @author olivier
 *
 */
public class ProcessChangeDescriptionUseCaseTest extends AbstractProcessEvaluation {

	/**
	 * Verify that update of description is refused with a description that is not
	 * property with same owner than the updated process.
	 */
	@Test
	public void givenNotEqualsOwner_whenChangeProcessDescription_thenIllegalArgumentThrown()
			throws ImmutabilityException {
		// Create a process described
		Process p = new Process(/* predecessor */ TestSampleFactory.createOrganization(null),
				processIdentity.identified(), new HashMap<String, Object>(processDesc.currentValue()));
		// Prepare a new version of description BUT THAT IS NOT ABOUT THE SAME PROCESS
		// IDENTITY
		String nameV2 = "NIST RMF V2";
		HashMap<String, Object> descriptionAttr = new HashMap<>();
		descriptionAttr.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), nameV2);
		ProcessDescriptor v2 = new ProcessDescriptor(TestSampleFactory.createIdentity(), descriptionAttr,
				HistoryState.COMMITTED, /* prior version */ p.description());

		// Try to update with invalid description owner
		assertThrows(IllegalArgumentException.class, () -> {
			p.changeDescription(v2);
		});

	}

	/**
	 * Check that update of the process description does not lost the previous
	 * description versions history.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenProcessDescribed_whenUpdateDescription_thenPriorVersionsHistorySaved() throws Exception {
		// Define a description of the process to instantiate
		DomainEntity processIdentity = TestSampleFactory.createIdentity();
		String nameV1 = "NIST RMF";
		Process p = new Process(/* predecessor */ TestSampleFactory.createOrganization(null),
				processIdentity.identified(), new HashMap<String, Object>(
						TestSampleFactory.createProcessDescription(processIdentity, nameV1).currentValue()));
		// Get the original description of the process
		ProcessDescriptor description = p.description();
		Set<MutableProperty> history = description.changesHistory(); // current version not
		// included because none previous version
		// Check empty history and default history status
		assertTrue(history.isEmpty(), "None anterior history shall exist!");
		// Check default history status applied by constructor
		assertEquals(HistoryState.COMMITTED, description.historyStatus(),
				"Invalid default status applied by constructor!");

		// Prepare a new version of the changeable description
		String nameV2 = "NIST RMF V2";
		HashMap<String, Object> descriptionAttr = new HashMap<>();
		descriptionAttr.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), nameV2);
		ProcessDescriptor v2 = new ProcessDescriptor(p.root().getEntity(), descriptionAttr, HistoryState.COMMITTED,
				/* prior version */ p.description());
		// Set new version of the process description
		p.changeDescription(v2);

		// Check that process description is updated and based on V2 name
		assertEquals(nameV2, p.name());

		String nameV3 = "NIST RMF V3";
		descriptionAttr = new HashMap<>();
		descriptionAttr.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), nameV3);
		ProcessDescriptor v3 = new ProcessDescriptor(p.root().getEntity(), descriptionAttr, HistoryState.COMMITTED,
				/* prior version */ p.description());
		// Set new version of the process description
		p.changeDescription(v3);

		// Check that history is including the V1 version
		assertEquals(1, p.description().changesHistory().size(),
				"shall contain only last instance as sequenced history!");
		// as history chain without
		// multiple concurrent
		// origins of changed
		// property

		// Check order of history
		String[] expectedOrderedNames = { nameV3, nameV2, nameV1 };
		checkDescriptionNames(expectedOrderedNames, p.description(), 0);
	}

	/**
	 * Recursive evaluation and verification of name versions regarding a
	 * descriptor's history.
	 * 
	 * @param expectedOrderedNames
	 * @param d
	 * @param expectedNameIndex
	 * @throws ImmutabilityException
	 */
	private void checkDescriptionNames(String[] expectedOrderedNames, ProcessDescriptor d, int expectedNameIndex)
			throws ImmutabilityException {
		// Check equals name
		// System.out.println("Evaluated: " + d.name() + ", compared to: " +
		// expectedOrderedNames[expectedNameIndex]);
		assertEquals(expectedOrderedNames[expectedNameIndex], d.name());
		// Check previous description version name (when existing more old descriptor
		// version)
		if (d.changesHistory() != null && !d.changesHistory().isEmpty()) {
			// Check previous description name
			checkDescriptionNames(expectedOrderedNames, (ProcessDescriptor) d.changesHistory().iterator().next(),
					expectedNameIndex + 1);
		}
	}
}
