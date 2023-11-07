package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;

/**
 * Example of command adding a new applicative role to a user account into a
 * system.
 *
 * @author olivier
 */
public class AssignRoleToUserAccountCommand extends Command {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(AssignRoleToUserAccountCommand.class).hashCode();
    public ApplicativeRoleDTO assignedRole;
    public String userAccountIdentifier;

    public AssignRoleToUserAccountCommand() {
        super();
    }

    public AssignRoleToUserAccountCommand(DomainEntity identifiedBy) {
        super(identifiedBy);
    }

    /**
     * This implementation do nothing.
     *
     * @param eventIdentifier Mandatory defined identifier. None assignment when not defined or empty parameter.
     */
    @Override
    protected void assignCorrelationId(String eventIdentifier) {

    }

    @Override
    public Attribute correlationId() {
        return null;
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
