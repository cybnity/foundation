package org.cybnity.framework.domain.model;

/**
 * Represents a scope of informations set which can be mutable, or immutable
 * (e.g entity reference).
 * 
 * An aggregate root instance can be persistent (e.g saving a state of mutable
 * properties as a version of a scope relative to value objects in a relation
 * that make sense for a domain topic) or can be only a dynamic scope of current
 * version of immutable value objects.
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
public abstract class AggregateRoot implements IAggregate {

}
