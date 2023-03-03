package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test of StringBasedNaturalKeyBuild behaviors regarding its supported
 * requirements.
 * 
 * @author olivier
 *
 */
public class StringBasedNaturalKeyBuilderUseCaseTest {

    /**
     * Test the transformation reducing all upper characters.
     * 
     * @throws Exception
     */
    @Test
    public void givenNaturalKeyWithUpperCharacters_whenTransform_thenLowerCaseApplied() throws Exception {
	// Use a natural key including lower characters
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(
		"OIUYTRk jhgd'sf9èAZERTYUIOPMLKJHGFDSQWXCVBN8765à434/<:5LKKJHH");
	builder.convertAllLettersToLowerCase();
	String result = builder.getResult();
	// Check all upper characters were removed
	for (int i = 0; i < result.length(); i++) {
	    char c = result.charAt(i);
	    // Check ASCII value
	    assertFalse("Invalid character existent in the transformed value!", (c >= 65 && c <= 90));
	}
    }

    /**
     * Test the transformation reducing all special characters.
     * 
     * @throws Exception
     */
    @Test
    public void givenNaturalKeyWithPunctuationMarks_whenTransform_thenRemoved() throws Exception {
	// Generate a list of characters
	int asciiFrom = 0;
	int asciiTo = 255;// including special characters codes
	StringBuffer b = new StringBuffer();
	for (int asciiCode = asciiFrom; asciiCode <= asciiTo; asciiCode++) {
	    b.append(Character.toString((char) asciiCode));
	}
	// Use a natural key including punctuation marks
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(b.toString());
	// Execute first step initializing the transformed first version
	builder.convertAllLettersToLowerCase();
	builder.dropPunctuationMarks();
	String result = builder.getResult();
	// Check all special and punctuations characters were removed
	for (int i = 0; i < result.length(); i++) {
	    char c = result.charAt(i);
	    // Check ASCII value
	    assertFalse("Invalid character existent in the transformed value!",
		    (c <= 47 || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c > 122)));
	}
    }

    /**
     * Test the transformation does not alterate key which have not special
     * characters originally.
     * 
     * @throws Exception
     */
    @Test
    public void givenNaturalKeyWithoutSpecialChar_whenTransform_thenOriginalMaintained() throws Exception {
	// Set original without special character included
	String originalKey = "klhgjdsiusdy8765434ih87654MLKJHGF";
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(originalKey);
	builder.convertAllLettersToLowerCase();
	String originalLowerCase = originalKey.toLowerCase();
	// Execute punctuation marks reduction
	builder.dropPunctuationMarks();
	String result = builder.getResult();
	// Check that none lost char
	assertEquals(originalLowerCase, result);
    }

    /**
     * Test the transformation rule regarding space and blank characters removing.
     * 
     * @throws Exception
     */
    @Test
    public void givenNaturalKeyWithSpaceChar_whenTransform_thenRemoved() throws Exception {
	// Define key included space and blank characters
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(" ihygf h ' k ");
	// Execute first step initializing the transformed first version
	builder.convertAllLettersToLowerCase();
	String result = builder.getResult();
	// Check always existent spaces not removed by punctuation steo
	assertTrue("Blank character shall alway exist!", result.contains(" "));
	// Execute cleaning of spaces
	builder.removeAnySpace();
	result = builder.getResult();
	// Check any blank characters removed
	assertFalse("Blank characters shall had been removed!", result.contains(" "));
    }

}
