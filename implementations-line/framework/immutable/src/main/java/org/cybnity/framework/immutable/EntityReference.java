package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Represent a mutable relationship between entities.
 * <p>
 * An entity reference is a property that points to another entity (e.g in
 * Domain-Driven design, the referenced entity is typically an aggregate root,
 * possibly in a different bounded context).
 * <p>
 * A less constrained one-to-many relationship (e.g without cascaded delete
 * constraint as represented by
 * {@link org.cybnity.framework.immutable.ChildFact}), especially one that
 * allows NULL, is probably an Entity Reference.
 * <p>
 * Related pattern: this is a variant of the Mutable
 * ({@link org.cybnity.framework.immutable.MutableProperty}) pattern in which
 * the value of the property is a reference to another entity. This is sometimes
 * used as an alternative to the Membership pattern to indicate that an entity
 * should be a member of only one group.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public class EntityReference implements IHistoricalFact {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(EntityReference.class).hashCode();

    /**
     * The owner of this reference, as primary entity.
     */
    protected Entity entity;

    /**
     * Optional referenced other entity that is often nullable.
     */
    private Entity referencedRelation;

    /**
     * Where the changed versions of this entity reference (include other reference
     * facts that refer to the same primary entity) are historized as predecessors.
     * In the chain of version, each prior item points back to its immediate
     * predecessor.
     * <p>
     * When a node computes a tree with multiples leaves, it recognizes a concurrent
     * change. In this situation, the application will typically present all leaves
     * as candidate values where each one represents a value that was concurrently
     * set for the property and has not been superseded. The user can select among
     * the candidate values and resolve the dispute (can also be accomplished via a
     * simple function over the leaves, such as a maximum equals to a several-way
     * merge).
     * <p>
     * Facts are only generated as a result of a user's decision. When the user
     * changes a property from a concurrent state, the system includes all of the
     * leaves of the tree in the newt fact's prior set (value attribute).
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3")
    protected HashSet<EntityReference> prior;

    /**
     * Identify this reference value had been confirmed (e.g during a merging
     * conflict resolution act decided by a user) as official current version.
     * {@link org.cybnity.framework.immutable.HistoryState.COMMITTED} by default for
     * any new instance of new instantiated property.
     */
    private HistoryState historyStatus = HistoryState.COMMITTED;

    /**
     * When this relation fact was created or observed.
     */
    protected OffsetDateTime changedAt;

    /**
     * Default constructor with automatic initialization of an empty value set
     * (prior chain).
     *
     * @param referenceOwner Mandatory entity which is subject of reference.
     * @param inRelationWith Optional other entity pointed by this reference owner.
     * @param status         Optional state of this property version. If null,
     *                       {@link org.cybnity.framework.immutable.HistoryState.COMMITTED}
     *                       is defined as default state.
     * @param createdAt      Optional date of creation. When null, this reference creation date is initialized to now.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  can't be cloned regarding immutable entity
     *                                  parameter(s).
     */
    public EntityReference(Entity referenceOwner, Entity inRelationWith, HistoryState status, OffsetDateTime createdAt)
            throws IllegalArgumentException {
        if (referenceOwner == null)
            throw new IllegalArgumentException("referenceOwner mandatory parameter is missing!");
        try {
            this.entity = (Entity) referenceOwner.immutable();
            this.setReferencedRelation(inRelationWith);
            // Set of prior versions is empty by default
            this.prior = new HashSet<>();
            if (status != null)
                this.historyStatus = status;
            // Create immutable time of this property changed version
            this.changedAt = (createdAt != null) ? createdAt : OffsetDateTime.now();
        } catch (ImmutabilityException ce) {
            throw new IllegalArgumentException(ce);
        }
    }

    /**
     * Constructor of new reference value version linked to previous versions of
     * this reference.
     *
     * @param referenceOwner Mandatory entity which is subject of reference.
     * @param inRelationWith Optional other entity pointed by this reference owner.
     * @param status         Optional state of this property version. If null,
     *                       {@link org.cybnity.framework.immutable.HistoryState.COMMITTED}
     *                       is defined as default state.
     * @param createdAt      Optional date of creation. When null, this reference creation date is initialized to now.
     * @param predecessors   Optional original instances (previous versions) that
     *                       were to consider in the history chain, regarding this
     *                       reference and that were identified as reference's
     *                       original states which had been changed. It's possible
     *                       that new instance (e.g in
     *                       {@link org.cybnity.framework.immutable.HistoryState.MERGED}
     *                       status) is based on several merged versions of previous
     *                       reference's states (e.g in case of concurrently changed
     *                       version with need of conflict resolution). Ignored if
     *                       null.
     * @throws IllegalArgumentException When mandatory parameter is missing; when
     *                                  can't be cloned regarding immutable entity
     *                                  parameter(s).
     */
    public EntityReference(Entity referenceOwner, Entity inRelationWith, HistoryState status, OffsetDateTime createdAt,
                           @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") EntityReference... predecessors)
            throws IllegalArgumentException {
        // Initialize instance as default without previous versions of property's values
        this(referenceOwner, inRelationWith, status, createdAt);
        if (predecessors != null) {
            // Manage the possible parallel concurrently previous state (e.g regarding
            // original previous values of this property that were evaluated to decide this
            // new value)
            for (EntityReference p : predecessors) {
                if (p != null) {
                    this.prior.add(p);
                }
            }
        }
    }

    /**
     * This property version state (e.g in a situation of concurrent change of
     * predecessor values requiring merging of new status to fix the new value of
     * this one) regarding its anterior versions.
     *
     * @return Official version of this property.
     * {@link org.cybnity.framework.immutable.HistoryState.COMMITTED} by
     * default.
     */
    public HistoryState historyStatus() {
        return historyStatus;
    }

    /**
     * Set the state of this property value as an official version of the history
     * chain, or shall only be saved in history as a concurrent version which had
     * been archived following a merging or reject decision (e.g by a user in front
     * of several potential changes requested concurrently of the prior version).
     *
     * @param state Status of this version as current version property in front of
     *              potential other parallel/concurrent versions regarding a same
     *              priors.
     */
    public void setHistoryStatus(HistoryState state) {
        this.historyStatus = state;
    }

    /**
     * Get the owner of this entity reference.
     *
     * @return This reference owner (immutable version).
     * @throws ImmutabilityException When impossible instantiation of immutable
     *                               version of instance returned.
     */
    public Entity getEntity() throws ImmutabilityException {
        return (Entity) entity.immutable();
    }

    /**
     * Get optional known relation with this entity owner.
     *
     * @return A referenced relation (e.g initiator of the entity) immutable version
     * or null.
     * @throws ImmutabilityException When impossible creation of immutable copy of
     *                               the current relation.
     */
    public Entity getReferencedRelation() throws ImmutabilityException {
        Entity otherRelated = null;
        if (referencedRelation != null) {
            otherRelated = (Entity) this.referencedRelation.immutable();
        }
        return otherRelated;
    }

    /**
     * Define an optional related entity that is in relation with this entity
     * reference.
     *
     * @param referencedRelation A related other entity.
     */
    protected void setReferencedRelation(Entity referencedRelation) {
        this.referencedRelation = referencedRelation;
    }

    /**
     * Get an immutable version of this entity reference. The returned version does
     * not include all the predecessors versions history.
     */
    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new EntityReference((Entity) this.entity.immutable(),
                (this.referencedRelation != null ? (Entity) this.referencedRelation.immutable() : null),
                this.historyStatus(), this.occurredAt());
    }

    /**
     * Get the date regarding the last change observed on this reference.
     */
    @Override
    public OffsetDateTime occurredAt() {
        return this.changedAt;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    /**
     * Redefine equality as based on this owner attribute equals() method that is
     * original source entity of this reference.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj != null && obj.getClass().equals(getClass())) {
            EntityReference compared = (EntityReference) obj;
            return this.entity.equals(compared.entity);
        }
        return false;
    }

}
