package org.cybnity.framework.domain.model;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represents a scope of informations set which can be mutable (e.g domain
 * entity aggregating value objects and/or entities reference), or immutable
 * (e.g entity reference).
 * 
 * An aggregate root instance can be persistent (e.g saving a state of mutable
 * properties as a version of a scope relative to value objects in a relation
 * that make sense for a domain topic) or can be only a dynamic scope of current
 * version of immutable value objects without need of state save (e.g temporary
 * aggregate root is only generated on-fly to maintain capability to be
 * recontacted during use, but that is not persisted in infrastructure layer).
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
public abstract class Aggregate extends CommonChildFactImpl implements IAggregate, Serializable, IVersionable {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Aggregate.class).hashCode();

	public Aggregate(Entity predecessor, Identifier id) throws IllegalArgumentException {
		super(predecessor, id);
	}

	public Aggregate(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
		super(predecessor, identifiers);
	}

}
