package org.cybnity.framework.domain.application.sample;

import java.util.HashMap;
import java.util.Set;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.ICommandHandler;
import org.cybnity.framework.domain.ProcessManager;

/**
 * Example of handlers service manager regarding a domain boundary.
 * 
 * @author olivier
 *
 */
public class UserAccountManagementProcessesImpl extends ProcessManager {

    public UserAccountManagementProcessesImpl(UserAccountManagementDomainContext ctx)
	    throws InvalidTargetObjectTypeException, IllegalArgumentException {
	super(ctx);
	if (ctx == null)
	    throw new IllegalArgumentException("Context parameter is required!");
    }

    @Override
    protected HashMap<String, ICommandHandler> managedHandlers() {
	// Define basic example of commands handlers which execute commands on the
	// aggregates and notify a comain publisher
	HashMap<String, ICommandHandler> exposedAPI = new HashMap<>();

	// Create handlers attached to the Domain context of this process manager
	exposedAPI.put(UserAccountCreateCommand.class.getName(),
		new UserAccountCreateCommandHandler((UserAccountManagementDomainContext) super.context));
	exposedAPI.put(AssignRoleToUserAccountCommand.class.getName(),
		new ApplicativeRoleAllocationCommandHandler((UserAccountManagementDomainContext) super.context));
	return exposedAPI;
    }

    @Override
    public void handle(Command command, IContext ctx) throws IllegalArgumentException {
	if (command == null)
	    throw new IllegalArgumentException("Command parameter to process is required");
	// Find the handler delegated to suppor the type of command
	ICommandHandler processhandler = this.delegation().get(command.getClass().getName());
	if (processhandler != null) {
	    // Execute command on delegated handler
	    processhandler.handle(command, ctx);
	    return;
	}
	throw new IllegalArgumentException(
		"The requested command is not supported by any process of this process manager!");

    }

    @Override
    public Set<Long> handledCommandTypeVersions() {
	// Any versions of command can be processed by this process manager
	return null;
    }

}
