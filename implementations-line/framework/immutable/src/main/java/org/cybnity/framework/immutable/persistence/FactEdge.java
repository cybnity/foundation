package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a link between two fact record defining an existent predecessors
 * relationship between two facts records. This aggregation object allow to
 * attach successors with predecessors facts similar to relations between saved
 * events in each fact record.
 * 
 * For example, on edge is existing for each predecessor relationship of a fact
 * regarding composition, aggregation, relation links referenced via
 * EntityReference attributes (e.g with external other entities or value
 * objects).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class FactEdge implements Unmodifiable, IVersionable, IUniqueness, Serializable {
    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(FactEdge.class).hashCode();

    /**
     * Edge point of the relation with the succeeding fact.
     */
    private String successorId;
    /**
     * Edge point of the relation with the preceding fact.
     */
    private String predecessorId;

    /**
     * Role defining the relation typ between the successor and the predecessor. It
     * is a classification (e.g of common naming for graph edges "to->from" vs
     * "in->out" vs "source->target") of this edge (relation type and naming).
     */
    private RelationRole factsRelationType;

    /**
     * Default constructor of a relation between facts.
     * 
     * @param successorFactIdentifier   Mandatory reference identifier of the fact which is
     *                                  successor to the preceding fact.
     * @param predecessorFactIdentifier Mandatory reference identifier of the fact which is
     *                                  predecessor of the successor fact.
     * @param factsRelationType         Mandatory type of relation regarding this
     *                                  edge.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public FactEdge(String successorFactIdentifier, String predecessorFactIdentifier, RelationRole factsRelationType)
	    throws IllegalArgumentException {
	if (successorFactIdentifier == null)
	    throw new IllegalArgumentException("successorFactIdentifier parameter is required!");
	if (predecessorFactIdentifier == null)
	    throw new IllegalArgumentException("predecessorFactIdentifier parameter is required!");
	if (factsRelationType == null)
	    throw new IllegalArgumentException("factsRelationType parameter is required!");
	this.successorId = successorFactIdentifier;
	this.predecessorId = predecessorFactIdentifier;
	this.factsRelationType = factsRelationType;
    }

    @Override
    public Set<Field> basedOn() {
	Set<Field> uniqueness = new HashSet<>();
	try {
	    uniqueness.add(this.getClass().getDeclaredField("successorId"));
	    uniqueness.add(this.getClass().getDeclaredField("predecessorId"));
	    uniqueness.add(this.getClass().getDeclaredField("factsRelationType"));
	} catch (NoSuchFieldException e) {
	    // Problem of implementation that shall never be thrown
	    // TODO: add log for developer error notification
	}
	return uniqueness;
    }

    /**
     * Get the naming of this edge as relation between the successor and the
     * predecessor.
     * 
     * @return A named relation type.
     */
    public RelationRole factsRelationType() {
	return this.factsRelationType;
    }

    /**
     * Get the identifier of the fact which is predecessor of the successor fact.
     * 
     * @return An identifier.
     */
    public String predecessorId() {
	return predecessorId;
    }

    /**
     * Get the identifier of the fact which is successor to the predecessor fact.
     * 
     * @return An identifier.
     */
    public String successorId() {
	return this.successorId;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new FactEdge(this.successorId, this.predecessorId, this.factsRelationType);
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }
}
