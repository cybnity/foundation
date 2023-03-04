package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a Builder pattern implementation of natural keys usable as
 * location-independent identifier of domain object.
 * 
 * This building process execute the generation of an Identifier based on a
 * natural key according to the concrete implementation sub-class applying the
 * canonicalization process.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_2")
public abstract class LocationIndependentIdentityNaturalKeyBuilder {

    /**
     * Apply the transformation rule for uniformity of the characters of the natural
     * key with lower casing of all the characters of the natural key.
     */
    public abstract void convertAllLettersToLowerCase();

    /**
     * Apply a transformation rule for removing of any character (based on ASCII
     * codes evaluation)
     */
    public abstract void dropPunctuationMarks();

    /**
     * Apply a transformation rule for removing any type of space in the natural
     * key.
     */
    public abstract void removeAnySpace();

    /**
     * Apply transformation rule for add of random additional characters allowing to
     * complete the natural key transformed value with randomized characters.
     */
    public abstract void generateMinimumCharactersQuantity();
}
