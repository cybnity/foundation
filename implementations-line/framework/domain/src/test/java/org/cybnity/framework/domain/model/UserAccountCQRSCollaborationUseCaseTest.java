package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.ProcessManager;
import org.cybnity.framework.domain.application.sample.AssignRoleToUserAccountCommand;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.application.sample.UserAccountCreateCommand;
import org.cybnity.framework.domain.application.sample.UserAccountManagementDomainContext;
import org.cybnity.framework.domain.application.sample.UserAccountManagementProcessesImpl;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.domain.model.sample.readmodel.DenormalizedEntityImpl;
import org.cybnity.framework.domain.model.sample.readmodel.UserAccountDTO;
import org.cybnity.framework.domain.model.sample.readmodel.UserAccountRepository;
import org.cybnity.framework.domain.model.sample.writemodel.DomainEntityImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStoreImpl;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of Domain object (UserAccountAggregate by write model) behaviors
 * regarding its supported requirements in a CQRS pattern integration (with
 * UserAccountDTO by read model).
 * 
 * @author olivier
 *
 */
public class UserAccountCQRSCollaborationUseCaseTest {

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
    private Entity accountOwner;
    private Identifier accountId;

    @Before
    public void initCQRSModelsDatastores() {
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

    @Before
    public void initUserAccountSample() {
	accountOwner = new DomainEntityImpl(
		new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
	accountId = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString());
    }

    @After
    public void cleanUserAccountSample() {
	this.accountOwner = null;
    }

    @After
    public void cleanCQRSModelsDatastores() {
	this.readModelRepository = null;
	this.writeModelStore = null;
	this.domainContext = null;
    }

    /**
     * Test automatic synchronization of ReadModel when the WriteModel is updated
     * regarding a new UserAccount object created. This test validate that
     * collaboration between a Datastore (write/command model) and a repository
     * (read model dedicated to a type of aggregate domain object) is ensured via
     * events promotion.
     * 
     * @throws Exception When problem of immutability.
     */
    @Test
    public void givenWriteModelStore_whenDomainObjectChanged_thenReadModelUpdatedViaSubscribers() throws Exception {
	// Create a domain event requested to create a new user account aggregate object
	UserAccountAggregate account = new UserAccountAggregate(accountId, accountOwner.reference());
	Entity eventId = new UserAccountIdentityCreation(accountId);
	UserAccountCreateCommand event = new UserAccountCreateCommand(eventId);
	event.accountUID = (String) accountId.value();
	event.userIdentity = accountOwner.reference();

	// Store the new instance of account into the write model
	writeModelStore.append(account, event);

	// Read the refreshed read model (query model)
	UserAccountDTO dtoVersion = readModelRepository.findFrom(event.accountUID);
	assertNotNull(dtoVersion);
    }

    @Test
    public void givenProcessManager_whenAPICommandsReceived_thenModelsOrchestrationViaHandlers() throws Exception {
	// Use a facade application service implementing a ProcessManager approach for
	// API exposure of supported Commands (via handlers delegation)
	ProcessManager processManager = new UserAccountManagementProcessesImpl(domainContext);

	// Create a domain event requested to create a new user account aggregate object
	UserAccountAggregate account = new UserAccountAggregate(accountId, accountOwner.reference());
	Entity eventId = new UserAccountIdentityCreation(accountId);
	UserAccountCreateCommand event = new UserAccountCreateCommand(eventId);
	event.accountUID = (String) accountId.value();
	event.userIdentity = accountOwner.reference();

	// Execute a creation of new user account via manager
	// writeModelStore.append(account, event);
	processManager.handle(event, domainContext);

	// Find created account
	UserAccountDTO dtoVersion = readModelRepository.findFrom(event.accountUID);
	// Verify that account have none role defined
	assertTrue(dtoVersion.getRoles() == null || dtoVersion.getRoles().isEmpty());

	// Create update command regarding new applicative role to assign for user
	// account
	AssignRoleToUserAccountCommand roleAssignmentCommand = new AssignRoleToUserAccountCommand(
		new DenormalizedEntityImpl(
			new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString())));
	String roleName = "CISO";
	// Which role shall be assigned
	roleAssignmentCommand.assignedRole = new ApplicativeRoleDTO(roleName);
	// What user account should be upgraded
	roleAssignmentCommand.userAccountIdentifier = (String) dtoVersion.getUserAccountEntityIdentifier().identified()
		.value();
	// Verify original account id is known
	assertEquals("Problem of user account identifier value retrieved from DTO!", event.accountUID,
		roleAssignmentCommand.userAccountIdentifier);

	// Execute a role assignment to the user account
	processManager.handle(roleAssignmentCommand, domainContext);

	// Find the new version of the updated account (normally automatically refreshed
	// into the Read Model)
	UserAccountDTO upgradedVersion = readModelRepository.findFrom(event.accountUID);
	assertTrue(!upgradedVersion.getRoles().isEmpty());
	boolean foundAssignedRole = false;
	for (ApplicativeRoleDTO aRole : upgradedVersion.getRoles()) {
	    if (aRole.getName().equals(roleName)) {
		foundAssignedRole = true;
		break;
	    }
	}
	// Check that role have been assigned
	assertTrue("New role assignment have not been performed!", foundAssignedRole);
    }

}
