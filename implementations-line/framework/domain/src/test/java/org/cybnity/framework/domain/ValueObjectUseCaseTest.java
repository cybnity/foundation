package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.sample.CompanyNameObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test of ValueObject behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class ValueObjectUseCaseTest {

    /**
     * Check that two different instances (e.g immutable copies) of a value object
     * containing the same internal values, have a same hashcode value (used by
     * hashmap).
     */
    @Test
    public void givenObjectsFunctionallyEquals_whenHashcodeEvaluated_thenSimilarHashCodeValue() throws Exception {
	String name = "Men In Black";
	CompanyNameObject companyLabel = new CompanyNameObject(name);
	CompanyNameObject companyLabelCopy = new CompanyNameObject(name);
	// Check functionnaly equals based on the unique attribute value
	assertEquals(companyLabel, companyLabelCopy);
	// Check hascode unicity calculated from the same attribute value
	assertEquals(companyLabel.hashCode(), companyLabelCopy.hashCode(),
		"Should be the same because from same attribute value!");
    }
}
