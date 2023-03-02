package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.immutable.Entity;

/**
 * Example of command adding a new applicative role to a user account into a
 * system.
 * 
 * @author olivier
 *
 */
public class AssignRoleToUserAccountCommand extends Command {

    private static final long serialVersionUID = 1L;
    public ApplicativeRoleDTO assignedRole;
    public String userAccountIdentifier;

    public AssignRoleToUserAccountCommand() {
	super();
    }

    public AssignRoleToUserAccountCommand(Entity identifiedBy) {
	super(identifiedBy);
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }

}
