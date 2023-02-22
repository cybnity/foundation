package org.cybnity.framework.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.cybnity.framework.immutable.data.IdentifierImpl;
import org.cybnity.framework.immutable.data.Organization;
import org.cybnity.framework.immutable.data.PhysicalAddressProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void initOrganizationSample() throws Exception {
	organizationName = "Stark Industries";
	id = new IdentifierImpl("uuid", "LKJHDGHFJGKH87654");
	org = new Organization(id, organizationName);

	address = new HashMap<>();
	address.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), "Los Angeles");
	address.put(PhysicalAddressProperty.PropertyAttributeKey.State.name(), "California");
	address.put(PhysicalAddressProperty.PropertyAttributeKey.Street.name(), "-- Confidential :) --");
    }

    @After
    public void cleanOrganizationSample() throws Exception {
	org = null;
	id = null;
	organizationName = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownPropertyOwner_whenMutablePropertyConstructor_thenIllegalArgumentExceptionThrown() {
	// Try instantiation with violation of mandatory owner parameter
	new PhysicalAddressProperty(/* undefined owner */ null, address,
		/* default status applied by constructor */ null);
    }

    @Test
    public void givenValidMutablePropertyInitialValue_whenMutablePropertyConstructor_thenMutabilityChainInitialized()
	    throws Exception {
	// Create required valid property initial value
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address,
		/* default status applied by constructor */ null);

	// Verify current values version is saved
	HashMap<String, Object> currentVersion = changeableAddress.currentValue();
	assertEquals("Attribute's value shall had been initialized by default!",
		address.get(PhysicalAddressProperty.PropertyAttributeKey.State.name()),
		currentVersion.get(PhysicalAddressProperty.PropertyAttributeKey.State.name()));
	assertEquals("Attribute's value shall had been initialized by default!",
		address.get(PhysicalAddressProperty.PropertyAttributeKey.Street.name()),
		currentVersion.get(PhysicalAddressProperty.PropertyAttributeKey.Street.name()));
	assertNull("Not defined attribute's value shall not found!",
		address.get(PhysicalAddressProperty.PropertyAttributeKey.Country.name()));

	// Check saved owner
	assertNotNull("Should had been saved as reference owner!", changeableAddress.owner());

	// Check that base history is empty as prior history
	Set<PhysicalAddressProperty> history = changeableAddress.changesHistory();
	assertTrue("Should be empty of any changed value!", history.isEmpty());

    }

    @Test
    public void givenValidMutableProperty_whenVersionsChanged_thenPriorVersionsHistorySaved() {
	// Create required valid property initial value
	String city1 = (String) address.get(PhysicalAddressProperty.PropertyAttributeKey.City.name());
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address,
		/* default status applied by constructor */ null);
	Set<PhysicalAddressProperty> history = changeableAddress.changesHistory(); // current version not
										   // included because none previous
										   // version
	// Check empty history and default history status
	assertTrue("None anterior history shall exist!", history.isEmpty());
	// Check default history status applied by constructor
	assertEquals("Invalid default status applied by constructor!", HistoryState.COMMITTED,
		changeableAddress.historyStatus());

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
	assertEquals("Only one prior address shall had been historized!", 1, history.size());
	// Check assigned history status of the updated property
	assertEquals("Invalid assigned status by constructor!", HistoryState.MERGED, lastAddress.historyStatus());
	// Check historized predecessor contents
	for (PhysicalAddressProperty priorAddress : history) {
	    HashMap<String, Object> priorValue = priorAddress.value;
	    for (Entry<String, Object> originalAddress : address.entrySet()) {
		String propertyAttributeName = originalAddress.getKey();
		String propertyAttributeValue = (String) originalAddress.getValue();
		// Find original address in historized instances
		assertTrue("First address element of organization should exist in history!",
			priorValue.containsKey(propertyAttributeName));
		assertTrue("First address element's value of organization should exist in history!",
			priorValue.containsValue(propertyAttributeValue));
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
	assertEquals("Only one prior address shall had been historized!", 1, history.size());// as history chain without
											     // multiple concurrent
											     // origins of changed
											     // property
	// Check history chain including previous addresses backuped contents
	for (PhysicalAddressProperty aPriorAddress : history) {
	    HashMap<String, Object> priorValue = aPriorAddress.value;
	    // Check if it's the last historized address (inverse path in history)
	    assertEquals("Previous prior is not the good!", city2,
		    priorValue.get(PhysicalAddressProperty.PropertyAttributeKey.City.name()));
	    // Verify that previous historized property value also contain a chained history
	    // of old addresses
	    Set<PhysicalAddressProperty> anteriors = aPriorAddress.changesHistory();
	    assertFalse("More old address should had been historized!", anteriors.isEmpty());
	    for (PhysicalAddressProperty anAnteriors : anteriors) {
		HashMap<String, Object> anteriorValue = anAnteriors.value;
		// Check if it's the anterior address
		assertEquals("Anterio prior is not the good!", city1,
			anteriorValue.get(PhysicalAddressProperty.PropertyAttributeKey.City.name()));
	    }
	}
    }

}
