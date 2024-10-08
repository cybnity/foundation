package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.*;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

/**
 * Hydration use case test regarding a domain object, and changes event behaviors.
 */
public class TenantHydrationUseCaseTest {

    /**
     * Validate that an origin tenant can be rehydrated from its lifecycle change history.
     */
    @Test
    public void givenChangeEventsHistory_whenTenantHydration_thenInstanceRehydrated() throws Exception {
        // Create new tenant
        Entity originCreationCommand = new DomainEntity(IdentifierStringBased.generate(null));
        String tenantLabel = "CYBNITY";
        TenantBuilder builder = new TenantBuilder(tenantLabel, originCreationCommand, Boolean.TRUE);
        builder.buildInstance();
        Tenant originTenantLatestVersion = builder.getResult();

        // Execute some changes feeding the tenant lifecycle history
        String tenantLabel2 = "CYBNITY2";
        HashMap<String, Object> propertyCurrentValue = new HashMap<>();
        propertyCurrentValue.put(TenantDescriptor.PropertyAttributeKey.LABEL.name(), tenantLabel2);
        TenantDescriptor ownerProperty = new TenantDescriptor(/* owner of description */originTenantLatestVersion.parent(), propertyCurrentValue, /* status */HistoryState.COMMITTED);
        originTenantLatestVersion.setLabel(ownerProperty); // Change tenant label
        originTenantLatestVersion.deactivate(); // Change activity status

        // Get the change events history of latest tenant version for simulate a rehydration
        List<DomainEvent> changesHistory = originTenantLatestVersion.changeEvents();
        Identifier originId = originTenantLatestVersion.identified();

        // Attempt to re-build hydrated instance from history
        Tenant rehydrated = Tenant.instanceOf(originId, changesHistory);

        // Check conformity of re-established last status equals to origin latest version
        Assertions.assertNotNull(rehydrated);
        Assertions.assertEquals(originTenantLatestVersion, rehydrated, "Shall be compared like equals!");
        Assertions.assertEquals(tenantLabel2, rehydrated.label().getLabel()); // Verify last version of label included in re-hydrated instance (mutation applied)
        Assertions.assertEquals(originTenantLatestVersion.status().isActive(), rehydrated.status().isActive()); // Verify mutation applied
        Assertions.assertTrue(rehydrated.changeEvents().isEmpty(), "Rehydrated instance shall not include changes events!"); // Verify that instantiation events have not been maintained when the rehydration finished
    }

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
        boolean foundValidCreationEvt = false, activityStatusChangeEvt = false;
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
            }
        }
        Assertions.assertTrue(foundValidCreationEvt && activityStatusChangeEvt, "Creation events shall have been produced by super class!");
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
        Identifier parentRefId = ((ConcreteDomainChangeEvent) changeEvt).changeSourcePredecessorReferenceId(); // Existing reference to predecessor stored in JSON string value
        Assertions.assertNotNull(parentRefId);// Existing reference to predecessor stored in JSON string value
        Assertions.assertEquals(predecessorUUID, parentRefId.value().toString(), "Predecessor reference identifier shall exist in equals value regarding aggregate parent!");
    }

}
