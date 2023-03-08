package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.Test;

/**
 * Unit test of DomainEvent behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class DomainEventUseCaseTest {

    @Test
    public void givenUnknownIdentifier_whenConstructor_thenOccuranceDatedAndVersionDefined() throws Exception {
	UserAccountChanged event = new UserAccountChanged();
	// Verify default contents initialized
	assertNotNull(event.occurredAt()); // When event occured
	assertNotNull(event.versionHash()); // Which version of event type
	assertNull(event.identified()); // not identified event
    }

    @Test
    public void givenIdentifiedEvent_whenConstructor_thenIdentifierAttached() throws Exception {
	// Create an identifiable event
	Identifier id = new IdentifierStringBased("uid", "KJGF8765");
	Entity identity = new UserAccountIdentityCreation(id);
	UserAccountChanged event = new UserAccountChanged(identity);
	// Verify that identity is maintained
	assertNotNull(event.identified());
	// Check immutable copy is available and valid
	assertEquals("Lost identity!", event.identified(), id);
    }

}
