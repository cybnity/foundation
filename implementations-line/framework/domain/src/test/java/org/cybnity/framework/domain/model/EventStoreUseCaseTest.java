package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.sample.writemodel.DomainEventsStoreImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentityCreation;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test of DomainEvent behaviors regarding its supported requirements.
 *
 * @author olivier
 */
public class EventStoreUseCaseTest {

    private DomainEventsStoreImpl persistenceOrientedStore;

    @BeforeEach
    public void initStore() {
        this.persistenceOrientedStore = (DomainEventsStoreImpl) DomainEventsStoreImpl.instance();
    }

    @AfterEach
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
        DomainEntity identity = new UserAccountIdentityCreation(id);
        UserAccountChanged event = new UserAccountChanged(identity);
        // Add into a store
        persistenceOrientedStore.append(event);
        // Search persisted event log
        DomainEvent saved = persistenceOrientedStore.findEventFrom(id);
        assertNotNull(saved);
    }

}
