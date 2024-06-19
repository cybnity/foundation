package org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.CommandFactory;
import org.cybnity.framework.domain.model.DomainEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Utility class for unit test
 */
public class TenantMockHelper {

    /**
     * Prepare and return a valid command valid to be treated by the feature entrypoint.
     *
     * @param tenantName    Mandatory name of an organization subject of registration.
     * @param activityState Mandatory operational activity state to build.
     * @return A prepared command.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public Command prepareRegisterTenantCommand(String tenantName, Boolean activityState) throws IllegalArgumentException {
        if (tenantName == null || tenantName.isEmpty())
            throw new IllegalArgumentException("organizationName parameter is required!");
        if (activityState == null) throw new IllegalArgumentException("activityStatus parameter is required!");
        // Prepare json object (RegisterTenant command event including tenant naming) from translator
        Collection<Attribute> definition = new ArrayList<>();
        // Set organization name
        Attribute tenantNameToRegister = new Attribute("TENANT_NAMING", tenantName);
        definition.add(tenantNameToRegister);
        // Define default activity state
        Attribute activityStatus = new Attribute("ACTIVITY_STATE", activityState.toString());
        //changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Tenant.Attribute.ACTIVITY_STATUS.name(), this.activityStatus.isActive().toString()));
        definition.add(activityStatus);

        // Prepare RegisterOrganization command event to perform via API
        Command cmd = CommandFactory.create("REGISTER_TENANT",
                new DomainEntity(IdentifierStringBased.generate(null)) /* command identity */, definition,
                /* none prior command to reference*/ null,
                /* None pre-identified organization because new creation */ null);
        // Auto-assign correlation identifier allowing finalized transaction check
        cmd.generateCorrelationId(UUID.randomUUID().toString());

        return cmd;
    }
}
