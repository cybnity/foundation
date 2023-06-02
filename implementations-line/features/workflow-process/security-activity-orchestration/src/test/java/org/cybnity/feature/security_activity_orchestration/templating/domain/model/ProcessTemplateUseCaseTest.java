package org.cybnity.feature.security_activity_orchestration.templating.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Set;

import org.cybnity.feature.security_activity_orchestration.sample.Organization;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.MutableProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of ProcessTemplate behaviors regarding its instantiation and
 * supported requirements.
 * 
 * @author olivier
 *
 */
public class ProcessTemplateUseCaseTest {

	private Identifier id;
	private String templateName;
	private Organization org;
	private HashMap<String, Object> specification;

	@BeforeEach
	public void initSample() throws Exception {
		templateName = "NIST RMF template";
		// Owner of template
		id = new IdentifierStringBased("uuid", "LKJHDGHFJGKH87654");
		org = new Organization(id, "CYBNITY");
		// template definition
		specification = new HashMap<>();
		specification.put(ProcessTemplate.PropertyAttributeKey.Name.name(), templateName);
		specification.put(ActivityState.PropertyAttributeKey.StateValue.name(), Boolean.TRUE);
	}

	@AfterEach
	public void cleanSample() throws Exception {
		id = null;
		templateName = null;
		specification = null;
	}

	@Test
	public void givenUnknownPropertyOwner_whenConstructor_thenIllegalArgumentExceptionThrown() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Try instantiation with violation of mandatory owner parameter
			new ProcessTemplate(/* undefined owner */ null, specification,
					/* default status applied by constructor */ null);
		});
	}

	@Test
	public void givenValidTemplateInitialValue_whenConstructor_thenMutabilityChainInitialized() throws Exception {
		// Create required valid property initial value
		ProcessTemplate changeableTemplate = new ProcessTemplate(org, specification, HistoryState.COMMITTED,
				/* default status applied by constructor */ (ProcessTemplate[]) null);

		// Verify current values version is saved
		HashMap<String, Object> currentVersion = changeableTemplate.currentValue();
		assertEquals(specification.get(ProcessTemplate.PropertyAttributeKey.Name.name()),
				currentVersion.get(ProcessTemplate.PropertyAttributeKey.Name.name()),
				"Attribute's value shall had been initialized by default!");

		// Check saved owner
		assertNotNull(changeableTemplate.owner(), "Should had been saved as reference owner!");

		// Check that base history is empty as prior history
		Set<MutableProperty> history = changeableTemplate.changesHistory();
		assertTrue(history.isEmpty(), "Should be empty of any changed value!");
	}

	/**
	 * Verify that specified attributes are read (e.g supporting the ITemplate,
	 * ActivityState contracts realization).
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenSpecification_whenConstructor_thenNamed() throws Exception {
		ProcessTemplate t = new ProcessTemplate(org, specification, HistoryState.COMMITTED,
				/* default status applied by constructor */ (ProcessTemplate[]) null);
		assertNotNull(t.name());
		assertTrue(t.isActive());
		assertNotNull(t.occurredAt());
		assertNotNull(t.versionHash());
		assertNotNull(t.immutable());
	}

	/**
	 * Verify the rules of equality applied on a template.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void givenSameNamedTemplate_whenEqualsEvaluation_thenTrue() throws Exception {
		// Create two instances including same types of contents
		ProcessTemplate t1 = new ProcessTemplate(org, specification, HistoryState.COMMITTED,
				/* default status applied by constructor */ (ProcessTemplate[]) null);
		ProcessTemplate t2 = new ProcessTemplate(org, (HashMap<String, Object>) specification.clone(),
				HistoryState.COMMITTED, /* default status applied by constructor */ (ProcessTemplate[]) null);
		ProcessTemplate t3 = new ProcessTemplate(org, (HashMap<String, Object>) specification.clone(),
				HistoryState.COMMITTED, /* default status applied by constructor */ (ProcessTemplate[]) null);

		// Check that all equals contents are evaluated as identical
		assertEquals(t1, t2, "Same contents shall had been detected!");
		assertEquals(t2, t1, "Same contents shall had been detected!");
		assertEquals(t2, t3, "Same contents shall had been detected!");

		// Change one value of one of the property to compare (simulating a difference
		// of property definition)
		HashMap<String, Object> specificationChanged = t3.currentValue();
		specificationChanged.put(ProcessTemplate.PropertyAttributeKey.Name.name(), "otherName");

		// Check that difference is detected during equality evaluation regarding the
		// values of the template names
		assertNotEquals(t2, t3, "Difference in values shall had been detected!");

	}
}
