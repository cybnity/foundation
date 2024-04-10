package org.cybnity.infrastructure.technical.persistence.store.impl.redis;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.model.*;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.DomainResourceStoreRedisImpl;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.PersistentObjectNamingConvention;
import org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock.AccessControlDomainModelSample;
import org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock.TenantMockHelper;
import org.junit.jupiter.api.*;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Test and check the usage of Redis stream as event store's persistence system for aggregate event records management (e.g as write-model).
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISPersistentStreamAdapterUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private final Logger logger = Logger.getLogger(UISPersistentStreamAdapterUseCaseTest.class.getName());

    private EventStore persistenceOrientedStore;
    private IDomainModel dataOwner;
    private PersistentObjectNamingConvention.NamingConventionApplicability persistentObjectNamingConvention;
    private Class<?> managedObjectType;

    public UISPersistentStreamAdapterUseCaseTest() {
        super();
    }

    @BeforeEach
    public void initResources() throws Exception {
        dataOwner = new AccessControlDomainModelSample();
        persistentObjectNamingConvention = PersistentObjectNamingConvention.NamingConventionApplicability.TENANT;
        managedObjectType = EventRecord.class;
    }

    @AfterEach
    public void cleanValues() {
        if (persistenceOrientedStore != null) persistenceOrientedStore.freeResources();
        persistenceOrientedStore = null;
        managedObjectType = null;
        persistentObjectNamingConvention = null;
        dataOwner = null;
        super.cleanValues();
    }

    /**
     * Test of simple persistence-oriented store write and read capability.
     * This test try to push multiples event records (e.g simulating an aggregate changes) into a dedicated stream (e.g used as event store) via adapter, and check that all records are retrieved from Redis.
     * This usage scenario validate the support of an event store by a Redis adapter as persistence system provider via Redis stream.
     */
    @Test
    public void givenIdentifiedEventRecord_whenAppendedInPersistenceOrientedStore_thenEntryRetrieved()
            throws Exception {
        // Create a store managing EventRecord streamed messages
        persistenceOrientedStore = DomainResourceStoreRedisImpl.instance(getContext(), dataOwner, persistentObjectNamingConvention, managedObjectType);

        // --- TEST DATA PREPARATION ---
        // Simulate creation of a domain object representing a tenant sample
        String tenantLabel = "CYBNITY";
        Boolean isActiveTenant = Boolean.TRUE;
        // Prepare and execute a tenant registration (simulating a first creation already performed by a start of company's platform instance)
        Command originEvent = TenantMockHelper.prepareRegisterTenantCommand(tenantLabel, /*Simulate that it is an operational status already defined as activated for registered users*/ isActiveTenant);
        TenantBuilder builder = new TenantBuilder(tenantLabel, /* Predecessor event of new tenant creation */ originEvent.getIdentifiedBy(), isActiveTenant);
        builder.buildInstance();
        Tenant tenant = builder.getResult();

        // Load event stream regarding the type of tenant version managed with equals identifier
        EventStream changesStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString());
        // Check no existing previous history about same duplicated identifier of domain object
        Assertions.assertNull(changesStream, "Should be empty of any previous change relative to the new tenant entity!");

        // --- Add subscriber allowing control of normally promoted change events to read-model during appendToStream execution
        EventsCheck checker = new EventsCheck(tenant.changeEvents(), ConcreteDomainChangeEvent.class);
        this.persistenceOrientedStore.subscribe(checker);

        // Add creation item into a store as persistent fact record
        persistenceOrientedStore.appendToStream(tenant.identified() /* domain object unique identifier */, tenant.changeEvents() /*delta of changes to save as new events on lifecycle archived */);

        // --- CHANGE NOTIFICATION TO READ-MODEL VERIFICATION
        Assertions.assertTrue(checker.isAllEventsToCheckHaveBeenFound(), checker.notAlreadyChecked.size() + " changes had not been notified to subscriber!");

        // --- TEST RETRIEVE CHECK: Search persisted history (events stream dedicated to the aggregate record)
        // Search history stream of the store known business object previous version (created previously)
        EventStream BOLifecycleStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString());
        // Verify found history
        Assertions.assertNotNull(BOLifecycleStream, "Shall exist since tenant original creation event!");
        Assertions.assertFalse(BOLifecycleStream.getEvents().isEmpty(), "Shall contain original creation event!");
        int creationEventCount = BOLifecycleStream.getEvents().size();

        // --- TEST DATA ENHANCEMENT OF BUSINESS OBJECT OVER CHANGE EVENT (e.g by application service layer)
        // Simulate a change about domain object via a change event operation (eg change of an attribute of domain object)
        // as an application service could perform from a read-model repository to identify which change event shall be stored about the domain object evolution operation

        // Simulate some changes to apply on the entity as original instance
        tenant.changeEvents().clear(); // Avoid all previous history of change, and simulate a reloaded tenant aggregate
        tenant.activate();// Make modification generating new change events in internal history
        tenant.deactivate();
        tenant.activate();
        Assertions.assertEquals(3, tenant.changeEvents().size(), "shall only includes 3 additional changes applied!");
        int totalChangesCount = creationEventCount + 3;

        // Define new upgrade change event as eligible to the domain object history stream (record version according object origin type version)
        checker = new EventsCheck(tenant.changeEvents(), ConcreteDomainChangeEvent.class);
        this.persistenceOrientedStore.subscribe(checker);
        // Save stream status into the store
        persistenceOrientedStore.appendToStream(tenant.identified(), tenant.changeEvents() /* Get event to commit additionally to the stream managed in store */);
        // --- CHANGE NOTIFICATION TO READ-MODEL VERIFICATION
        Assertions.assertTrue(checker.isAllEventsToCheckHaveBeenFound(), checker.notAlreadyChecked.size() + " changes had not been notified to subscriber!");

        // --- TEST RETRIEVE CHECK: search persisted history including the 2 events (creation + upgrade)
        // Normally the read of last domain object value (merged of the domain object) shall be performed over a read-model's projection
        // Here we check only the event changes flow allowing capability to rehydrate all the domain object from its lifecycle history (based on event sourcing recorded)

        // Search lifecycle stream including all events relative to the domain object
        BOLifecycleStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString() /* first original uui of domain object (tenant company named) */);
        // Check ths existing 2 event of its life that are confirmed recorded and maintained by the Store
        Assertions.assertEquals(totalChangesCount, BOLifecycleStream.getEvents().size(), "Invalid qty of store events regarding BO lifecycle history!");
    }

    private static class EventsCheck extends DomainEventSubscriber<DomainEvent> {

        private final List<DomainEvent> notAlreadyChecked = new LinkedList<>();
        private final Class<?> observedType;

        public EventsCheck(List<DomainEvent> toCheck, Class<?> observedEventType) {
            super();
            // Prepare validation container
            notAlreadyChecked.addAll(toCheck);
            // Define handled type
            this.observedType = observedEventType;
        }

        @Override
        public void handleEvent(DomainEvent event) {
            if (event != null) {
                // Search and remove any existing from the list of origins to check
                boolean removed = notAlreadyChecked.remove(event);
            }
        }

        @Override
        public Class<?> subscribeToEventType() {
            return observedType;
        }

        public boolean isAllEventsToCheckHaveBeenFound() {
            return this.notAlreadyChecked.isEmpty();
        }
    }
}