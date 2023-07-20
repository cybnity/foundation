package org.cybnity.framework.domain.model;

import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represents a scope of informations providing attributes and/or capabilities
 * as a complex domain object.
 * 
 * An aggregate root of the process entity domain is defined via immutable
 * attributes (e.g ValueObject, EntityReference of other domains' objects,
 * ChildFact historical and identified fact) and/or mutable attributes (e.g
 * MutableProperty objects).
 * 
 * An aggregate root instance can be persistent (e.g saving a state of mutable
 * properties as a version of a scope relative to value objects in a relation
 * that make sense for a domain topic) and referenceable (e.g EntityReference
 * provided), or can be only a dynamic scope of current version of immutable
 * value objects without need of state save (e.g temporary aggregate root is
 * only generated on-fly to maintain capability to be recontacted during use,
 * but that is not persisted in infrastructure layer).
 * 
 * An entity defining the aggregate root allow to uniquely identify a persistent
 * aggregate.
 * 
 * For example, an identifiable aggregate scope can be retrieved from an
 * original unique identifier defining during its instantiation (persistence
 * capable) or can generate on-fly an identifier of the scope dynamically (e.g
 * aggregation of multiple identifier of referenced instances of its represented
 * scope).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public abstract class Aggregate extends CommonChildFactImpl implements IAggregate {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Aggregate.class).hashCode();

	/**
	 * Default constructor.
	 * 
	 * @param predecessor Mandatory parent of this root aggregate entity.
	 * @param id          Unique and optional identifier of this entity. For
	 *                    example, identifier is required when the aggregate shall
	 *                    be persistent. Else, can be without identity when not
	 *                    persistent aggregate.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Aggregate(Entity predecessor, Identifier id) throws IllegalArgumentException {
		super(predecessor, id);
	}

	/**
	 * Specific partial constructor.
	 * 
	 * @param predecessor Mandatory parent of this root aggregate entity.
	 * @param identifiers Optional set of identifiers of this entity, that contains
	 *                    non-duplicable elements. For example, identifier is
	 *                    required when the aggregate shall be persistent. Else, can
	 *                    be without identity when not persistent aggregate.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value. When
	 *                                  predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Aggregate(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
		super(predecessor, identifiers);
	}

	@Override
	public EntityReference root() throws ImmutabilityException {
		// Read the identity of this root aggregate domain object
		DomainEntity aggregateRootEntity = rootEntity();
		EntityReference ref = null;
		if (aggregateRootEntity != null) {
			// Build an identification reference
			ref = aggregateRootEntity.reference();
		} // Else it's an aggregate representing a dynamic domain boundary without
			// persistence capability
		return ref;
	}

	/**
	 * Get the identity of this aggregate when existing.
	 * 
	 * @return A domain entity identity instance based on current identifier of the
	 *         aggregate root. Null when not identifier defined regarding this
	 *         aggregate.
	 * @throws ImmutabilityException
	 */
	protected DomainEntity rootEntity() throws ImmutabilityException {
		// Read the identity of this root aggregate domain object
		Identifier id = this.identified();
		DomainEntity aggregateRootEntity = null;
		if (id != null) {
			aggregateRootEntity = new DomainEntity(id);
		} // Else it's an aggregate representing a dynamic domain boundary without
			// persistence capability
		return aggregateRootEntity;
	}
}
