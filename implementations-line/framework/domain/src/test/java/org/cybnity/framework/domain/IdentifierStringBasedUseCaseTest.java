package org.cybnity.framework.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Unit test of IdentifierStringBased behaviors regarding its supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class IdentifierStringBasedUseCaseTest {

    /**
     * Check that equals function only based on value is valid.
     */
    @Test
    public void givenIdentifierWithSameValueAndDifferentName_whenEqualsEvaluated_thenNotEquals() throws Exception {
	// Create identifiers with same value but with different name
	String aValue = "IOUYTGFD876543j";
	IdentifierStringBased s = new IdentifierStringBased("uui", aValue);
	IdentifierStringBased s2 = new IdentifierStringBased("uid", aValue);
	// Check equality based on identifier value and name
	assertNotEquals(s, s2, "Should be fals as not equals contributors (and hascode)!");

	// Check than equals instance reference detect also the technical equality
	IdentifierStringBased s3 = s2;
	assertEquals(s2, s3, "Should be true as equals references!");
    }

    /**
     * Check that two instance with identical name but several value are not
     * evaluated as equals.
     * 
     * @throws Exception
     */
    @Test
    public void givenSameNameAndNotEqualsValue_whenEqualsEvaluated_thenNotEquals() throws Exception {
	// Create identifiers with same name but different values
	String name = "uid";
	IdentifierStringBased s = new IdentifierStringBased(name, "KJHGF8765456789");
	IdentifierStringBased s2 = new IdentifierStringBased(name, "OIUYTRFD9875432345");
	// Check evaluation result as not equals
	assertNotEquals(s, s2, "Should not be equals regarding their values!");
    }

    /**
     * Verify that several instance (e.g immutable copy) of identifier defined by a
     * same name and value, have the same hascode value.
     * 
     * @throws Exception
     */
    @Test
    public void givenSameFunctionalContentContributors_whenHasCodeCalculation_thenSame() throws Exception {
	// Create instances with same name and value
	String name = "UUID", value = UUID.randomUUID().toString();
	IdentifierStringBased s = new IdentifierStringBased(name, value);
	IdentifierStringBased s2 = new IdentifierStringBased(name, value);

	// Check hashcode unicity calculated from the same attribute value
	assertEquals(s.hashCode(), s2.hashCode(), "Should be the same because from same attribute value!");

	// Create instance with same value but different name
	IdentifierStringBased s3 = new IdentifierStringBased("id", value);
	// Check not equals functional based hashcode
	assertNotEquals(s.hashCode(), s3.hashCode(), "Should be different because not same contributor attributes!");
    }
}
