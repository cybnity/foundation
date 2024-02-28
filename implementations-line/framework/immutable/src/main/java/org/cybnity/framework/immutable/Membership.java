package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.time.OffsetDateTime;

/**
 * The membership is a more flexible grouping relationship than the ownership. A
 * many-to-many relationship typically denotes Membership. The relationship
 * between the member and the group is represented as a fact having both the
 * member and group as predecessors. It has an additional identifier (e.g
 * timestamp) that allows a member to be removed and re-added to a group over
 * time.
 * <p>
 * This fact is a successor of both a group and a member. The group and member
 * are not causally related.
 * <p>
 * When a member shall leave an assigned group as Membership fact, a <<Membership
 * Name>>Deletion fact ({@link org.cybnity.framework.immutable.IDeletionFact})
 * is created with membership fact as predecessor.
 * <p>
 * Related patterns: if the model requires that the entity be a member of only
 * one group, and that group cannot change, then consider using the Ownership
 * pattern instead. If the model requires that membership in one group be
 * replaced with membership in another group, then consider applying the Entity
 * Reference pattern. Model membership the group as a reference to the group
 * fact, superseding prior references for the same entity.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class Membership implements IHistoricalFact {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(Membership.class).hashCode();
    /**
     * When the relation was created.
     */
    protected OffsetDateTime createdAt;
    private final IMember member;
    private final IGroup group;

    /**
     * Default constructor.
     *
     * @param member Mandatory member of the relationship.
     * @param group  Mandatory group that categorized the member.
     * @throws IllegalArgumentException When a mandatory parameter is null, not
     *                                  immutable or does not include identifier.
     */
    public Membership(IMember member, IGroup group) throws IllegalArgumentException {
        if (member == null)
            throw new IllegalArgumentException("Member parameter is required!");
        if (group == null)
            throw new IllegalArgumentException("Group parameter is required!");
        // Check conformity of identifiers
        if (member.identified() == null)
            throw new IllegalArgumentException("Member's identifier is missing!");
        if (group.identified() == null)
            throw new IllegalArgumentException("Group's identifier is missing!");
        try {
            this.group = (IGroup) group.immutable();
            this.member = (IMember) member.immutable();
            // Create immutable time of this fact creation
            this.createdAt = OffsetDateTime.now();
        } catch (ImmutabilityException ce) {
            throw new IllegalArgumentException(ce);
        }
    }

    /**
     * Get the member of this relation.
     *
     * @return An immutable version of the group's member.
     * @throws ImmutabilityException If impossible creation of immutable copy of the
     *                               returned instance.
     */
    public IMember member() throws ImmutabilityException {
        return (IMember) this.member.immutable();
    }

    /**
     * Get the group categorizing this relation.
     *
     * @return An immutable version of the group.
     * @throws ImmutabilityException If impossible creation of immutable copy of the
     *                               returned instance.
     */
    public IGroup group() throws ImmutabilityException {
        return (IGroup) this.group.immutable();
    }

    /**
     * Default implementation of fact date when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
        // Return immutable value of the fact time
        return this.createdAt;
    }
}
