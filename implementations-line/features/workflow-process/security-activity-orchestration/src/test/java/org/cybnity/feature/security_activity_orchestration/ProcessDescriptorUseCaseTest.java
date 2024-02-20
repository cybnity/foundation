package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessDescriptor;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of process descriptor behaviors regarding its supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class ProcessDescriptorUseCaseTest {

	private Entity processId;

	@BeforeEach
	public void init() {
		Identifier id = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), "JHGFH87654");
		this.processId = new DomainEntity(id);
	}

	@AfterEach
	public void clean() {
		processId = null;
	}

	/**
	 * Verify that instantiation of descriptor is completed regarding standard
	 * immutability contents, and additional properties.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenNamedProcess_whenDescriptorConstructor_thenMinimumDescriptionDefined() throws Exception {
		// Define a description of the process
		HashMap<String, Object> propertyCurrentValue = new HashMap<>();
		// Define process name
		propertyCurrentValue.put(ProcessDescriptor.PropertyAttributeKey.Name.name(), "NIST RMF");
		// Define process description properties
		Collection<Attribute> properties = new ArrayList<>();
		Attribute organizationLevel = new Attribute("action-level", "OrganizationLevel");
		properties.add(organizationLevel);
		propertyCurrentValue.put(ProcessDescriptor.PropertyAttributeKey.Properties.name(), properties);
		// Create the descriptor of the process sample as owner
		ProcessDescriptor desc = new ProcessDescriptor(/** owner of the descriptor **/
				processId, propertyCurrentValue, HistoryState.COMMITTED,
				/** none prior version */
				(ProcessDescriptor) null);

		// Verify default contents initialized
		assertNotNull(desc.name());// process naming
		assertNotNull(desc.versionHash()); // Which version of descriptor type
		assertNotNull(desc.occurredAt()); // When immutable descriptor created
		assertNotNull(desc.owner()); // Attached descriptor to process identity
		assertEquals(processId, desc.owner());

		// Verify if immutable version is completed
		ProcessDescriptor clone = (ProcessDescriptor) desc.immutable();
		assertNotNull(clone.name());// process naming
		assertNotNull(clone.versionHash()); // Which version of descriptor type
		assertNotNull(clone.occurredAt()); // When immutable descriptor created
		assertNotNull(clone.owner()); // Attached descriptor to process identity
		Map<String, Object> currentValue = clone.currentValue();

		@SuppressWarnings("unchecked")
		Collection<Attribute> clonedProperties = (Collection<Attribute>) currentValue
				.get(ProcessDescriptor.PropertyAttributeKey.Properties.name());
		assertTrue(clonedProperties.contains(organizationLevel), "should had been cloned!");

		// Verify equality of descriptor and immutable version
		assertEquals(desc, clone);
	}

}
