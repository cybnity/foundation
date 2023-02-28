package org.cybnity.framework.domain.application.sample;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.CommandHandler;
import org.cybnity.framework.domain.EventIdentifierStringBased;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.domain.model.sample.UserAccountCreationCommitted;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Sample handling supporting the treatment of a command for creation of a new
 * user account into a system.
 * 
 * @author olivier
 *
 */
public class UserAccountCreateCommandHandler implements CommandHandler {

    private DomainEventSubscriber<UserAccountCreationCommitted> listenedWriteModel;
    private Class<?> supportedCommand = UserAccountCreateCommand.class;
    private DomainEventPublisher readModelPublisher;

    /**
     * Default constructor.
     * 
     * @param readModelPublisher Mandatory domain boundary publisher service.
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    public UserAccountCreateCommandHandler(DomainEventPublisher readModelPublisher) throws IllegalArgumentException {
	if (readModelPublisher == null)
	    throw new IllegalArgumentException("writeModelPublisher parameter is required!");
	this.readModelPublisher = readModelPublisher;
    }

    @Override
    public void handle(Command command) throws IllegalArgumentException, InvalidParameterException {
	if (command == null)
	    throw new IllegalArgumentException("The command parameter is required!");
	if (command instanceof UserAccountCreateCommand) {
	    UserAccountCreateCommand toProcess = (UserAccountCreateCommand) command;
	    try {
		// Create listener of write model's changes
		listenedWriteModel = new DomainEventSubscriber<UserAccountCreationCommitted>() {
		    @Override
		    public void handleEvent(UserAccountCreationCommitted event) {
			// Refresh the read model regarding the new state of created user account
			System.out.println("Update the read model about new created user account");
		    }

		    @Override
		    public Class<?> subscribeToEventType() {
			return supportedCommand;
		    }
		};

		// Register the model changes listeners regarding the write model changes
		this.readModelPublisher.subscribe(listenedWriteModel);

		// Execute the command regarding creation of a new aggregate (user account)
		UserAccountAggregate account = new UserAccountAggregate(
			new EventIdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), toProcess.accountUID),
			toProcess.userIdentity);

		// Normally save into a persistence system (e.g Datastore of user accounts)
		// -------------> SAVE CONFIRMED AS CONSISTENT UNIT OF USERACCOUNTAGGREGATE
		// VERSION

		// Build event child based on the created account (parent of immutable story)
		CommonChildFactImpl persistedAccount = new CommonChildFactImpl(account,
			new EventIdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
				/* identifier as performed transaction number */ UUID.randomUUID().toString()));

		// Prepare notification of the read model about new committed account
		UserAccountCreationCommitted committedAccount = new UserAccountCreationCommitted(
			persistedAccount.parent());
		committedAccount.creationCommandRef = toProcess.reference();
		committedAccount.createdAccountRef = account.reference();

		// Publish event occured on the write model regarding the committed user account
		this.readModelPublisher.publish(committedAccount);
	    } catch (IllegalArgumentException iae) {
		// Invalid command rejected by the aggregate
		throw new InvalidParameterException(iae.getMessage());
	    } catch (ImmutabilityException cnse) {
		// Exception regarding persisted account entity
		// Log the implementation problem and notify the error occured during the
		// command processing for information of command sender
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
	    UserAccountCreateCommand obj = (UserAccountCreateCommand) Class.forName(supportedCommand.getName())
		    .getDeclaredConstructor().newInstance((Object[]) null);
	    set.add(obj.versionUID());
	} catch (Exception e) {
	    // Log not found exception regarding the command type normally supported
	    e.printStackTrace();
	}
	return set;
    }

}
