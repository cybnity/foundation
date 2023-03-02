package org.cybnity.framework.domain.model;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.sample.readmodel.EventStored;
import org.cybnity.framework.domain.model.sample.writemodel.LogsEventStoreImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.BaseConstants;
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

    private LogsEventStoreImpl persistenceOrientedStore;

    @Before
    public void initStore() {
	this.persistenceOrientedStore = (LogsEventStoreImpl) LogsEventStoreImpl.instance();
    }

    @After
    public void cleanStore() {
	this.persistenceOrientedStore = null;
    }

    /**
     * Test of simple persistence-oriented store write and read capability.
     * 
     * @throws Exception
     */
    @Test
    public void givenIdentifiedStoredEvent_whenAppendedInPersistenceOrientedStore_thenEntryRetrieved()
	    throws Exception {
	// Create an identifiable event
	Identifier id = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString());
	Entity identity = new UserAccountIdentityCreation(id);
	UserAccountChanged event = new UserAccountChanged(identity);
	// Add into a store
	persistenceOrientedStore.append(event);
	// Search persisted event log
	EventStored saved = persistenceOrientedStore.findEventFrom(id);
	assertNotNull(saved);
    }

}
