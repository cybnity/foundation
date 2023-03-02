package org.cybnity.framework.immutable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import org.cybnity.framework.immutable.sample.ChildAggregate;
import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of ChildFact behaviors regarding its immutability supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class ChildFactUseCaseTest {

    private Entity predecessor;
    private IdentifierImpl childId;

    @Before
    public void initParentEntitySample() {
	predecessor = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
	childId = new IdentifierImpl("uid", "765GFGHJ");
    }

    @After
    public void deleteParentEntitySample() {
	predecessor = null;
	childId = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownChildParent_whenConstructor_thenIllegalArgumentExceptionThrown() {
	new ChildAggregate(/* Unknown parent */null, childId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenUnknownParentIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	predecessor.identifiedBy = new ArrayList<>();// Replace identifiers of pre-constructed instance
	new ChildAggregate(predecessor, /* Unknown optional child identifier */ (Identifier) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenInvalidIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
	IdentifierImpl id = new IdentifierImpl("uid", null); // Invalid child identifier without value
	new ChildAggregate(predecessor, id);
    }

    @Test
    public void givenValidIdentifyingInformation_whenConstruction_thenFactTimeHistorized() {
	ChildFact child = new ChildAggregate(predecessor, childId);
	assertNotNull("Creation time shall be auto-generated", child.occurredAt());
    }


    @Test(expected = UnsupportedOperationException.class)
    public void givenCreatedChild_whenIdentifiersAccess_thenImmutableIdentifiersProvided() {
	ChildFact child = new ChildAggregate(predecessor, childId);
	Collection<Identifier> identifiers = child.identifiers();
	assertFalse("Shall contains one element!", identifiers.isEmpty());
	// Check that collection is not modifiable
	identifiers.add(new IdentifierImpl("uid", "98765HGFVH"));
    }
}
