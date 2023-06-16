package org.cybnity.framework.domain.model;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * In a Domain-Driven Design (DDD), an aggregate defines a consistency boundary.
 * Typically is defined by classes in a
 * {@link org.cybnity.framework.domain.IWriteModel}. Aggregates are recipients
 * of multiple properties (e.g {@link org.cybnity.framework.domain.Command}, or
 * {@link org.cybnity.framework.domain.ValueObject}) and can be units of
 * persistence. For example, after an aggregate instance has processed a command
 * and its state has changed, the system shall persist the new state of the
 * instance to storage system.
 * 
 * An aggregate may consist of multiple related objects (e.g an order and
 * multiple order lines), all of which could be persisted together (e.g atomic
 * operation). When it's an aggregate of multiple types, one type is identified
 * as the aggregate root. The access to all of the objects shall be performed
 * over the aggregate root, that only hold references to the aggregate root.
 * Every aggregate instance should have a unique identifier when represents a
 * boundary with persistence capability.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IAggregate {

	/**
	 * Manage the execution of a command regarding the boundary managed by this
	 * aggregate. For example, execute a change (processing) of a property regarding
	 * this aggregate and publish notification (e.g to several domain event
	 * publishers) about changed information.
	 * 
	 * @param change Mandatory action to process.
	 * @param ctx    Context providing resources which could be required for command
	 *               treatment by this Aggregate.
	 * @throws IllegalArgumentException When parameter is not defined or is invalid
	 *                                  (e.g conformity problem).
	 */
	public void execute(Command change, IContext ctx) throws IllegalArgumentException;

	/**
	 * Get the reference to the aggregate root entity.
	 * 
	 * @return A reference or null (when the aggregate is not a persistent or
	 *         historical domain entity; in case of undefined identifier of dynamic
	 *         aggregate representing a domain boundary of capabilities only).
	 * @exception ImmutabilityException When impossible reference generation.
	 */
	public EntityReference root() throws ImmutabilityException;

}
