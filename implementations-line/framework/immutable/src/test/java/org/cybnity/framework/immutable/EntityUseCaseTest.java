package org.cybnity.framework.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.Test;

/**
 * Unit test of Entity behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class EntityUseCaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	new EntityImpl((Identifier) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenInvalidIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	IdentifierImpl id = new IdentifierImpl("uid", null);
	new EntityImpl(id);
    }

    @Test
    public void givenValidIdentifyingInformation_whenConstruction_thenFactTimeHistorized() {
	Entity e = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
	assertNotNull("Creation time shall be auto-generated", e.occurredAt());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenCreatedEntity_whenIdentifiersAccess_thenImmutableIdentifiersProvided() {
	Entity e = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
	Collection<Identifier> identifiers = e.identifiers();
	assertFalse("Shall contains one element!", identifiers.isEmpty());
	// Check that collection is not modifiable
	identifiers.add(new IdentifierImpl("uid", "98765HGFVH"));
    }

    @Test
    public void givenEntity_whenEquals_thenIdentifierBasedResult() {
	Identifier i = new IdentifierImpl("uid", "alk8756");
	// Verify identifier equals
	assertEquals(new IdentifierImpl("uid", "alk8756"), i);
	// Create entity based on identifiable fact
	Entity e = new EntityImpl(i);
	// Check if equals is based on identifier value comparison
	assertEquals(new EntityImpl(i), e);

	// Verify if not equals identifier is detected
	assertNotEquals("Not equals identifier shall be detected!", new EntityImpl(new IdentifierImpl("uid", "LKJHGF")),
		e);
    }

}
