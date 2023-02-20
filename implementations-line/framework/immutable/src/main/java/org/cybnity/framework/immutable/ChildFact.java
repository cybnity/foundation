package org.cybnity.framework.immutable;

import java.util.Collection;
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
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class ChildFact implements HistoricalFact {

    /**
     * Predecessor of this child fact.
     */
    private HistoricalFact parent;

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
     * @param predecessor Mandatory parent of this child entity.
     * @param id          Unique and optional identifier of this entity.
     * @throws IllegalArgumentException When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public ChildFact(HistoricalFact predecessor, Identifier id) throws IllegalArgumentException {
	if (predecessor == null)
	    throw new IllegalArgumentException("parent parameter is required!");
	// Check conformity of identifier
	if (id != null && (id.identifierName() == null || id.identifierName().equals("") || id.value() == null)) {
	    throw new IllegalArgumentException("Identifier parameter's name and value is required!");
	}
	try {
	    // Reference immutable copy of predecessor
	    this.parent = predecessor.immutable();
	} catch (CloneNotSupportedException cn) {
	    throw new IllegalArgumentException(cn);
	}
	// Check mandatory existent identifier of parent (child identifier based on its
	// contribution)
	if (this.parent().identified() == null || this.parent().identifiers() == null
		|| this.parent().identifiers().isEmpty())
	    throw new IllegalArgumentException("The parent identifier(s) shall be existent!");

	// Get predecessor dependent identifier assignable to this child
	Identifier parentDependentId = generateIdentifierPredecessorBased(predecessor, id);
	if (parentDependentId == null)
	    throw new IllegalArgumentException("Impossible identifier generation based on parent identifiers!");
	// Check conformity of generated identifier
	if (parentDependentId.identifierName() == null || parentDependentId.identifierName().equals("")
		|| parentDependentId.value() == null) {
	    throw new IllegalArgumentException("Child identifier based on parent shall include name and value!");
	}
	try {
	    identifiedBy = new LinkedHashSet<Identifier>(1);
	    identifiedBy.add(parentDependentId.immutable());
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Default constructor.
     * 
     * @param predecessor Mandatory parent of this child entity.
     * @param identifiers Set of optional base identifiers of this entity.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    public ChildFact(HistoricalFact predecessor, Collection<Identifier> identifiers) throws IllegalArgumentException {
	if (predecessor == null)
	    throw new IllegalArgumentException("parent parameter is required!");
	try {
	    // Reference immutable copy of predecessor
	    this.parent = predecessor.immutable();
	} catch (CloneNotSupportedException cn) {
	    throw new IllegalArgumentException(cn);
	}
	// Check mandatory existent identifier of parent (child identifier based on its
	// contribution)
	if (this.parent().identified() == null || this.parent().identifiers() == null
		|| this.parent().identifiers().isEmpty())
	    throw new IllegalArgumentException("The parent identifier(s) shall be existent!");
	Collection<Identifier> origins = null;
	if (identifiers != null && !identifiers.isEmpty()) {
	    origins = new LinkedHashSet<Identifier>(identifiers.size());
	    // Get immutable version of identifiers
	    try {
		for (Identifier id : identifiers) {
		    // Check conformity of identifier
		    if (id.identifierName() == null || id.identifierName().equals("") || id.value() == null) {
			throw new IllegalArgumentException(
				"Any child base identifier parameter's name and value is required!");
		    }
		    // get a reference copy
		    origins.add(id.immutable());
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
	if (parentDependentId.identifierName() == null || parentDependentId.identifierName().equals("")
		|| parentDependentId.value() == null) {
	    throw new IllegalArgumentException("Child identifier based on parent shall include name and value!");
	}
	try {
	    identifiedBy = new LinkedHashSet<Identifier>(1);
	    identifiedBy.add(parentDependentId.immutable());
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
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

    /**
     * Generate a combined identifier regarding this child that is based on the
     * predecessor identifier (e.g reuse identifying information of parent's
     * identifiers).
     * 
     * @param predecessor     Mandatory parent of this child instance.
     * @param childOriginalId Optional base identifier of this child instance, which
     *                        can be combined by the final id generation process.
     * @return A predecessor dependent identifier which can be assigned to this
     *         child fact.
     */
    protected abstract Identifier generateIdentifierPredecessorBased(HistoricalFact predecessor,
	    Identifier childOriginalId);

    /**
     * Generate a combined identifier regarding this child that is based on the
     * predecessor identifiers (e.g reuse identifying information of parent's
     * identifiers).
     * 
     * @param predecessor      Mandatory parent of this child instance.
     * @param childOriginalIds Optional base identifiers of this child instance,
     *                         which can be combined by the final id generation
     *                         process.
     * @return A predecessor dependent identifier which can be assigned to this
     *         child fact.
     */
    protected abstract Identifier generateIdentifierPredecessorBased(HistoricalFact predecessor,
	    Collection<Identifier> childOriginalIds);

    /**
     * Predecessor fact of this child entity's identifier. The identity of a root
     * owner tends to become part of the identities of most other entities durign
     * their creation.
     * 
     * @return A predecessor of the child.
     */
    public HistoricalFact parent() {
	return this.parent;
    }

}
