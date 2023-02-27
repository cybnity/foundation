package org.cybnity.framework.immutable;

import java.security.InvalidParameterException;
import java.time.OffsetDateTime;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * The membership is a more flexible grouping relationship than the ownership. A
 * many-to-many relationship typically denotes Membership. The relationship
 * between the member and the group is represented as a fact having both the
 * member and group as predecessors. It has an additional identifier (e.g
 * timestamp) that allows a member to be removed and re-added to a group over
 * time.
 * 
 * This fact is a successor of both a group and a member. The group and member
 * are not causally related.
 * 
 * When a member shal leave an assigned group as Membership fact, a <<Membership
 * Name>>Deletion fact ({@link org.cybnity.framework.immutable.DeletionFact}) is
 * created with membership fact as predecessor.
 * 
 * Related patterns: if the model requires that the entity be a member of only
 * one group, and that group cannot change, then consider using the Ownership
 * pattern instead. If the model requires that membership in one group be
 * replaced with membership in another group, then consider applying the Entity
 * Reference pattern. Model membership the group as a reference to the group
 * fact, superseding prior references for the same entity.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class Membership implements HistoricalFact {

    private static final long serialVersionUID = 1L;
    /**
     * When the relation was created.
     */
    protected OffsetDateTime createdAt;
    private final Member member;
    private final Group group;

    /**
     * Default constructor.
     * 
     * @param member Mandatory member of the relationship.
     * @param group  Mandatory group that categorized the member.
     * @throws IllegalArgumentException When a mandatory parameter is null, not
     *                                  immutable or does not include identifier.
     */
    public Membership(Member member, Group group) throws IllegalArgumentException {
	if (member == null)
	    throw new IllegalArgumentException(new InvalidParameterException("Member parameter is required!"));
	if (group == null)
	    throw new IllegalArgumentException(new InvalidParameterException("Group parameter is required!"));
	// Check conformity of identifiers
	if (member.identified() == null)
	    throw new IllegalArgumentException(new InvalidParameterException("Member's identifier is missing!"));
	if (group.identified() == null)
	    throw new IllegalArgumentException(new InvalidParameterException("Group's identifier is missing!"));
	try {
	    this.group = (Group) group.immutable();
	    this.member = (Member) member.immutable();
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Get the member of this relation.
     * 
     * @return An immutable version of the group's member.
     * @throws CloneNotSupportedException If impossible creation of immutable copy
     *                                    of the returned instance.
     */
    public Member member() throws CloneNotSupportedException {
	return (Member) this.member.immutable();
    }

    /**
     * Get the group categorizing this relation.
     * 
     * @return An immutable version of the group.
     * @throws CloneNotSupportedException If impossible creation of immutable copy
     *                                    of the returned instance.
     */
    public Group group() throws CloneNotSupportedException {
	return (Group) this.group.immutable();
    }

    /**
     * Default implementation of fact date when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
	// Return immutable value of the fact time
	return OffsetDateTime.parse(this.createdAt.toString());
    }
}
