package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.NaturalKeyIdentifierGenerator;
import org.cybnity.framework.immutable.StringBasedNaturalKeyBuilder;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a category of fact (e.g based on fact class type). Allow to
 * identify a type of fact (e.g DomainEvent, Command) which is persisted as a
 * fact.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class FactType {

    /**
     * Label identifying an unique name regarding a category of fact (e.g name of
     * class regarding a concrete event like <<EventType>><<Fact State>> (e.g
     * OrderConfirmed).
     */
    private String name;

    /**
     * Auto-generated identifier of this fact type.
     */
    private String id;

    /**
     * Configuration about the minimum number of characters for identifier
     * generation process.
     */
    static private int minLetterQty = 20;

    /**
     * Default constructor of a fact category.
     * 
     * @param categoryName Mandatory label regarding the fact type logical naming.
     * @param identifier   Optional identifier of this fact type. When not defined,
     *                     a location-independent identifier is automatically
     *                     generated (based on categoryName natural key).
     * @throws IllegalArgumentException When mandatory parameter is missing (e.g
     *                                  null or empty).
     */
    public FactType(String categoryName, String identifier) throws IllegalArgumentException {
	if (categoryName == null || categoryName.equals(""))
	    throw new IllegalArgumentException("The categoryName parameter is required!");
	this.name = categoryName;
	this.id = identifier;
	if (this.id == null || this.id.equals("")) {
	    try {
		// Generate automatic location-independant identifier regarding the fact type
		// label
		StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(this.name, minLetterQty);
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
     * @param categoryName Mandatory label regarding the fact type logical naming.
     * @throws IllegalArgumentException When mandatory parameter is missing (e.g
     *                                  null or empty).
     */
    public FactType(String categoryName) throws IllegalArgumentException {
	this(categoryName, null);
    }

    /**
     * Get the name of this fact type.
     * 
     * @return A label.
     */
    public String name() {
	return this.name;
    }

    /**
     * Get this fact type identifier.
     * 
     * @return An identifier.
     */
    public String id() {
	return this.id;
    }

}
