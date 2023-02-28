package org.cybnity.framework.domain;

import java.security.InvalidParameterException;
import java.util.Set;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represents a responsible of actions realization requested by a stakeholder
 * (e.g ui component) via a command event. Typically, the handler receives a
 * command instand from a messaging infrastructure, validates the command
 * validity, locates the aggregate instance that is the target of the command
 * (this may involve creating a new aggregate instance or locating an existing
 * instance), and invokes the appropriate method on the aggregate instance,
 * passing in any parameters from the command; and it persists the new state of
 * the aggregate to a persistence system.
 * 
 * Typically, the command handlers are organized into a class (see
 * {@link org.cybnity.framework.domain.CommandHandlingService}) that contains
 * all of the handlers for a specific aggregate type.
 * 
 * Commands should be processed once, by a single recipient. The messaging
 * infrastructure should ensure that it delivers just a single copy of a command
 * to single command handler.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface CommandHandler {

    /**
     * Manage the realization of a command.
     * 
     * @param command Mandatory command that must be treated.
     * @throws IllegalArgumentException  When command parameter is null.
     * @throws InvalidParameterException When a command processing can't be
     *                                   performed for cause of command invalidity
     *                                   (e.g missing required contents).
     */
    public void handle(Command command) throws IllegalArgumentException, InvalidParameterException;

    /**
     * Get the version number regarding the types of Command which are supported by
     * this handler.
     * 
     * @return A set of versions that this handler is capable to handle (e.g
     *         specific versions of a same type of command). Null or empty set when
     *         any type of Command can be treated by this handler.
     */
    public Set<Long> handledCommandTypeVersions();
}
