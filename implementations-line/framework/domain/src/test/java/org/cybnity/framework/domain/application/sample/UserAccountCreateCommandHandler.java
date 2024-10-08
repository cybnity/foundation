package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.ICommandHandler;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.ImmutabilityException;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample handling supporting the treatment of a command for creation of a new
 * user account into a system.
 *
 * @author olivier
 */
public class UserAccountCreateCommandHandler implements ICommandHandler {

    private final Class<?> supportedCommand = UserAccountCreateCommand.class;
    private final UserAccountManagementDomainContext context;
    private final Logger logger = Logger.getLogger(UserAccountCreateCommandHandler.class.getName());

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public UserAccountCreateCommandHandler(UserAccountManagementDomainContext ctx) throws IllegalArgumentException {
        if (ctx == null)
            throw new IllegalArgumentException("Context parameter is required!");
        context = ctx;
    }

    @Override
    public void handle(Command command, IContext ctx) throws IllegalArgumentException {
        if (command == null)
            throw new IllegalArgumentException("The command parameter is required!");
        if (command instanceof UserAccountCreateCommand) {
            UserAccountCreateCommand toProcess = (UserAccountCreateCommand) command;
            try {
                // Execute the command regarding creation of a new aggregate (user account)
                UserAccountAggregate account = new UserAccountAggregate(
                        new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), toProcess.accountUID),
                        toProcess.userIdentity.getEntity());

                // Normally save into a persistence system (e.g Datastore of user accounts)
                context.getWriteModelStore().append(account, toProcess);
                // -------------> SAVE CONFIRMED AS CONSISTENT UNIT OF USERACCOUNTAGGREGATE
                // VERSION BY UserAccountChanged event automatically send by the store
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
    public Set<String> handledCommandTypeVersions() {
        Set<String> set = new HashSet<String>();
        try {
            UserAccountCreateCommand obj = (UserAccountCreateCommand) Class.forName(supportedCommand.getName())
                    .getDeclaredConstructor().newInstance((Object[]) null);
            set.add(obj.versionHash());
        } catch (Exception e) {
            // Log not found exception regarding the command type normally supported
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return set;
    }

}
