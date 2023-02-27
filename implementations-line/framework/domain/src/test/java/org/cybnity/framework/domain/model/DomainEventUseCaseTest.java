package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.cybnity.framework.domain.EventIdentifierStringBased;
import org.cybnity.framework.domain.model.sample.UserAccountCreationCommitted;
import org.cybnity.framework.domain.model.sample.UserAccountIdentityCreation;
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
    public void givenUnknownIdentifier_whenConstructor_thenOccuranceDatedAndVersionDefined() {
	UserAccountCreationCommitted event = new UserAccountCreationCommitted();
	// Verify default contents initialized
	assertNotNull(event.occurredAt()); // When event occured
	assertNotNull(event.versionUID()); // Which version of event type
	assertNull(event.identified()); // not identified event
    }

    @Test
    public void givenIdentifiedEvent_whenConstructor_thenIdentifierAttached() {
	// Create an identifiable event
	Identifier id = new EventIdentifierStringBased("uid", "KJGF8765");
	Entity identity = new UserAccountIdentityCreation(id);
	UserAccountCreationCommitted event = new UserAccountCreationCommitted(identity);
	// Verify that identity is maintained
	assertNotNull(event.identified());
	// Check immutable copy is available and valid
	assertEquals("Lost identity!", event.identified(), id);
    }

}
