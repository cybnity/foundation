package org.cybnity.framework.domain;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a component which manage handlers regarding a specific aggregate
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
    private Aggregate recipientOfCommands;

    /**
     * Default constructor initializing recipient of handlers regarding the
     * aggregate type.
     * 
     * @param type Mandatory type or perimeter manage by this commands handling
     *             service, usable for method call (e.g in direct or remote via a
     *             messaging service).
     * @throws IllegalArgumentException         WHen mandatory parameter is missing.
     * @throws InvalidTargetObjectTypeException When none managed handlers are
     *                                          identified (non sens of
     *                                          instantiation regarding this
     *                                          component without any delegation
     *                                          defined) or some eligible handler
     *                                          instances are not valid.
     */
    public CommandHandlingService(Aggregate type) throws IllegalArgumentException, InvalidTargetObjectTypeException {
	super();
	if (type == null)
	    throw new IllegalArgumentException("Supported aggregate type parameter is required!");
	recipientOfCommands = type;
    }

    /**
     * Get the aggregate instance that is supported by this handling service (via
     * its delegated command handlers set).
     * 
     * @return An aggregate instance.
     */
    protected Aggregate recipientOfCommands() {
	return this.recipientOfCommands;
    }

}
