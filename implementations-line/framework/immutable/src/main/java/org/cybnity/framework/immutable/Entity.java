package org.cybnity.framework.immutable;

import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public abstract class Entity implements HistoricalFact, IdentifiableFact, Referenceable {

    private static final long serialVersionUID = 1L;

    /**
     * Required identification elements (e.g that can be combined to define a
     * natural identity of this historical fact).
     * 
     * Related patterns: when an entity includes the identity of its parent, it
     * shall follows the Ownership pattern. While the entity's fact cannot be
     * deleted, the Delete pattern simulates the removal of this entity.
     * 
     * This collection implementation contains non-duplicable elements at every
     * given time.
     */
    protected ArrayList<Identifier> identifiedBy;

    /**
     * When this fact was created or observed regarding the historized topic.
     */
    protected OffsetDateTime createdAt;

    /**
     * Default constructor.
     * 
     * @param id Unique and mandatory identifier of this entity.
     * @throws IllegalArgumentException When id parameter is null and does not
     *                                  include name and value.
     */
    protected Entity(Identifier id) throws IllegalArgumentException {
	if (id == null)
	    throw new IllegalArgumentException(new InvalidParameterException("Id parameter is required!"));
	// Check conformity of identifier
	if (id.name() == null || id.name().equals("") || id.value() == null) {
	    throw new IllegalArgumentException(
		    new InvalidParameterException("Identifier parameter's name and value are required!"));
	}
	try {
	    identifiedBy = new ArrayList<Identifier>(1);
	    identifiedBy.add((Identifier) id.immutable());
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (ImmutabilityException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Default constructor.
     * 
     * @param identifiers Set of mandatory identifiers of this entity, that contains
     *                    non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    protected Entity(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	if (identifiers == null || identifiers.isEmpty())
	    throw new IllegalArgumentException(new InvalidParameterException("Identifiers parameter is required!"));
	identifiedBy = new ArrayList<Identifier>(identifiers.size());
	// Save immutable identifiers
	try {
	    for (Identifier id : identifiers) {
		// Check conformity of identifier
		if (id.name() == null || id.name().equals("") || id.value() == null) {
		    throw new IllegalArgumentException(
			    new InvalidParameterException("Identifier parameter's name and value is required!"));
		}
		// save reference copy
		identifiedBy.add((Identifier) id.immutable());
	    }
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (ImmutabilityException cn) {
	    throw new IllegalArgumentException(cn);
	}
    }

    /**
     * A fact shall use location-independent identity. It cannot use
     * auto-incremented IDs, URLS, or any other location-dependent identifier.
     * 
     * @return One or multiple identification informations.
     */
    public Collection<Identifier> identifiers() {
	// Return immutable version
	return Collections.unmodifiableCollection(identifiedBy);
    }

    /**
     * Default implementation of fact date when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
	// Return immutable value of the fact time
	return this.createdAt;
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
	if (fact == this)
	    return true;
	if (fact != null && IdentifiableFact.class.isAssignableFrom(fact.getClass())) {
	    // Compare equality based on each instance's identifier (unique or based on
	    // identifying informations combination)
	    return Evaluations.isIdentifiedEquals(this, (IdentifiableFact) fact);
	}
	return false;
    }

    @Override
    public EntityReference reference() throws ImmutabilityException {
	try {
	    return new EntityReference((Entity) this.immutable(),
		    /* Unknown external relation with the caller of this method */ null, null);
	} catch (Exception e) {
	    throw new ImmutabilityException(e);
	}
    }
}
