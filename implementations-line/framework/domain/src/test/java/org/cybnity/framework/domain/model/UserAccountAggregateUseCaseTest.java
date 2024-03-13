package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.application.sample.AssignRoleToUserAccountCommand;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.application.sample.UserAccountCreateCommand;
import org.cybnity.framework.domain.application.sample.UserAccountManagementDomainContext;
import org.cybnity.framework.domain.model.sample.ApplicativeRole;
import org.cybnity.framework.domain.model.sample.DomainEntityImpl;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.domain.model.sample.readmodel.DenormalizedEntityImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentity;
import org.cybnity.framework.immutable.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of Domain object (UserAccountAggregate) behaviors regarding its
 * supported requirements.
 *
 * @author olivier
 */
public class UserAccountAggregateUseCaseTest {

    private Entity accountOwner;
    private Identifier accountId;

    private UserAccountManagementDomainContext domainContext;

    @BeforeEach
    public void initContext() {
        // Create a context supporting the domain with repositories resources
        this.domainContext = new UserAccountManagementDomainContext();
    }

    @BeforeEach
    public void initUserAccountSample() {
        accountOwner = new DomainEntityImpl(
                new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
        accountId = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString());
    }

    @AfterEach
    public void cleanUserAccountSample() {
        this.accountOwner = null;
    }

    @AfterEach
    public void cleanContext() {
        this.domainContext = null;
    }

    /**
     * Test that any modification of a roles set is maintaining the roles history
     * graph in terms of roles entries and removed.
     *
     * @throws Exception
     */
    @Test
    public void givenIdentifierUserAccountWithRole_whenChangedRole_thenRoleFactGraphMaintained() throws Exception {
        // Create a user account
        UserAccountAggregate account = new UserAccountAggregate(accountId, accountOwner);
        // Create an event simulating an original command of user account creation
        DomainEntity eventId = new UserAccountIdentity(accountId);
        UserAccountCreateCommand event = new UserAccountCreateCommand(eventId);
        event.accountUID = (String) accountId.value();

        // Check none default role assigned
        Set<ApplicativeRole> currentRoles = account.assignedRoles();
        assertTrue(currentRoles.isEmpty(), "Shall not include any default role!");

        // Simulate assignment of new role with reuse of natural key based for
        // identifier generation
        String roleName = "ISO";
        AssignRoleToUserAccountCommand roleAssignmentCommand = new AssignRoleToUserAccountCommand(
                new DomainEntity(
                        new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString())));
        // Which role shall be assigned
        roleAssignmentCommand.assignedRole = new ApplicativeRoleDTO(roleName);
        assertEquals(HistoryState.COMMITTED, roleAssignmentCommand.assignedRole.status,
                "Default state shall be Committed!");
        // What user account should be upgraded
        roleAssignmentCommand.userAccountIdentifier = (String) account.identified().value();

        // Add the role to the aggregate account
        account.handle(roleAssignmentCommand, this.domainContext);

        assertTrue(account.assignedRoles().size() == 1, "ISO role shall have been saved!");
        // Verify that role have not predecessor version (it's the initial version)
        for (ApplicativeRole assignedRole : account.assignedRoles()) {
            // Check that's the good role name
            assertEquals(roleName, assignedRole.getName());
            // Check none history of predecessor version
            assertTrue(assignedRole.changesHistory().isEmpty());
        }

        // Create a new version of the same role which is cancelled to this user account
        roleAssignmentCommand = new AssignRoleToUserAccountCommand(new DenormalizedEntityImpl(
                new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString())));
        // Which role shall be cancelled
        roleAssignmentCommand.assignedRole = new ApplicativeRoleDTO(roleName);
        roleAssignmentCommand.assignedRole.status = HistoryState.CANCELLED;
        // What user account should be upgraded
        roleAssignmentCommand.userAccountIdentifier = (String) account.identified().value();

        // Update the role (history graph) to the aggregate account regarding a removed
        // role
        account.handle(roleAssignmentCommand, this.domainContext);
        assertTrue(account.assignedRoles().size() == 1,
                "Modified existent ISO role shall have been maintained!");

        // Verify that role have one predecessor version (it's the initial version
        // before it's cancelled version)
        for (ApplicativeRole assignedRole : account.assignedRoles()) {
            // Check that's the good role name that is maintained
            assertEquals(roleName, assignedRole.getName());
            // Check that current role is in the last status (cancelled)
            assertEquals(HistoryState.CANCELLED, assignedRole.historyStatus(),
                    "The current role should be in last status assigned (Cancelled)!");

            // Check that role have an history of several status (committed, cancelled)
            Set<MutableProperty> story = assignedRole.changesHistory();
            assertFalse(story.isEmpty(), "One previous version shall exist regarding the original status (committed)!");
            ApplicativeRole previousOriginalRoleVersion = (ApplicativeRole) story.iterator().next();
            assertEquals(HistoryState.COMMITTED, previousOriginalRoleVersion.historyStatus(),
                    "Invalid status of the previous role originally created in Committed state!");

        }
    }

}
