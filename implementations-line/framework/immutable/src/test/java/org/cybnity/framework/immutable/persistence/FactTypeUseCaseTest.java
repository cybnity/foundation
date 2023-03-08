package org.cybnity.framework.immutable.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Set;

import org.cybnity.framework.immutable.sample.DepartmentChanged;
import org.junit.Test;

/**
 * Unit test of FactType behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class FactTypeUseCaseTest {

    /**
     * Check that the construction of no named type is refused by constructors.
     */
    @Test(expected = IllegalArgumentException.class)
    public void givenUndefinedTypeName_whenConstructor_thenRejected() {
	// Try to create an instance about null type name
	new FactType(null);
    }

    /**
     * Check that the construction of no named type is refused by constructors.
     */
    @Test(expected = IllegalArgumentException.class)
    public void givenEmptyTypeName_whenConstructor_thenRejected() {
	// Try to create an instance about empty type name
	new FactType("");
    }

    /**
     * Check that identifier auto-generation is applied when not declared identifier is used during the constructor use.
     */
    @Test
    public void givenUnidentifiedType_whenConstructor_thenIdAutoGeneration() {
	// Create valid fact not identified
	FactType f = new FactType(DepartmentChanged.class.getSimpleName());
	// Check that id was auto-generated
	assertNotNull(f.id());
	assertFalse(f.id().isBlank() || f.id().isEmpty());
    }

    /**
     * Check that as uniqueness requirement supported, the object deliver the unique
     * good field.
     * 
     * @throws Exception
     */
    @Test
    public void givenFactType_whenUniquenessBasedOn_thenValidNameFieldReturned() {
	FactType type = new FactType(DepartmentChanged.class.getSimpleName());
	Set<Field> uniquenessBasedOn = type.basedOn();
	assertNotNull(uniquenessBasedOn);
	// Verify uniqueness only based on one field
	assertEquals("Only one field shall define the uniqueness of a FactType!", 1, uniquenessBasedOn.size());
	// Check that only 'name' attribute value if the chain of uniqueness evaluation
	Field nameField = uniquenessBasedOn.iterator().next();
	assertEquals("Invalid constraint identified as UNIQUE field!", "name", nameField.getName());

    }
}
