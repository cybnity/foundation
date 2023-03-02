package org.cybnity.framework.domain.application.sample;

import java.util.HashSet;
import java.util.Set;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.ICommandHandler;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;

/**
 * Sample handling supporting the treatment of a command for add of an
 * applicative role to a user account into a system.
 * 
 * @author olivier
 *
 */
public class ApplicativeRoleAllocationCommandHandler implements ICommandHandler {

    private Class<?> supportedCommand = AssignRoleToUserAccountCommand.class;
    private UserAccountManagementDomainContext context;

    /**
     * Default constructor.
     * 
     * @param ctx Mandatory context.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public ApplicativeRoleAllocationCommandHandler(UserAccountManagementDomainContext ctx)
	    throws IllegalArgumentException {
	if (ctx == null)
	    throw new IllegalArgumentException("Context parameter is required!");
	context = ctx;
    }

    @Override
    public void handle(Command command, IContext ctx) throws IllegalArgumentException {
	if (command == null)
	    throw new IllegalArgumentException("The command parameter is required!");
	if (command instanceof AssignRoleToUserAccountCommand) {
	    AssignRoleToUserAccountCommand toProcess = (AssignRoleToUserAccountCommand) command;
	    ApplicativeRoleDTO roleToAssign = toProcess.assignedRole;
	    String userAccountId = toProcess.userAccountIdentifier;

	    if (roleToAssign != null && userAccountId != null) {
		// Normally save into a persistence system (e.g Datastore of user accounts)
		UserAccountStore accountStore = context.getWriteModelStore();

		// Search the user account and role to modify regarding current version (from
		// datastore of accounts/roles)
		UserAccountAggregate account = accountStore.findFrom(userAccountId);
		if (account != null) {
		    // Delegate command processing to the found aggregate object
		    // (which manage the domain boundary regarding the roles assignment state
		    account.execute(toProcess, ctx);
		} else {
		    // Notify that command is ignored because regarding an unknown user account
		    // which is not existing
		    throw new IllegalArgumentException("Unknown user account targeted by the identifier (accountId="
			    + userAccountId + ") received into the command!");
		}
	    } else {
		throw new IllegalArgumentException(
			"Invalid received command that shall contain role to assign and user account identifier!");
	    }
	} else {
	    // Log problem of bad linking between this handler and the type of command
	    // supported
	    throw new IllegalArgumentException("Unsupported type of command (only " + supportedCommand.getName()
		    + " command is handled by this handler)!");
	}
    }

    @Override
    public Set<Long> handledCommandTypeVersions() {
	Set<Long> set = new HashSet<Long>();
	try {
	    AssignRoleToUserAccountCommand obj = (AssignRoleToUserAccountCommand) Class
		    .forName(supportedCommand.getName()).getDeclaredConstructor().newInstance((Object[]) null);
	    set.add(obj.versionUID());
	} catch (Exception e) {
	    // Log not found exception regarding the command type normally supported
	    e.printStackTrace();
	}
	return set;
    }

}
