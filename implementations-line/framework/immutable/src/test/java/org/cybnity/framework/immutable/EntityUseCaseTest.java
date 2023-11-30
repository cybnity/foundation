package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of Entity behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class EntityUseCaseTest {

    @Test
    public void givenUnknownIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	assertThrows(IllegalArgumentException.class, () -> {
	    new EntityImpl((Identifier) null);
	});
    }

    @Test
    public void givenInvalidIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	assertThrows(IllegalArgumentException.class, () -> {
	    IdentifierImpl id = new IdentifierImpl("uid", null);
	    new EntityImpl(id);
	});
    }

    @Test
    public void givenValidIdentifyingInformation_whenConstruction_thenFactTimeHistorized() {
	Entity e = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
	assertNotNull(e.occurredAt(),"Creation time shall be auto-generated");
    }

    @Test
    public void givenCreatedEntity_whenIdentifiersAccess_thenImmutableIdentifiersProvided() {
	assertThrows(UnsupportedOperationException.class, () -> {
	    Entity e = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
	    Collection<Identifier> identifiers = e.identifiers();
	    assertFalse(identifiers.isEmpty(), "Shall contains one element!");
	    // Check that collection is not modifiable
	    identifiers.add(new IdentifierImpl("uid", "98765HGFVH"));
	});
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
	assertNotEquals(new EntityImpl(new IdentifierImpl("uid", "LKJHGF")), e,
		"Not equals identifier shall be detected!");
    }

}
