package org.cybnity.framework.domain.application;

import static org.junit.Assert.assertNotNull;

import org.cybnity.framework.domain.EventIdentifierStringBased;
import org.cybnity.framework.domain.application.sample.EventStoreImpl;
import org.cybnity.framework.domain.application.sample.EventStored;
import org.cybnity.framework.domain.model.sample.UserAccountCreationCommitted;
import org.cybnity.framework.domain.model.sample.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of DomainEvent behaviors regarding its supported requirements.
 * 
 * @author olivier
 *
 */
public class EventStoreUseCaseTest {

    private EventStoreImpl store;

    @Before
    public void initStore() {
	this.store = (EventStoreImpl) EventStoreImpl.instance();
    }

    @After
    public void cleanStore() {
	this.store = null;
    }

    @Test
    public void givenIdentifiedEvent_whenAppended_thenPersistedEntry() {
	// Create an identifiable event
	Identifier id = new EventIdentifierStringBased("uid", "KJGF8765");
	Entity identity = new UserAccountIdentityCreation(id);
	UserAccountCreationCommitted event = new UserAccountCreationCommitted(identity);
	// Add into a store
	store.append(event);
	// Search persisted event log
	EventStored saved = store.findEventFrom(id);
	assertNotNull(saved);
    }

    @Test
    public void givenWriteModelStore_whenEventAppended_thenSubscribersNotified() {
	// Create a write model store (e.g storage system of domain event logs)

	// Create a read model store (e.g interested to be notified about changes
	// observed in write model)

	// Add subscription of read model to the write model registry

	// Create any domain event and store into the write model

	// Verify that read model had been automatically informed of the write model
	// changed
    }

}
