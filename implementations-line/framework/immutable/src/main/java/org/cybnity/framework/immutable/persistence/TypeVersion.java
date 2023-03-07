package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.NaturalKeyIdentifierGenerator;
import org.cybnity.framework.immutable.StringBasedNaturalKeyBuilder;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a structural version of object type (e.g fact class, domain event
 * class).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class TypeVersion {

    /**
     * Hash value of an object type
     */
    private String hash;

    /**
     * Auto-generated identifier of this version.
     */
    private String id;

    /**
     * Configuration about the minimum number of characters for identifier
     * generation process.
     */
    static private int minLetterQty = 20;

    /**
     * Category of origin subject.
     */
    private FactType factType;

    /**
     * Default constructor of a version.
     * 
     * @param subject    Mandatory type regarding the subject of versioning.
     * @param identifier Optional identifier of this version. When not defined, a
     *                   location-independent identifier is automatically generated
     *                   (based on subject natural key).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TypeVersion(Class<?> subject, String identifier) throws IllegalArgumentException {
	if (subject == null)
	    throw new IllegalArgumentException("The subject parameter is required!");
	this.factType = new FactType(subject.getName());
	// Generate hash value regarding the subject
	this.hash = new VersionConcreteStrategy().composeCanonicalVersionHash(subject);
	// Get or generate optional identifier of this version
	this.id = identifier;
	if (this.id == null || this.id.equals("")) {
	    try {
		// Generate automatic location-independant identifier regarding the subject type
		// label
		StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(subject.getName(),
			minLetterQty);
		NaturalKeyIdentifierGenerator gen = new NaturalKeyIdentifierGenerator(builder);
		gen.build();
		id = builder.getResult();
	    } catch (Exception e) {
		// Generation problem that should never arrive because build() method called
		// before result read

		// TODO: Add technical log in case of implementation evolution of the
		// builder.getResult() method usage requirements
	    }
	}
    }

    /**
     * Default constructor of a fact category with a location-independent identifier
     * automatically generated (based on categoryName natural key).
     * 
     * @param subject Mandatory type regarding the subject of versioning.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TypeVersion(Class<?> subject) throws IllegalArgumentException {
	this(subject, null);
    }

    /**
     * Get the type of origin fact.
     * 
     * @return A categorized type of fact (e.g based on original event class name).
     */
    public FactType factType() {
	return factType;
    }

    /**
     * Get the version hash value of the subject.
     * 
     * @return A hashed value (base64 encoded).
     */
    public String hash() {
	return this.hash;
    }

    /**
     * Get this version identifier.
     * 
     * @return An identifier.
     */
    public String id() {
	return this.id;
    }

}
