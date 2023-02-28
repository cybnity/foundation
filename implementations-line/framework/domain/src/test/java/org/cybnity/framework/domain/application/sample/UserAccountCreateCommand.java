package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;

/**
 * Example of command creating a new user account into a system.
 * 
 * @author olivier
 *
 */
public class UserAccountCreateCommand extends Command {

    private static final long serialVersionUID = 1L;
    public String accountUID;
    public EntityReference userIdentity;

    public UserAccountCreateCommand() {
	super();
    }

    public UserAccountCreateCommand(Entity identifiedBy) {
	super(identifiedBy);
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }

}
