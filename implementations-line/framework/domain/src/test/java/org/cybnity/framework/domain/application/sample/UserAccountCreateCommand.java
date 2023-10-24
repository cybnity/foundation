package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;

/**
 * Example of command creating a new user account into a system.
 *
 * @author olivier
 */
public class UserAccountCreateCommand extends Command {

    private static final long serialVersionUID = 1L;
    public String accountUID;
    public EntityReference userIdentity;

    public UserAccountCreateCommand() {
        super();
    }

    public UserAccountCreateCommand(DomainEntity identifiedBy) {
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

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return null;
    }
}
