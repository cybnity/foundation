package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a producer of Identifier based on natural key. The name is
 * canonicalized by converting all letters to lower case, dropping punctuation
 * marks, and replacing spaces with hyphens to make them more URL friendly.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_2")
public class NaturalKeyIdentifierGenerator {

    private LocationIndependentIdentityNaturalKeyBuilder builder;

    /**
     * Default constructor of generator managing the build of an Identifier based on
     * natural key concrete builder.
     * 
     * @param builder Mandatory builder to use during the generation of Identifier,
     *                and that implement canonicalization rules transforming a
     *                natural key into a usable identifier.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public NaturalKeyIdentifierGenerator(LocationIndependentIdentityNaturalKeyBuilder builder)
	    throws IllegalArgumentException {
	if (builder == null)
	    throw new IllegalArgumentException("Builder parameter is required!");
	this.builder = builder;
    }

    /**
     * Prepare a canonical key usable as identifier with execution of the build
     * process via builder.
     */
    public void build() {
	// Execute the build process according to the steps of canonicalization
	builder.convertAllLettersToLowerCase();
	builder.dropPunctuationMarks();
	builder.removeAnySpace();
	builder.generateMinimumCharactersQuantity();
    }

}
