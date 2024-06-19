package org.cybnity.feature.defense_template.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReferentialUseCaseTest {

	@Test
	public void givenNullLabel_whenConstructor_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Create instance with null name
			new Referential(/** null label **/
					null, "acron");
		});
	}

	@Test
	public void givenEmptyLabel_whenConstructor_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Create instance with null name
			new Referential(/** empty label **/
					"", "acron");
		});
	}

	/**
	 * Verify that same referential is detected from same label and acronym.
	 */
	@Test
	public void givenSameLabelAndAcroReferential_whenEquals_thenTrue() {
		Referential ref = new Referential("National Institute of Standards and Technology", "NIST");
		Referential ref2 = new Referential("National Institute of Standards and Technology", "NIST");
		assertEquals(ref, ref2);
	}

	/**
	 * Verify that same referential is detected from same name only.
	 */
	@Test
	public void givenSameLabelReferential_whenEquals_thenTrue() {
		Referential ref = new Referential("National Institute of Standards and Technology", null);
		Referential ref2 = new Referential("National Institute of Standards and Technology", null);
		assertEquals(ref, ref2);
	}

	/**
	 * Verify that same label but different acronym is detected as not equals
	 * referential.
	 */
	@Test
	public void givenDifferentAcronym_whenEquals_thenFalse() {
		Referential ref = new Referential("National Institute of Standards and Technology", null);
		Referential ref2 = new Referential("National Institute of Standards and Technology", "NIST");
		assertNotEquals(ref, ref2);

		ref = new Referential("National Institute of Standards and Technology", "NIST");
		ref2 = new Referential("National Institute of Standards and Technology", "NIST2");
		assertNotEquals(ref, ref2);
	}
}
