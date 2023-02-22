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
 * Historical fact referencing (e.g as an aggregate) a parent as predecessor.
 * Low-cost way to documenting the desired owner relationship. The identity of
 * the parent is part of the identity of this child. Because prerequisites are
 * immutable, a child cannot be moved to another parent. Ownership relation
 * between a child and a parent is non-transferable. The parent must exist
 * before the child can be created.
 * 
 * The Ownership pattern (supporting a Child Fact existence) is a special case
 * of the Entity pattern, where the entity's identifiers include the identity of
 * an owner.
 * 
 * A one-to-many relationship that has a cascade delete constraint represents
 * Ownership.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class ChildFact implements HistoricalFact, IdentifiableFact {

    /**
     * Predecessor (as Owner of this child) of this child fact.
     */
    protected final Entity parent;

    /**
     * Required identification elements (e.g that can be combined to define a
     * natural identity of this historical fact). Related patterns: when an entity
     * includes the identity of its parent, it shall follows the Ownership pattern.
     * While the entity's fact cannot be deleted, the Delete pattern simulates the
     * removal of this entity.
     * 
     * This collection implementation contains non-duplicable elements at every
     * given time.
     */
    protected ArrayList<Identifier> identifiedBy;

    /**
     * When this fact was created or observed regarding the historized topic. This
     * creation date distingues among children within the same Owner.
     */
    protected OffsetDateTime createdAt;

    /**
     * Default constructor.
     * 
     * @param predecessor Mandatory parent of this child entity.
     * @param id          Unique and optional identifier of this entity.
     * @throws IllegalArgumentException When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public ChildFact(Entity predecessor, Identifier id) throws IllegalArgumentException {
	if (predecessor == null)
	    throw new IllegalArgumentException(new InvalidParameterException("parent parameter is required!"));
	// Check conformity of optional child identifier
	if (id != null && (id.name() == null || id.name().equals("") || id.value() == null)) {
	    throw new IllegalArgumentException(
		    new InvalidParameterException("Identifier parameter's name and value is required!"));
	}
	try {
	    // Check mandatory existent identifier of parent (child identifier based on its
	    // contribution)
	    Collection<Identifier> predecessorIdentifiers = predecessor.identifiers();
	    if (predecessor.identified() == null || predecessorIdentifiers == null || predecessorIdentifiers.isEmpty())
		throw new IllegalArgumentException("The parent identifier(s) shall be existent!");
	    // Reference immutable copy of predecessor
	    this.parent = (Entity) predecessor.immutable();
	} catch (CloneNotSupportedException cn) {
	    throw new IllegalArgumentException(cn);
	}

	// Get predecessor dependent identifier assignable to this child
	Identifier parentDependentId = generateIdentifierPredecessorBased(predecessor, id);
	if (parentDependentId == null)
	    throw new IllegalArgumentException(
		    new InvalidParameterException("Impossible identifier generation based on parent identifiers!"));
	// Check conformity of generated identifier
	if (parentDependentId.name() == null || parentDependentId.name().equals("")
		|| parentDependentId.value() == null) {
	    throw new IllegalArgumentException(
		    new InvalidParameterException("Child identifier based on parent shall include name and value!"));
	}
	try {
	    identifiedBy = new ArrayList<Identifier>(1);
	    identifiedBy.add((Identifier) parentDependentId.immutable());
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Default constructor.
     * 
     * @param predecessor Mandatory parent of this child entity.
     * @param identifiers Set of optional base identifiers of this entity, that
     *                    contains non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    public ChildFact(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	if (predecessor == null)
	    throw new IllegalArgumentException(new InvalidParameterException("parent parameter is required!"));
	try {
	    // Check mandatory existent identifier of parent (child identifier based on its
	    // contribution)
	    Collection<Identifier> predecessorIdentifiers = predecessor.identifiers();
	    if (predecessor.identified() == null || predecessorIdentifiers == null || predecessorIdentifiers.isEmpty())
		throw new IllegalArgumentException(
			new InvalidParameterException("The parent identifier(s) shall be existent!"));
	    // Reference immutable copy of predecessor
	    this.parent = (Entity) predecessor.immutable();
	} catch (CloneNotSupportedException cn) {
	    throw new IllegalArgumentException(cn);
	}
	Collection<Identifier> origins = null;
	if (identifiers != null && !identifiers.isEmpty()) {
	    origins = new LinkedHashSet<Identifier>(identifiers.size());
	    // Get immutable version of identifiers
	    try {
		for (Identifier id : identifiers) {
		    // Check conformity of identifier
		    if (id.name() == null || id.name().equals("") || id.value() == null) {
			throw new IllegalArgumentException(new InvalidParameterException(
				"Any child base identifier parameter's name and value is required!"));
		    }
		    // get a reference copy
		    origins.add((Identifier) id.immutable());
		}
	    } catch (CloneNotSupportedException cn) {
		throw new IllegalArgumentException(cn);
	    }
	}
	// Get predecessor dependent identifier assignable to this child
	Identifier parentDependentId = generateIdentifierPredecessorBased(predecessor, origins);
	if (parentDependentId == null)
	    throw new IllegalArgumentException("Impossible identifier generation based on parent identifiers!");
	// Check conformity of generated identifier
	if (parentDependentId.name() == null || parentDependentId.name().equals("")
		|| parentDependentId.value() == null) {
	    throw new IllegalArgumentException("Child identifier based on parent shall include name and value!");
	}
	try {
	    identifiedBy = new ArrayList<Identifier>(1);
	    identifiedBy.add((Identifier) parentDependentId.immutable());
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
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
	if (fact != null && IdentifiableFact.class.isAssignableFrom(fact.getClass())) {
	    // Compare equality based on each instance's identifier (unique or based on
	    // identifying informations combination)
	    return Evaluations.isIdentifiedEquals(this, (IdentifiableFact) fact);
	}
	return false;
    }

    /**
     * Generate a combined identifier regarding this child that is based on the
     * predecessor identifier (e.g reuse identifying information of parent's
     * identifiers).
     * 
     * The identity of the parent (Owner) is part of the identity of this child
     * (because prerequisites are immutable, child cannot be moved to another
     * parent).
     * 
     * The identity of a root Owner tends to become part of the identities of most
     * other entities.
     * 
     * @param predecessor     Mandatory parent of this child instance.
     * @param childOriginalId Optional base identifier of this child instance, which
     *                        can be combined by the final id generation process.
     * @return A predecessor dependent identifier which can be assigned to this
     *         child fact.
     * @throws IllegalArgumentException When mandatory parameter is null or missing
     *                                  identifying information.
     */
    protected abstract Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
	    throws IllegalArgumentException;

    /**
     * Generate a combined identifier regarding this child that is based on the
     * predecessor identifiers (e.g reuse identifying information of parent's
     * identifiers).
     * 
     * The identity of the parent (Owner) is part of the identity of this child
     * (because prerequisites are immutable, child cannot be moved to another
     * parent).
     * 
     * The identity of a root Owner tends to become part of the identities of most
     * other entities.
     * 
     * @param predecessor      Mandatory parent of this child instance.
     * @param childOriginalIds Optional base identifiers of this child instance,
     *                         which can be combined by the final id generation
     *                         process.
     * @return A predecessor dependent identifier which can be assigned to this
     *         child fact.
     * @throws IllegalArgumentException When mandatory parameter is null or missing
     *                                  identifying information.
     */
    protected abstract Identifier generateIdentifierPredecessorBased(Entity predecessor,
	    Collection<Identifier> childOriginalIds) throws IllegalArgumentException;

    /**
     * Predecessor fact of this child entity's identifier. The identity of a root
     * owner tends to become part of the identities of most other entities durign
     * their creation.
     * 
     * @return A predecessor of the child.
     * @throws CloneNotSupportedException When impossible cloned instance of
     *                                    predecessor.
     */
    public Entity parent() throws CloneNotSupportedException {
	// Return unmodifiable instance of predecessor
	return (Entity) this.parent.immutable();
    }

}