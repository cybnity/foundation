package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.infrastructure.DomainEventInMemoryStoreImpl;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountIdentity;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test of DomainEvent behaviors regarding its supported requirements.
 *
 * @author olivier
 */
public class EventStoreUseCaseTest {

    private DomainEventInMemoryStoreImpl persistenceOrientedStore;

    @BeforeEach
    public void initStore() {
        this.persistenceOrientedStore = (DomainEventInMemoryStoreImpl) DomainEventInMemoryStoreImpl.instance();
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
    public void givenIdentifiedEvent_whenAppendedInPersistenceOrientedStore_thenEntryRetrieved()
            throws Exception {
        // --- TEST DATA PREPARATION
        // Simulate creation of a domain object representing a user account identity sample
        Identifier id = IdentifierStringBased.generate(null);
        UserAccountIdentity domainObjectState = new UserAccountIdentity(id);

        // Load event stream regarding the type of user account version managed with equals identifier
        EventStream changesStream = persistenceOrientedStore.loadEventStream(domainObjectState.identified());
        // Check no existing previous history about same duplicated identifier of domain object
        Assertions.assertNull(changesStream, "Should be empty of any previous change relative to the new user account entity!");

        // Simulate some changes to apply on the entity as original creation event
        UserAccountChanged creationEvt = new UserAccountChanged(new DomainEntity(IdentifierStringBased.generate(null)) /* id of change event*/);
        creationEvt.changedAccountRef = domainObjectState.reference(); // Reference the domain entity source (eligible to serialization into the store's records stream)
        // Save the fact in store supporting the type of aggregate (UserAccountIdentity)
        changesStream = new EventStream(Long.parseLong(domainObjectState.versionHash()), creationEvt); // init origin user account creation event as first item of history
        // Add creation item into a store as persistent fact record
        persistenceOrientedStore.appendToStream(domainObjectState.identified() /* domain object unique identifier */, /* version of domain object type */ changesStream.getVersion(), changesStream.getEvents() /*delta of changes to save as new events on lifecycle archived*/);

        // --- TEST RETRIEVE CHECK: Search persisted history (events stream dedicated to the aggregate record)
        // Search history stream of the store known business object previous version (created previously)
        EventStream BOLifecycleStream = persistenceOrientedStore.loadEventStream(domainObjectState.identified());
        // Verify found history
        Assertions.assertNotNull(BOLifecycleStream, "Shall exist since user account original creation event!");
        Assertions.assertFalse(BOLifecycleStream.getEvents().isEmpty(), "Shall contain original creation event!");

        // --- TEST DATA ENHANCEMENT OF BUSINESS OBJECT OVER CHANGE EVENT (e.g by application service layer)
        // Simulate a change about domain object via a change event operation (eg change of on attribute of domain object)
        // as an application service could perform from a read-model repository to identify which change event shall be stored about the domain object evolution operation
        ConcreteDomainChangeEvent updateEvt = new ConcreteDomainChangeEvent(new DomainEntity(IdentifierStringBased.generate(null)) /* id of new change event*/);
        updateEvt.setChangedModelElementRef(domainObjectState.reference());// Identify the user account which was upgraded by any value that can be also included into the updateEvt instance when committed
        // Define a sample representing an attribute upgraded on the domain user account
        String BOAttrName = "loginName";
        String BOAttrValue = "sampleLogin";
        updateEvt.appendSpecification(new Attribute(BOAttrName, BOAttrValue));

        // Define new upgrade change event as eligible to the domain object history stream
        changesStream = new EventStream(Long.parseLong(domainObjectState.versionHash()), updateEvt);
        // Save stream status into the store
        persistenceOrientedStore.appendToStream(domainObjectState.identified(), changesStream.getVersion() /*equals stream version to enhance*/, changesStream.getEvents() /* Get event to commit additionally to the stream managed in store*/);

        // --- TEST RETRIEVE CHECK: search persisted history including the 2 events (creation + upgrade)
        // Normally the read of last domain object value (merged of the domain object) shall be performed over a read-model's projection
        // Here we check only the event changes flow allowing capability to rehydrate all the domain object from its lifecycle history (based on event sourcing recorded)

        // Search lifecycle stream including all events relative to the domain object
        BOLifecycleStream = persistenceOrientedStore.loadEventStream(domainObjectState.identified() /* first original uui of domain object (user account identity) */);
        // Check ths existing 2 event of its life that are confirmed recorded and maintained by the Store
        Assertions.assertEquals(2, BOLifecycleStream.getEvents().size(), "Invalid qty of store events regarding BO lifecycle history!");
    }

}
