package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.*;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Hydration use case test regarding a domain object, and changes event behaviors.
 */
public class TenantHydrationUseCaseTest {

    /**
     * Validate that change events history of a domain object is automatically produced during instance creation.
     */
    @Test
    public void givenLabel_whenTenantInstanceBuilt_thenChangeEventsHistorized() throws Exception {
        // Simulate a request of tenant creation command
        Entity originCreationCommand = new DomainEntity(IdentifierStringBased.generate(null));
        Command cmd = new ConcreteCommandEvent(originCreationCommand);
        String tenantLabel = "CYBNITY";
        cmd.appendSpecification(new org.cybnity.framework.domain.Attribute(CommandName.CREATE_TENANT.name(), tenantLabel));

        // Create a tenant instance (generating default activity status and label descriptor)
        TenantBuilder builder = new TenantBuilder(tenantLabel, originCreationCommand, Boolean.TRUE);
        builder.buildInstance();
        Tenant tenantInstance = builder.getResult();

        // Check that change events have been generated in history
        List<DomainEvent> changes = tenantInstance.changeEvents();
        Assertions.assertNotNull(changes);
        boolean foundValidCreationEvt = false, activityStatusChangeEvt = false, tenantLabelChangeEvt = false;
        for (DomainEvent evt : changes) {
            if (DomainEventType.TENANT_CREATED.name().equals(evt.type().value())) {
                // Found event regarding creation
                // Verify its minimum contents validation
                checkCreationEventTypeBasicContentsConformity(evt, (String) originCreationCommand.identified().value(), tenantInstance);
                foundValidCreationEvt = true;// Confirm found valid creation event
            }
            if (DomainEventType.TENANT_CHANGED.name().equals(evt.type().value())) {
                // Found event regarding change of a Tenant attribute

                // Verify if it's a change of activity status (e.g realized during creation step and notified as an additional change event)
                Attribute activityState = EventSpecification.findSpecificationByName(Tenant.Attribute.ACTIVITY_STATUS.name(), evt.specification());
                if (activityState != null) {
                    // Activity status verification
                    Assertions.assertEquals(tenantInstance.status().isActive(), Boolean.valueOf(activityState.value()));
                    activityStatusChangeEvt = true;
                }

                // Verify if it's a change of tenant label (e.g realized during creation step as origin name assigned to tenant)
                Attribute tenantDescriptorLabel = EventSpecification.findSpecificationByName(Tenant.Attribute.LABEL.name(), evt.specification());
                if (tenantDescriptorLabel != null) {
                    // Label verification
                    Assertions.assertEquals(tenantInstance.label().getLabel(), tenantDescriptorLabel.value());
                    tenantLabelChangeEvt = true;
                }
            }
        }
        Assertions.assertTrue(foundValidCreationEvt && activityStatusChangeEvt && tenantLabelChangeEvt, "Creation events shall have been produced by super class!");
    }

    /**
     * Verify contents and description of a creation event regarding a tenant.
     */
    private void checkCreationEventTypeBasicContentsConformity(DomainEvent changeEvt, String predecessorUUID, Tenant originTenant) throws ImmutabilityException {
        Assertions.assertNotNull(changeEvt);
        Assertions.assertNotNull(changeEvt.type());
        Assertions.assertEquals(DomainEventType.TENANT_CREATED.name(), changeEvt.type().value());
        Assertions.assertNotNull(changeEvt.identified());

        // Check additional contents relative to child fact
        Assertions.assertTrue(ConcreteDomainChangeEvent.class.isAssignableFrom(changeEvt.getClass()), "Invalid default impl class used by standard aggregate for change events production!");
        ConcreteDomainChangeEvent creationEvt = (ConcreteDomainChangeEvent) changeEvt;
        // Origin reference verification
        Assertions.assertEquals(originTenant.root(), creationEvt.changedModelElementReference(), "Subject of change shall be origin referenced!");

        // Check existing predecessor attribute
        Attribute predecessorAttr = EventSpecification.findSpecificationByName(Aggregate.Attribute.PREDECESSOR_REFERENCE_ID.name(), changeEvt.specification());
        Assertions.assertNotNull(predecessorAttr);// Existing reference to predecessor
        Assertions.assertEquals(predecessorUUID, predecessorAttr.value(), "Predecessor reference shall exist regarding aggregate parent!");

    }
}
