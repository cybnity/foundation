package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.sample.ChildAggregate;
import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of ChildFact behaviors regarding its immutability supported
 * requirements.
 *
 * @author olivier
 */
public class ChildFactUseCaseTest {

    private Entity predecessor;
    private IdentifierImpl childId;

    @BeforeEach
    public void initParentEntitySample() {
        predecessor = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
        childId = new IdentifierImpl("uid", "765GFGHJ");
    }

    @AfterEach
    public void deleteParentEntitySample() {
        predecessor = null;
        childId = null;
    }

    @Test
    public void givenUnknownChildParent_whenConstructor_thenIllegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChildAggregate(/* Unknown parent */null, childId);
        });

    }

    @Test
    public void givenUnknownParentIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            predecessor.identifiedBy = new ArrayList<>();// Replace identifiers of pre-constructed instance
            new ChildAggregate(predecessor, /* Unknown optional child identifier */ (Identifier) null);
        });
    }

    @Test
    public void givenInvalidIdentifier_whenConstructor_thenIllegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            IdentifierImpl id = new IdentifierImpl("uid", null); // Invalid child identifier without value
            new ChildAggregate(predecessor, id);
        });
    }

    @Test
    public void givenValidIdentifyingInformation_whenConstruction_thenFactTimeHistorized() {
        ChildFact child = new ChildAggregate(predecessor, childId);
        assertNotNull(child.occurredAt(), "Creation time shall be auto-generated");
    }

    @Test
    public void givenCreatedChild_whenIdentifiersAccess_thenImmutableIdentifiersProvided() {
        assertThrows(UnsupportedOperationException.class, () -> {
            ChildFact child = new ChildAggregate(predecessor, childId);
            Collection<Identifier> identifiers = child.identifiers();
            assertFalse(identifiers.isEmpty(), "Shall contains one element!");
            // Check that collection is not modifiable
            identifiers.add(new IdentifierImpl("uid", "98765HGFVH"));
        });
    }
}
