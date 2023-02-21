package org.cybnity.framework.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
	new PhysicalAddressProperty(/* undefined owner */ null, address);
    }

    @Test
    public void givenValidMutablePropertyInitialValue_whenMutablePropertyConstructor_thenMutabilityChainInitialized()
	    throws Exception {
	// Create required valid property initial value
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address);

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
	List<PhysicalAddressProperty> history = changeableAddress.changesHistory();
	assertTrue("Should be empty of any changed value!", history.isEmpty());

    }

    @Test
    public void givenValidMutableProperty_whenVersionChanged_thenPreviousChangesHistorized() {
	// Create required valid property initial value
	PhysicalAddressProperty changeableAddress = new PhysicalAddressProperty(org, address);
	LinkedList<PhysicalAddressProperty> history = changeableAddress.changesHistory(); // current version not
											  // included
	// Add current version to history
	history.add(changeableAddress);

	// Create a new version of changed values (e.g organization moved physical
	// address)
	HashMap<String, Object> newLocation = new HashMap<>();
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), "Paris");
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.Country.name(), "France");

	PhysicalAddressProperty lastAddress = new PhysicalAddressProperty(org, newLocation,
		/* continue changes history since previous states chain */ history);
	newLocation = new HashMap<>();
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.City.name(), "Pekin");
	newLocation.put(PhysicalAddressProperty.PropertyAttributeKey.Country.name(), "China");
	history = lastAddress.changesHistory(); // current version not included
	// Add current version to history
	history.add(lastAddress);

	// Check if previous versions are historized since the start of the changes
	// chain (prior)
	LinkedList<PhysicalAddressProperty> allChangesStory = lastAddress.changesHistory();
	assertEquals("Only 3 times organization changed of address!", 3, allChangesStory.size());
    }

}
