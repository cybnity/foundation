package org.cybnity.framework.immutable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.cybnity.framework.immutable.sample.Organization;
import org.cybnity.framework.immutable.sample.PhysicalAddressProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of MutableProperty behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class MutablePropertyUseCaseTest {

    private Organization org;
    private Identifier id;
    private String organizationName;
    private HashMap<String, Object> address;

    @BeforeEach
    public void initOrganizationSample() throws Exception {
	organizationName = "Stark Industries";
	id = new IdentifierImpl("uuid", "LKJHDGHFJGKH87654");
	org = new Organization(id, organizationName);

	address = new HashMap<>();
	address.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), "Los Angeles");
	address.put(PhysicalAddressProperty.PropertyAttributeKey.State.name(), "California");
	address.put(PhysicalAddressProperty.PropertyAttributeKey.Street.name(), "-- Confidential :) --");
    }

    @AfterEach
    public void cleanOrganizationSample() throws Exception {
	org = null;
	id = null;
	organizationName = null;
    }

    @Test
    public void givenUnknownPropertyOwner_whenMutablePropertyConstructor_thenIllegalArgumentExceptionThrown() {
	assertThrows(IllegalArgumentException.class, () -> {
	    // Try instantiation with violation of mandatory owner parameter
	    new PhysicalAddressProperty(/* undefined owner */ null, address,
		    /* default status applied by constructor */ null);
	});
    }

    @Test
    public void givenValidMutablePropertyInitialValue_whenMutablePropertyConstructor_thenMutabilityChainInitialized()
	    throws Exception {
	// Create required valid property initial value
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address,
		/* default status applied by constructor */ null);

	// Verify current values version is saved
	HashMap<String, Object> currentVersion = changeableAddress.currentValue();
	assertEquals(address.get(PhysicalAddressProperty.PropertyAttributeKey.State.name()),
		currentVersion.get(PhysicalAddressProperty.PropertyAttributeKey.State.name()),
		"Attribute's value shall had been initialized by default!");
	assertEquals(address.get(PhysicalAddressProperty.PropertyAttributeKey.Street.name()),
		currentVersion.get(PhysicalAddressProperty.PropertyAttributeKey.Street.name()),
		"Attribute's value shall had been initialized by default!");
	assertNull(address.get(PhysicalAddressProperty.PropertyAttributeKey.Country.name()),
		"Not defined attribute's value shall not found!");

	// Check saved owner
	assertNotNull(changeableAddress.owner(), "Should had been saved as reference owner!");

	// Check that base history is empty as prior history
	Set<MutableProperty> history = changeableAddress.changesHistory();
	assertTrue(history.isEmpty(), "Should be empty of any changed value!");

    }

    @Test
    public void givenValidMutableProperty_whenVersionsChanged_thenPriorVersionsHistorySaved() {
	// Create required valid property initial value
	String city1 = (String) address.get(PhysicalAddressProperty.PropertyAttributeKey.City.name());
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address,
		/* default status applied by constructor */ null);
	Set<MutableProperty> history = changeableAddress.changesHistory(); // current version not
									   // included because none previous
									   // version
	// Check empty history and default history status
	assertTrue(history.isEmpty(), "None anterior history shall exist!");
	// Check default history status applied by constructor
	assertEquals(HistoryState.COMMITTED, changeableAddress.historyStatus(),
		"Invalid default status applied by constructor!");

	// Create a new version of changed values (e.g organization moved physical
	// address) that simulate change of address decided by user of physical address
	HashMap<String, Object> newLocation = new HashMap<>();
	String city2 = "Paris";
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), city2);
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.Country.name(), "France");

	PhysicalAddressProperty lastAddress = new PhysicalAddressProperty(org, newLocation,
		/* Decision status simulating user decision */ HistoryState.MERGED,
		/* continue changes history after one predecessor */ changeableAddress);
	history = lastAddress.changesHistory(); // current version including one historized prior state (e.g Paris
						// location)
	// Check history including previous address backuped
	assertEquals(1, history.size(), "Only one prior address shall had been historized!");
	// Check assigned history status of the updated property
	assertEquals(HistoryState.MERGED, lastAddress.historyStatus(), "Invalid assigned status by constructor!");
	// Check historized predecessor contents
	for (MutableProperty priorAddress : history) {
	    HashMap<String, Object> priorValue = priorAddress.value;
	    for (Entry<String, Object> originalAddress : address.entrySet()) {
		String propertyAttributeName = originalAddress.getKey();
		String propertyAttributeValue = (String) originalAddress.getValue();
		// Find original address in historized instances
		assertTrue(priorValue.containsKey(propertyAttributeName),
			"First address element of organization should exist in history!");
		assertTrue(priorValue.containsValue(propertyAttributeValue),
			"First address element's value of organization should exist in history!");
	    }
	}

	// Create a new version of changed address
	HashMap<String, Object> nextLocation = new HashMap<>();
	String city3 = "Pekin";
	nextLocation.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), city3);
	nextLocation.put(PhysicalAddressProperty.PropertyAttributeKey.Country.name(), "China");
	PhysicalAddressProperty lastCurrentAddress = new PhysicalAddressProperty(org, nextLocation,
		/* Decision status simulating user decision */ HistoryState.MERGED,
		/* continue changes history after one predecessor */ lastAddress);
	history = lastCurrentAddress.changesHistory(); // current version including one historized previous addresses
	assertEquals(1, history.size(), "Only one prior address shall had been historized!");// as history chain without
											     // multiple concurrent
											     // origins of changed
											     // property
	// Check history chain including previous addresses backuped contents
	for (MutableProperty aPriorAddress : history) {
	    HashMap<String, Object> priorValue = aPriorAddress.value;
	    // Check if it's the last historized address (inverse path in history)
	    assertEquals(city2, priorValue.get(PhysicalAddressProperty.PropertyAttributeKey.City.name()),
		    "Previous prior is not the good!");
	    // Verify that previous historized property value also contain a chained history
	    // of old addresses
	    Set<MutableProperty> anteriors = aPriorAddress.changesHistory();
	    assertFalse(anteriors.isEmpty(), "More old address should had been historized!");
	    for (MutableProperty anAnteriors : anteriors) {
		HashMap<String, Object> anteriorValue = anAnteriors.value;
		// Check if it's the anterior address
		assertEquals(city1, anteriorValue.get(PhysicalAddressProperty.PropertyAttributeKey.City.name()),
			"Anterior prior is not the good!");
	    }
	}
    }

}
