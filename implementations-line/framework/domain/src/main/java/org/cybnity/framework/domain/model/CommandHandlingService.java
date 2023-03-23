package org.cybnity.framework.domain.model;

import java.util.HashSet;
import java.util.Set;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.ProcessManager;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a component which manage handlers regarding a specific Aggregate
 * type.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class CommandHandlingService extends ProcessManager {

    /**
     * Subject that support the commands handled by the handlers manager.
     */
    private IAggregate recipientOfCommands;

    /**
     * Domain publishers which could be notified by changed aggregate via command
     * processed.
     */
    private Set<DomainEventPublisher> notifiablePublishers = new HashSet<>();

    /**
     * Default constructor initializing recipient of handlers regarding the
     * aggregate type.
     * 
     * @param type                   Mandatory type or perimeter manage by this
     *                               commands handling service, usable for method
     *                               call (e.g in direct or remote via a messaging
     *                               service).
     * @param notifiableAboutChanges Optional set of domain publishers which should
     *                               be notified about change events (e.g resulting
     *                               of commands executed by this service on
     *                               aggregates).
     * @param ctx                    Optional context which can be required by the
     *                               managedHandlers() method (child implementation)
     *                               executed during the construction of this
     *                               instance.
     * @throws IllegalArgumentException         WHen mandatory parameter is missing.
     * @throws InvalidTargetObjectTypeException When none managed handlers are
     *                                          identified (non sens of
     *                                          instantiation regarding this
     *                                          component without any delegation
     *                                          defined) or some eligible handler
     *                                          instances are not valid.
     */
    public CommandHandlingService(IAggregate type, Set<DomainEventPublisher> notifiableAboutChanges, IContext ctx)
	    throws IllegalArgumentException, InvalidTargetObjectTypeException {
	super(ctx);
	if (type == null)
	    throw new IllegalArgumentException("Supported aggregate type parameter is required!");
	recipientOfCommands = type;
	if (notifiableAboutChanges != null) {
	    getNotifiablePublishers().addAll(notifiableAboutChanges);
	}
    }

    /**
     * Get the aggregate instance that is supported by this handling service (via
     * its delegated command handlers set).
     * 
     * @return An aggregate instance.
     */
    protected IAggregate recipientOfCommands() {
	return this.recipientOfCommands;
    }

    /**
     * Get the publishers which should be notified by changed aggregate via command
     * processed.
     * 
     * @return A set or empty list.
     */
    protected Set<DomainEventPublisher> getNotifiablePublishers() {
	return notifiablePublishers;
    }

    /**
     * Define the publishers which should be notified by changed aggregate via
     * command processed.
     * 
     * @param notifiablePublishers A list of publishers to notify. If null or empty,
     *                             default set if cleaned (remove any potential
     *                             previous defined publishers).
     */
    protected void setNotifiablePublishers(Set<DomainEventPublisher> notifiablePublishers) {
	if (notifiablePublishers == null || notifiablePublishers.isEmpty()) {
	    // Clean default set
	    this.notifiablePublishers.clear();
	} else {
	    this.notifiablePublishers.clear();
	    this.notifiablePublishers.addAll(notifiablePublishers);
	}
    }

}
