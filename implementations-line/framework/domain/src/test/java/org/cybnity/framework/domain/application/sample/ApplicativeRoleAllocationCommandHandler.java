package org.cybnity.framework.domain.application.sample;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.CommandHandler;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.domain.model.sample.UserAccountApplicativeRoleAssigned;

/**
 * Sample handling supporting the treatment of a command for add of an
 * applicative role to a user account into a system.
 * 
 * @author olivier
 *
 */
public class ApplicativeRoleAllocationCommandHandler implements CommandHandler {

    private DomainEventSubscriber<UserAccountApplicativeRoleAssigned> listenedWriteModel;
    private Class<?> supportedCommand = AssignRoleToUserAccountCommand.class;
    private DomainEventPublisher readModelPublisher;

    /**
     * Default constructor.
     * 
     * @param readModelPublisher Mandatory domain boundary publisher service.
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    public ApplicativeRoleAllocationCommandHandler(DomainEventPublisher readModelPublisher)
	    throws IllegalArgumentException {
	if (readModelPublisher == null)
	    throw new IllegalArgumentException("writeModelPublisher parameter is required!");
	this.readModelPublisher = readModelPublisher;
    }

    @Override
    public void handle(Command command) throws IllegalArgumentException, InvalidParameterException {
	if (command == null)
	    throw new IllegalArgumentException("The command parameter is required!");
	if (command instanceof AssignRoleToUserAccountCommand) {
	    AssignRoleToUserAccountCommand toProcess = (AssignRoleToUserAccountCommand) command;
	    try {
		// Create listener of write model's changes
		listenedWriteModel = new DomainEventSubscriber<UserAccountApplicativeRoleAssigned>() {
		    @Override
		    public void handleEvent(UserAccountApplicativeRoleAssigned event) {
			// Refresh the read model regarding the new state of created user account
			System.out.println("Update the read model about user account's role changed");
		    }

		    @Override
		    public Class<?> subscribeToEventType() {
			return supportedCommand;
		    }
		};

		// Register the model changes listeners regarding the write model changes
		this.readModelPublisher.subscribe(listenedWriteModel);

		// Search the user account and role to modify regarding current version (from
		// datastore of accounts/roles)
		UserAccountAggregate account = null;

		// Delegate command processing to the found aggregate object
		// (which manage the domain boundary regarding the roles assignment state
		account.execute(command);
	    } catch (IllegalArgumentException iae) {
		// Invalid command rejected by the aggregate
		throw new InvalidParameterException(iae.getMessage());
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
