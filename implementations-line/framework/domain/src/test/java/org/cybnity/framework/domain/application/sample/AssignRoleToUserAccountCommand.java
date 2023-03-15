package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

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

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

}
