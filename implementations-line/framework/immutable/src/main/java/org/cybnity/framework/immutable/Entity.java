package org.cybnity.framework.immutable;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * An entity is a HISTORICAL FACT that contains only identifying information(s).
 * An entity does not contain mutable properties. Any mutable properties (e.g
 * eventual other properties of the entity are not stored within the fact
 * because that could be mutable) that should be associated with this entity are
 * applied with a subsequent fact (they are attached using the Mutable Property
 * pattern). If two nodes create entities with the same identifiers, then they
 * are the same entity. The nodes may not be aware of each other at the time of
 * creation, but any nodes who learn of the two entities will assume that they
 * are the same. If auditing information (such as the creator, location, or time
 * of creation) is added to this entity, than that becomes part of its identity.
 * Choosing to make that information part of the identity is one way of
 * circumventing the previous consequence (that two entities with the same
 * identifiers are the same entity). It's recommended to keep auditing
 * information outside of the facts themselves.
 * 
 * In synthesis, this class represent the creation of a historical fact.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class Entity implements HistoricalFact {

    /**
     * Required identification elements (e.g that can be combined to define a
     * natural identity of this historical fact). Related patterns: when an entity
     * includes the identity of its parent, it shall follows the Ownership pattern.
     * While the entity's fact cannot be deleted, the Delete pattern simulates the
     * removal of this entity.
     */
    private Collection<Identifier> identifiedBy;

    /**
     * Default constructor.
     * 
     * @param id Unique and mandatory identifier of this entity.
     * @throws IllegalArgumentException When id parameter is null and does not
     *                                  include name and value.
     */
    public Entity(Identifier id) throws IllegalArgumentException {
	if (id == null)
	    throw new IllegalArgumentException("Id parameter is required!");
	// Check conformity of identifier
	if (id.identifierName() == null || id.identifierName().equals("") || id.value() == null) {
	    throw new IllegalArgumentException("Identifier parameter's name and value are required!");
	}
	try {
	    identifiedBy = new LinkedHashSet<Identifier>(1);
	    identifiedBy.add(id.immutable());
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Default constructor.
     * 
     * @param identifiers Set of mandatory identifiers of this entity.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    public Entity(Collection<Identifier> identifiers) throws IllegalArgumentException {
	if (identifiers == null || identifiers.isEmpty())
	    throw new IllegalArgumentException("Identifiers parameter is required!");
	identifiedBy = new LinkedHashSet<Identifier>(identifiers.size());
	// Save immutable identifiers
	try {
	    for (Identifier id : identifiers) {
		// Check conformity of identifier
		if (id.identifierName() == null || id.identifierName().equals("") || id.value() == null) {
		    throw new IllegalArgumentException("Identifier parameter's name and value is required!");
		}
		// save reference copy
		identifiedBy.add(id.immutable());
	    }
	} catch (CloneNotSupportedException cn) {
	    throw new IllegalArgumentException(cn);
	}
    }

    @Override
    public Collection<Identifier> identifiers() {
	return identifiedBy;
    }

    /**
     * Redefine the comparison of this fact with another based on the identifier.
     * 
     * @param fact To compare.
     * @return True if this fact is based on the same identifier(s) as the fact
     *         argument; false otherwise.
     */
    @Override
    public boolean equals(Object fact) {
	if (fact != null && HistoricalFact.class.isAssignableFrom(fact.getClass())) {
	    HistoricalFact compared = (HistoricalFact) fact;
	    // Compare equality based on each instance's identifier (unique or based on
	    // identifying informations combination)
	    return this.identifiedBy.equals(compared.identified());
	}
	return false;
    }

}
