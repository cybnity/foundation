package org.cybnity.framework.domain.application.sample;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Set;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.CommandHandler;
import org.cybnity.framework.domain.ProcessManager;
import org.cybnity.framework.domain.model.DomainEventPublisher;

/**
 * Example of handlers service manager regarding a domain boundary.
 * 
 * @author olivier
 *
 */
public class UserAccountManagementProcessesImpl extends ProcessManager {

    public UserAccountManagementProcessesImpl() throws InvalidTargetObjectTypeException {
	super();
    }

    @Override
    protected HashMap<String, CommandHandler> managedHandlers() {
	// Define basic example of commands handlers which execute commands on the
	// aggregates and notify a comain publisher
	HashMap<String, CommandHandler> exposedAPI = new HashMap<>();
	DomainEventPublisher writeModelPublisher = DomainEventPublisher.instance();

	exposedAPI.put(UserAccountCreateCommand.class.getName(),
		new UserAccountCreateCommandHandler(writeModelPublisher));
	exposedAPI.put(AssignRoleToUserAccountCommand.class.getName(),
		new ApplicativeRoleAllocationCommandHandler(writeModelPublisher));
	return exposedAPI;
    }

    @Override
    public void handle(Command command) throws IllegalArgumentException, InvalidParameterException {
	if (command == null)
	    throw new IllegalArgumentException("Command parameter to process is required");
	// Find the handler delegated to suppor the type of command
	CommandHandler processhandler = this.delegation().get(command.getClass().getName());
	if (processhandler != null) {
	    // Execute command on delegated handler
	    processhandler.handle(command);
	    return;
	}
	throw new InvalidParameterException(
		"The requested command is not supported by any process of this process manager!");

    }

    @Override
    public Set<Long> handledCommandTypeVersions() {
	// Any versions of command can be processed by this process manager
	return null;
    }

}
