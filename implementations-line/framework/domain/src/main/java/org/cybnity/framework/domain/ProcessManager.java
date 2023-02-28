package org.cybnity.framework.domain;

import java.util.HashMap;
import java.util.Map;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Instead of each aggregates sending messages directly to other aggregates, the
 * messages are mediated by the process manager. Behavior design pattern, a
 * process manager is a mediation component that distribute the messages (e.g
 * CommandEvent to several aggregates instances). Used when a bounded context
 * uses a large number of events and commands that would be difficul to manage
 * as a collection point-to-point interactions between aggregates; or when
 * message routing in a bounded context need to be easily modifiable.
 * 
 * Process manager does not perform any business logic but it only routes
 * messages, and in some cases translates between message types.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class ProcessManager implements CommandHandler {

    /**
     * Managed handlers which are specific to the aggregate type, and that can be
     * selected for processing of specific commands. The Key is equals to a Command
     * name (that should be unique per command) which is a supported command by the
     * aggregate, and the value if the mandatory handler instance which is
     * responsible of the treatment for this type of command.
     */
    private HashMap<String, CommandHandler> mediated;

    /**
     * Default constructor of this mediation pattern component.
     * 
     * @throws InvalidTargetObjectTypeException When none managed handlers are
     *                                          identified (non sens of
     *                                          instantiation regarding this
     *                                          component without any delegation
     *                                          defined) or some eligible handler
     *                                          instances are not valid.
     */
    public ProcessManager() throws InvalidTargetObjectTypeException {
	mediated = new HashMap<>();
	initializeHandlers();
    }

    /**
     * Initialize and verify the conformity of the commands handlers supported by
     * this services regarding the aggregate type. This method is calling the
     * managedHandlers() method which provide potential usable handlers of future
     * commands to treat. Each handler identified is checked to ensure that a
     * command is identified for an handler in a unique way (e.g none several
     * handlers are target of a same type of command).
     * 
     * @throws InvalidTargetObjectTypeException When none managed handlers are
     *                                          identified (non sens of
     *                                          instantiation regarding this
     *                                          component without any delegation
     *                                          defined) or some eligible handler
     *                                          instances are not valid.
     */
    protected void initializeHandlers() throws InvalidTargetObjectTypeException {
	// Get managed handlers
	HashMap<String, CommandHandler> handlers = managedHandlers();
	if (handlers == null || handlers.isEmpty())
	    throw new InvalidTargetObjectTypeException(
		    "Minimum of one managed handlers shall exist as delegated to the treatment of one command type!");
	// Use temporary checker of eligible future handlers
	HashMap<String, CommandHandler> eligibleDelegation = new HashMap<>(handlers.size());

	// Verify handler and command validity
	// and initialize managed handler instances
	for (Map.Entry<String, CommandHandler> set : handlers.entrySet()) {
	    String commandName = set.getKey();
	    if (commandName == null || commandName.equals(""))
		throw new InvalidTargetObjectTypeException(
			"Each managed handler shall reference a command name supported!");
	    CommandHandler delegated = set.getValue();
	    if (delegated == null)
		throw new InvalidTargetObjectTypeException(
			"Each handled command shall be supported by a delegated handler type!");
	    // Verify that a same command is not already supported by another handler
	    if (eligibleDelegation.containsKey(commandName))
		throw new InvalidTargetObjectTypeException(
			"Multiples handlers are identified to support a same command type. Not manageable by this service!");
	    // Make delegation as eligible
	    eligibleDelegation.put(commandName, delegated);
	}
	if (!eligibleDelegation.isEmpty()) {
	    this.mediated = eligibleDelegation;
	    return;
	}
	throw new InvalidTargetObjectTypeException("Undefined managed handlers!");
    }

    /**
     * Get the handlers instances that are currently managed by this service.
     * 
     * @return A list of handling instances.
     */
    protected HashMap<String, CommandHandler> delegation() {
	return this.mediated;
    }

    /**
     * Initialize a set of command handlers that are managed and that support the
     * processed to treat.
     * 
     * This method is used during the construction of this instance, that check
     * conformity of each handler instance delivered by this method (e.g key and
     * value are not null, no duplicated handler for same type of command).
     * 
     * @return A set of handlers. Each item shall be non null. Key is equals to name
     *         of Command supported (e.g <<CommandType>>.getClass().getName() or
     *         <<CommandType>>.getClass().getSimpleName()), by the Handler value.
     */
    protected abstract HashMap<String, CommandHandler> managedHandlers();
}
