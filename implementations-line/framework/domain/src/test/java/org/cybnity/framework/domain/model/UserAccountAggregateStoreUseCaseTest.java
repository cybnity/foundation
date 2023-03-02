package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.application.sample.UserAccountCreateCommand;
import org.cybnity.framework.domain.model.sample.writemodel.DomainEntityImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStoreImpl;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of Domain object (UserAccountAggregate by write model) behaviors
 * regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class UserAccountAggregateStoreUseCaseTest {

    /**
     * Datastore relative to all the account samples managed by a test.
     */
    private UserAccountStoreImpl collectionOrientedStore;

    private Entity accountOwner;
    private Identifier accountId;

    @Before
    public void initDatastores() {
	// Create a write model store (e.g storage system of domain event logs)
	this.collectionOrientedStore = (UserAccountStoreImpl) UserAccountStoreImpl.instance();
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
    public void cleanDatastores() {
	this.collectionOrientedStore = null;
    }

    /**
     * Test of simple collection-oriented store write and read capability.
     * 
     * @throws Exception When problem of immutability.
     */
    @Test
    public void givenIdentifiedUserAccount_whenAppendedInCollectionOrientedStore_thenEntryRetrieved() throws Exception {
	// Create an identifiable user account based on a owner entity
	UserAccountAggregate account = new UserAccountAggregate(accountId, accountOwner.reference());

	// Create an event simulating an original command of user account creation
	Entity eventId = new UserAccountIdentityCreation(accountId);
	UserAccountCreateCommand event = new UserAccountCreateCommand(eventId);
	event.accountUID = (String) accountId.value();

	// Add into a store
	collectionOrientedStore.append(account, event);

	// Search persisted object from datastore (write model)
	UserAccountAggregate saved = collectionOrientedStore.findFrom(event.accountUID);
	assertNotNull(saved);
    }

}
