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
		"OIUYTRk jhgd'sf9èAZERTYUIOPMLKJHGFDSQWXCVBN8765à434/<:5LKKJHH", 50);
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
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(b.toString(), 10);
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
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(originalKey, 50);
	String originalLowerCase = originalKey.toLowerCase();
	// Execute punctuation marks reduction
	builder.dropPunctuationMarks();
	builder.convertAllLettersToLowerCase();
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
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(" ihygf h ' k ", 10);
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

    /**
     * Test the transformation rule adding randomized characters
     * 
     * @throws Exception
     */
    @Test
    public void givenNaturalKeyWithNotSufficientChar_whenTransform_thenAdditionalRandomChart() throws Exception {
	// Define key with not sufficient char length
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder("aaaaa", 30);
	// Execute rule about min characters complement
	builder.generateMinimumCharactersQuantity();
	String result = builder.getResult();
	// Check minimum characters length are generated
	assertTrue("Invalid quantity of char in transformed text!", result.length() == 30);
    }

    /**
     * Test that transformation result read without transformed process is refused.
     * 
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void givenNaturalKey_whenTransformNotExecuted_thenException() throws Exception {
	StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder("aaaaa", 30);
	builder.getResult();
    }

}
