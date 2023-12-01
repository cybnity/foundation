package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.UUID;

/**
 * Represent implementation class regarding the build of a value transformed
 * from a String natural key.
 * 
 * This class implements each step of transformation involved into the
 * canonicalization process.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_2")
public class StringBasedNaturalKeyBuilder extends LocationIndependentIdentityNaturalKeyBuilder {

    /**
     * Current version of natural key processed by the build rules.
     */
    private String transformationResult;

    private int minCharacters = 0;
    
    /**
     * Status of transformation
     **/
    private boolean isPartialTransformationStarted = false;

    /**
     * Default constructor regarding a natural key value.
     * 
     * @param aNaturalKey  Mandatory text selected from domain as natural key of a
     *                     subject. To identify the natural keys, examine the domain
     *                     that is modeled in an application and select an attribute
     *                     that uniquely identifies concepts in that domain. A
     *                     defined attribute shall be immutable in real world to be
     *                     considered as usable as a natural key within the model. A
     *                     good natural key is the canonical name of that tag in a
     *                     primary language (e.g English).
     * @param minLetterQty How many minimum characters shall be generated as
     *                     transformation result.
     * @throws IllegalArgumentException When mandatory parameter is missing or
     *                                  empty.
     */
    public StringBasedNaturalKeyBuilder(String aNaturalKey, int minLetterQty) throws IllegalArgumentException {
	if (aNaturalKey == null || aNaturalKey.equals(""))
	    throw new IllegalArgumentException("Natural key parameter is required!");
	this.transformationResult = aNaturalKey.toString();
	this.minCharacters = minLetterQty;
    }

    /**
     * Apply the transformation rule for uniformity of the characters of the natural
     * key with lower casing of all the characters of the natural key.
     */
    @Override
    public void convertAllLettersToLowerCase() {
	this.transformationResult = this.transformationResult.toLowerCase();
	this.isPartialTransformationStarted = true;
    }

    /**
     * Apply a transformation rule for removing of any character (based on ASCII
     * codes evaluation)
     */
    @Override
    public void dropPunctuationMarks() {
	// Method based on ASCII evaluation of each character
	char c = 0;
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < this.transformationResult.length(); i++) {
	    // Select next character
	    c = this.transformationResult.charAt(i);
	    if (c < 48 || (c > 57 && c < 65) || (c > 90 && c < 97) || (c > 122)) {
		// It's a special character, so ignore it
	    } else {
		// Retain
		buffer.append(Character.toString(c));
	    }
	}
	this.transformationResult = buffer.toString();
	this.isPartialTransformationStarted = true;
    }

    /**
     * Apply a transformation rule for removing any type of space in the natural
     * key.
     */
    @Override
    public void removeAnySpace() {
	// According to Unicode standards there are various space characters having
	// ASCII value more than 32(‘U+0020’). Ex: 8193(U+2001)
	this.transformationResult = transformationResult.strip().trim().replaceAll("\\s", "");
	this.isPartialTransformationStarted = true;
    }

    @Override
    public void generateMinimumCharactersQuantity() {
	// Verify if minimum quantity of characters is reached
	if (this.transformationResult.length() < this.minCharacters) {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(this.transformationResult);
	    while (buffer.length() < this.minCharacters) {
		// Generate complementary random char and add to end of current transformed text
		buffer.append(UUID.randomUUID().toString());
	    }
	    // Reduce final result to the minimum characters quantity required
	    this.transformationResult = buffer.substring(0, this.minCharacters);
	}
	this.isPartialTransformationStarted = true;
    }

    /**
     * Get the result of transformed information based on the original natural key.
     * 
     * @return The transformed version of the natural key after executed build
     *         process.
     * @throws Exception If transformation process was not executed before to
     *                   acquire the result.
     */
    public String getResult() throws Exception {
	if (!this.isPartialTransformationStarted)
	    throw new Exception("The transformation process had not been executed!");
	return this.transformationResult;
    }
}
