package org.cybnity.framework.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.application.sample.AssignRoleToUserAccountCommand;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.application.sample.UserAccountCreateCommand;
import org.cybnity.framework.domain.application.sample.UserAccountManagementDomainContext;
import org.cybnity.framework.domain.model.sample.ApplicativeRole;
import org.cybnity.framework.domain.model.sample.DomainEntityImpl;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.domain.model.sample.readmodel.DenormalizedEntityImpl;
import org.cybnity.framework.domain.model.sample.readmodel.UserAccountRepository;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStoreImpl;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.MutableProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of Domain object (UserAccountAggregate) behaviors regarding its
 * supported requirements.
 * 
 * @author olivier
 *
 */
public class UserAccountAggregateUseCaseTest {

	private Entity accountOwner;
	private Identifier accountId;
	/**
	 * Datastore relative to all the account samples managed by a test.
	 */
	private UserAccountStoreImpl writeModelStore;
	/**
	 * Read Model repository storing user accounts DTO versions (last version of
	 * each user account)
	 */
	private UserAccountRepository readModelRepository;
	private UserAccountManagementDomainContext domainContext;

	@BeforeEach
	public void initContext() {
		// Create a write model store (e.g storage system of domain event logs)
		this.writeModelStore = (UserAccountStoreImpl) UserAccountStoreImpl.instance();
		// Create a read model repository (e.g interested to be notified about changes
		// observed in write model)
		// Constructor automatically manage the subscription of read model to the write
		// model datastore to implement models synchronizing via change events
		this.readModelRepository = UserAccountRepository.instance(this.writeModelStore);

		// Create a context supporting the domain with repositories resources
		this.domainContext = new UserAccountManagementDomainContext();
		this.domainContext.addResource(writeModelStore, UserAccountStore.class.getName(), true);
		this.domainContext.addResource(readModelRepository, UserAccountRepository.class.getName(), true);
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
		this.readModelRepository = null;
		this.writeModelStore = null;
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
		Entity eventId = new UserAccountIdentityCreation(accountId);
		UserAccountCreateCommand event = new UserAccountCreateCommand(eventId);
		event.accountUID = (String) accountId.value();

		// Add into a store
		writeModelStore.append(account, event);
		// Retriev persisted object from datastore (write model)
		UserAccountAggregate currentAccount = writeModelStore.findFrom(event.accountUID);
		// Check none default role assigned
		Set<ApplicativeRole> currentRoles = currentAccount.assignedRoles();
		assertTrue(currentRoles.isEmpty(), "Shall not include any default role!");

		// Simulate assignment of new role with reuse of natural key based for
		// identifier generation
		String roleName = "ISO";
		AssignRoleToUserAccountCommand roleAssignmentCommand = new AssignRoleToUserAccountCommand(
				new DenormalizedEntityImpl(
						new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString())));
		// Which role shall be assigned
		roleAssignmentCommand.assignedRole = new ApplicativeRoleDTO(roleName);
		assertEquals(HistoryState.COMMITTED, roleAssignmentCommand.assignedRole.status,
				"Default state shall be Committed!");
		// What user account should be upgraded
		roleAssignmentCommand.userAccountIdentifier = (String) account.identified().value();

		// Add the role to the aggregate account
		currentAccount.handle(roleAssignmentCommand, this.domainContext);

		// Reload the saved state of account from store
		currentAccount = writeModelStore.findFrom(event.accountUID);
		assertTrue(currentAccount.assignedRoles().size() == 1, "ISO role shall have been saved!");
		// Verify that role have not predecessor version (it's the initial version)
		for (ApplicativeRole assignedRole : currentAccount.assignedRoles()) {
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
		roleAssignmentCommand.userAccountIdentifier = (String) currentAccount.identified().value();

		// Update the role (history graph) to the aggregate account regarding a removed
		// role
		currentAccount.handle(roleAssignmentCommand, this.domainContext);
		// Reload the saved state of account from store
		currentAccount = writeModelStore.findFrom(event.accountUID);
		assertTrue(currentAccount.assignedRoles().size() == 1,
				"Modified existent ISO role shall have been maintained!");

		// Verify that role have one predecessor versione (it's the initial version
		// before it's cancelled version)
		for (ApplicativeRole assignedRole : currentAccount.assignedRoles()) {
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
