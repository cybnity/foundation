package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.TenantDescriptor.PropertyAttributeKey;
import org.cybnity.framework.domain.model.sample.DomainEntityImpl;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test regarding the behavior of a Tenant class.
 *
 * @author olivier
 */
public class TenantUseCaseTest {

    private Tenant tenant;
    private String namedOrganization = "CYBNITY France";
    private TenantDescriptor organization;
    private Identifier id;

    @BeforeEach
    public void initTenantSample() throws Exception {
        id = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString());
        // Create named tenant
        tenant = new Tenant(new DomainEntityImpl(id),
                /*
                 * Simulate auto-assigned parent identifier without extension of the child id
                 * generation based on identifiers and minimum quantity of length
                 */ null, /* Simulate unknown original activity state */ null);

        // Define attributes of tenant owner
        HashMap<String, Object> organisationAttr = new HashMap<>();
        organisationAttr.put(PropertyAttributeKey.LABEL.name(), namedOrganization);
        organization = new TenantDescriptor(tenant.parent(), organisationAttr, HistoryState.COMMITTED);
        tenant.setLabel(organization);
    }

    @AfterEach
    public void cleanTenantSample() {
        this.tenant = null;
        this.organization = null;
        this.id = null;
    }

    /**
     * Test that when a named tenant (original organization representer) is upgraded
     * regarding its naming mutable property, the attribute versions history is
     * managed by the tenant.
     *
     * @throws Exception
     */
    @Test
    public void givenDefineOrganization_whenChangeOrganization_thenMutableVersionsHistorized() throws Exception {
        // Created tenant with a name (organization sample)

        // Define a new organization changed regarding the tenant (e.g simulate a
        // company brand
        // change)
        String renamedAs = "CYBNITY Corp";
        HashMap<String, Object> attr = new HashMap<String, Object>();
        attr.put(PropertyAttributeKey.LABEL.name(), renamedAs);

        TenantDescriptor renamed1 = new TenantDescriptor(tenant.parent(), attr, HistoryState.COMMITTED,
                /*
                 * prior link about old organizations automatically included into the versions
                 * history of this renaming
                 */ tenant.label());

        // Update the tenant with new organization (that have already previous
        // organization in history
        // attached)
        tenant.setLabel(renamed1);
        // Verify saved organization regarding the tenant
        assertEquals(renamed1, tenant.label());
        // Verify that previous organization is maintained into the tenant mutable
        // property
        // history
        assertEquals(1, tenant.label().changesHistory().size(),
                "Initial organization of the tenant shall had been maintained in the versions history!");
    }

    /**
     * Verify that a created tenant include all required values after instantiation.
     */
    @Test
    public void givenDefaultTenant_whenCreate_thenCompleted() throws Exception {
        // Check valid identifier
        assertNotNull(tenant.identified());
        assertNotNull(tenant.occurredAt());
        // Because default null optional identifier of sample id assign automatically
        // the parent id to the tenant, child and parent identifier are equals
        assertEquals(id, tenant.parent().identified(), "Invalid sample identifier!");
        assertEquals(id, tenant.identified(), "Invalid sample identifier!");

        // Named tenant check
        TenantDescriptor orgaDesc = tenant.label();
        assertNotNull(orgaDesc);
        assertEquals(namedOrganization, orgaDesc.getLabel(),
                "Shall be defined equals as defined by the initTenantSample() call!");

        // Default status is unknown
        assertNull(tenant.status(), "Shall not be automatically defined by constructor!");
    }

    /**
     * Create a default tenant, activate (simulate its registration as active) and
     * verify that status is synchronized.
     */
    @Test
    public void givenDefaultTenant_whenActivate_thenActiveStatus() throws Exception {
        // Activate the tenant sample
        tenant.activate();
        // Check that active status if defined
        ActivityState status = tenant.status();
        assertNotNull(status, "Shall had been initialized!");
        // Check default value of the state
        assertTrue(status.isActive());
    }

    /**
     * Create a default tenant, deactivate (simulate its registration as not active)
     * and verify that status is synchronized.
     */
    @Test
    public void givenDefaultTenant_whenDeactivate_thenUnactiveStatus() throws Exception {
        // Deactivate the tenant sample
        tenant.deactivate();
        // Check that active status if defined
        ActivityState status = tenant.status();
        assertNotNull(status, "Shall had been initialized!");
        // Check default value of the state
        assertFalse(status.isActive());
    }

    /**
     * Verify that constructor supports only specific/standard identifier name and
     * reject other type (required to be equals with the used identifier name of
     * identified() ).
     */
    @Test
    public void givenInvalidIdentifierName_whenConstructor_thenIllegalArgumentException() {
        // Check that invalid identifier name is rejected
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                // Try instantiation based on not supported identifier name
                new Tenant(
                        new DomainEntityImpl(new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
                                UUID.randomUUID().toString())),
                        new IdentifierStringBased("other", UUID.randomUUID().toString()), Boolean.TRUE);
            }
        });
        // Check null id is not supported
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                // Try instantiation based on none predecessor
                new Tenant(null,
                        new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()),
                        Boolean.TRUE);
            }
        });
    }

    /**
     * Test that default tenant without defined original state, is activable with
     * valid instantiated contents (e.g maintained history of previous state
     * versions).
     *
     * @throws Exception
     */
    @Test
    public void givenDefaultInstance_whenActivate_thenStateUpdatedWithHistoryMaintained() throws Exception {
        // Create default tenant
        Tenant tenant = new Tenant(
                new DomainEntityImpl(
                        new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString())),
                new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()),
                /* null as default unknown state */ null);
        assertNull(tenant.status(), "Unknown by default!");
        // Activate it (e.g simulate registration/subscription state changed)
        assertNotNull(tenant.activate(), "Mutable property state shall have been created!");

        // Check activity state initialized
        ActivityState v1State = tenant.status();
        assertNotNull(v1State);
        assertTrue(tenant.status().isActive());

        // Check the ownership of the activity state property
        assertEquals(tenant.parent().reference(), tenant.status().ownerReference(),
                "Owner of state property shall had been defined as the tenant!");

        // Verify that none history is by default existent
        assertTrue(tenant.status().changesHistory().isEmpty());

        // Update the activity state of the tenant
        ActivityState updated = (ActivityState) tenant.deactivate(); // change current status of the tenant, including
        // the automatic add of previous state into the
        // history of the new created state instance
        // Check maintained history
        assertEquals(1, updated.changesHistory().size(),
                "previous original status shall have been saved in history (as prior of new instantiated status)!");
        ActivityState savedV1 = (ActivityState) tenant.status().changesHistory().iterator().next();
        assertEquals(v1State, savedV1,
                "Original state shall had been saved into the history chain of the new actived/created status!");
        assertTrue(v1State.changesHistory().isEmpty() && savedV1.changesHistory().isEmpty()); // Check none any previous
        // history version in the
        // original state

        // Check good updated unactive state into the changed tenant
        assertFalse(tenant.status().isActive());
        assertEquals(HistoryState.COMMITTED, tenant.status().historyStatus(), "Invalid default committed history!");
    }

    /**
     * Verify that creation of immutable version include all required elements.
     *
     * @throws Exception
     */
    @Test
    public void givenInstance_whenImmutable_completedCopy() throws Exception {
        tenant.activate(); // Activate status replacing originally unknown
        Tenant copy = (Tenant) tenant.immutable();
        assertNotNull(copy.identified());
        assertNotNull(copy.occurredAt());
        assertNotNull(copy.label());
        assertNotNull(copy.parent());
        assertNotNull(copy.status());
    }
}
