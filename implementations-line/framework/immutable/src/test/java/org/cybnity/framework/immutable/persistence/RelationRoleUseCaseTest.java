package org.cybnity.framework.immutable.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;

import org.cybnity.framework.immutable.sample.CreateDepartment;
import org.cybnity.framework.immutable.sample.DepartmentChanged;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of RelationRole behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class RelationRoleUseCaseTest {

    private FactType predecessorInitialFactType;
    private FactType successorRelatedFactType;

    @BeforeEach
    public void initFactTypes() {
	successorRelatedFactType = new FactType(DepartmentChanged.class.getSimpleName());
	predecessorInitialFactType = new FactType(CreateDepartment.class.getSimpleName());
    }

    @AfterEach
    public void cleanFactTypes() {
	this.predecessorInitialFactType = null;
	this.successorRelatedFactType = null;
    }

    /**
     * Check that the construction of unrelated predecessor is refused by
     * constructors.
     */
    @Test
    public void givenUnknowPredecessorType_whenConstructor_thenRejected() {
	assertThrows(IllegalArgumentException.class, () -> {
	    // Try to create an instance
	    new RelationRole("source->target", null, successorRelatedFactType);
	});
    }

    /**
     * Check that the construction of unrelated successor is refused by
     * constructors.
     */
    @Test
    public void givenUnknowSuccessorType_whenConstructor_thenRejected() {
	assertThrows(IllegalArgumentException.class, () -> {
	    // Try to create an instance
	    new RelationRole("source->target", predecessorInitialFactType, null);
	});
    }

    /**
     * Check that as uniqueness requirement supported, the object deliver the good
     * fields.
     * 
     * @throws Exception
     */
    @Test
    public void givenFactRecord_whenUniquenessBasedOn_thenValidFieldsReturned() throws Exception {
	RelationRole role = new RelationRole("source->target", predecessorInitialFactType, successorRelatedFactType);
	Set<Field> uniquenessBasedOn = role.basedOn();
	assertNotNull(uniquenessBasedOn);
	// Verify uniqueness only based on two fields
	assertEquals(2, uniquenessBasedOn.size(), "Only two fields shall define the uniqueness of a RelationRole!");
	// Check the chain of uniqueness evaluation
	boolean nameFound = false;
	boolean relationDeclaredByOwnerTypeFound = false;
	for (Field aField : uniquenessBasedOn) {
	    if (aField.getName().equals("relationDeclaredByOwnerType"))
		relationDeclaredByOwnerTypeFound = true;
	    if (aField.getName().equals("name"))
		nameFound = true;
	}
	// Verify found uniqueness combination
	assertTrue(nameFound && relationDeclaredByOwnerTypeFound,
		"Invalid constraints identified as UNIQUE combined set!");
    }
}
