package org.cybnity.infrastructure.technical.persistence.store.impl.redis;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.infrastructure.IDomainStore;
import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.cybnity.framework.domain.model.*;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.PersistentObjectNamingConvention;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.SnapshotRepositoryRedisImpl;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.SnapshotSpaceName;
import org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock.AccessControlDomainModelSample;
import org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock.TenantAggregateStoreImplExample;
import org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock.TenantMockHelper;
import org.junit.jupiter.api.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Test and check the usage of Redis stream as event store's persistence system for aggregate event records management (e.g as write-model).
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISPersistentStreamAdapterUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private TenantAggregateStoreImplExample persistenceOrientedStore;
    private ISnapshotRepository snapshotsRepo;

    private IDomainModel dataOwner;
    private PersistentObjectNamingConvention.NamingConventionApplicability persistentObjectNamingConvention;

    public UISPersistentStreamAdapterUseCaseTest() {
        super();
    }

    @BeforeEach
    public void initResources() {
        dataOwner = new AccessControlDomainModelSample();
        persistentObjectNamingConvention = PersistentObjectNamingConvention.NamingConventionApplicability.TENANT;
    }

    @AfterEach
    public void cleanValues() {
        if (persistenceOrientedStore != null) persistenceOrientedStore.freeUpResources();
        persistenceOrientedStore = null;
        persistentObjectNamingConvention = null;
        dataOwner = null;
        if (snapshotsRepo != null) snapshotsRepo.freeUpResources();
        snapshotsRepo = null;
        super.cleanValues();
    }

    /**
     * Get example of Tenant.
     *
     * @param label Tenant label.
     * @return A sample.
     */
    private Tenant sample(String label) throws ImmutabilityException {
        // Simulate creation of a domain object representing a tenant sample
        Boolean isActiveTenant = Boolean.TRUE;
        // Prepare and execute a tenant registration (simulating a first creation already performed by a start of company's platform instance)
        Command originEvent = TenantMockHelper.prepareRegisterTenantCommand(label, /*Simulate that it is an operational status already defined as activated for registered users*/ isActiveTenant);
        TenantBuilder builder = new TenantBuilder(label, /* Predecessor event of new tenant creation */ originEvent.getIdentifiedBy(), isActiveTenant);
        builder.buildInstance();
        return builder.getResult();
    }

    private void checkNoExistingTenantInStore(Tenant sample) throws UnoperationalStateException {
        // Load event stream regarding the type of tenant version managed with equals identifier
        EventStream changesStream = persistenceOrientedStore.loadEventStream(sample.identified().value().toString());
        // Check no existing previous history about same duplicated identifier of domain object
        Assertions.assertNull(changesStream, "Should be empty of any previous change relative to the new tenant entity!");
    }

    private EventsCheck registerEventsChecker(Tenant tenant) {
        // --- Add subscriber allowing control of normally promoted change events to read-model during appendToStream execution
        EventsCheck checker = new EventsCheck(tenant.changeEvents(), ConcreteDomainChangeEvent.class);
        this.persistenceOrientedStore.subscribe(checker);
        return checker;
    }

    /**
     * Get a persistence store implementation with or without support of snapshots capabilities.
     *
     * @param supportedBySnapshotRepository True when snapshots usage shall be configured into the returned store.
     * @return A store.
     * @throws UnoperationalStateException When impossible instantiation of the Redis adapter.
     */
    private TenantAggregateStoreImplExample getPersistenceOrientedStore(boolean supportedBySnapshotRepository) throws UnoperationalStateException {
        snapshotsRepo = (supportedBySnapshotRepository) ? new SnapshotRepositoryRedisImpl(getContext()) : null;
        // Voluntary don't use instance() method to avoid singleton capability usage during this test campaign
        return new TenantAggregateStoreImplExample(getContext(), dataOwner, persistentObjectNamingConvention, /* with or without help by a snapshots capability provider */ snapshotsRepo);
    }

    /**
     * Test of domain aggregate version storage, involving automatic snapshot version creation (by Redis store for future rehydration optimization), and attempt to retrieve aggregate last version based only on snapshot rehydrated.
     * This test creation a Tenant sample with multiple changed values (e.g simulating change events on aggregate) and store into a stream (e.g used as event store), that shall automatically generate snapshot version for future optimized read from redis.
     * This usage scenario validate the support of snapshots for optimization of rehydration via a Redis adapter as persistence system provider via Redis stream AND cache of snapshots.
     */
    @Test
    public void givenActivatedSnapshotRepository_whenReloadedDomainAggregate_thenLastSnapshotVersionRehydratedWithLastEvents() throws Exception {
        // Create a store managing streamed messages
        persistenceOrientedStore = getPersistenceOrientedStore(true /* With snapshots management capability activated */);
        // Allow usage of implementation methods dedicated to snapshots management regarding a specific type of aggregate
        IDomainStore<Tenant> tenantsStore = persistenceOrientedStore;
        String snapshotsStorageNamespace = ((SnapshotSpaceName) persistenceOrientedStore.snapshotVersionsStorageNamespace()).getSpaceNamePath();

        // ---- FIRST AGGREGATE TEST - USE CASE: without snapshot reload ----
        // Create and save an aggregate sample into the store by-passing the snapshot generation
        Tenant tenant1 = sample("CYBNITY_X");
        String tenant1_initial_commitVersion = tenant1.getCommitVersion();

        persistenceOrientedStore.appendToStream(tenant1.identified(), tenant1.changeEvents());
        // Verify that none snapshot version have not been generated into the snapshots repository
        ISnapshot tenant1Snap = snapshotsRepo.getLatestSnapshotById(tenant1.identified().value().toString(), snapshotsStorageNamespace);
        Assertions.assertNull(tenant1Snap, "None snapshot version shall exist at this moment!");

        // --- TEST RETRIEVE CHECK: Search initial persisted version
        // Search history stream of the store known business object previous version (created previously) BUT in way where none snapshot is existing
        Tenant retrievedTenant1 = tenantsStore.findEventFrom(tenant1.identified()); // return aggregate full state valued
        Assertions.assertNotNull(retrievedTenant1, "Shall have been found from store and rehydrated from events read since stream history!");
        Assertions.assertEquals(tenant1_initial_commitVersion, retrievedTenant1.getCommitVersion(), "Same shall always the same because not upgraded!");

        // ---- SECOND AGGREGATE TEST - USE CASE: with snapshot reload ----
        // Create and save another aggregate sample into the store (normally generating additional automatic snapshot created in snapshots repository)
        Tenant tenant2 = sample("CYBNITY_Y");
        String tenant2_initial_commitVersion = tenant2.getCommitVersion();
        tenantsStore.append(tenant2); // With snapshot repository automatic feeding

        // Add intermediary new change storage about tenant 1 (forcing the next search to make filtering relative to the snapshot successor events to rehydrate)
        retrievedTenant1.activate();
        retrievedTenant1.deactivate();
        retrievedTenant1.activate();
        String tenant1_updated_commitVersion = retrievedTenant1.getCommitVersion();
        Assertions.assertNotEquals(tenant1_initial_commitVersion, tenant1_updated_commitVersion, "Shall have been changed during the upgraded contents!");
        tenantsStore.append(retrievedTenant1);

        // Verify that a snapshot version have been generated into the snapshots repository
        Assertions.assertNotNull(snapshotsRepo.getLatestSnapshotById(tenant2.identified().value().toString(), snapshotsStorageNamespace), "Automatic saved snapshot version shall have been generated during append of aggregate instance in store!");

        // --- TEST RETRIEVE CHECK: Search persisted history (common events stream)
        // Search history stream of the store AND SNAPSHOT REPO about known business object current version
        Tenant retrievedTenant2 = tenantsStore.findEventFrom(tenant2.identified());

        // --- CHECK : verify that found tenant have been rehydrated at the same status of latest version
        Assertions.assertNotNull(retrievedTenant2, "Shall have been stored in store's history AND into snapshots repository!");
        // Verify if latest commit version is good retrieved
        Assertions.assertEquals(tenant2_initial_commitVersion, retrievedTenant2.getCommitVersion(), "Invalid rehydrated version from snapshots repository!");

        // --- TEST RETRIEVED of Tenant 1 ---
        Tenant futurRetrievedTenant1 = tenantsStore.findEventFrom(retrievedTenant1.identified());
        Assertions.assertEquals(tenant1_updated_commitVersion, futurRetrievedTenant1.getCommitVersion(), "Shall have been rehydrated in same version because none upgrade between the 2 moments!");
        Assertions.assertEquals(retrievedTenant1.status().isActive(), futurRetrievedTenant1.status().isActive(), "Shall be the last changed status!");
    }

    /**
     * Test of store write and read capability of aggregate from common stream supporting multiple aggregate versions.
     * This test try to push multiples event records (e.g simulating an aggregate changes) into a common stream via adapter, and check that only records regarding a specific domain aggregate (identified) are retrieved from Redis.
     * This usage scenario validate the support of a unique stream as storage system for all identifiable aggregates without need to create multiples dedicated stream (per aggregate identifier)
     */
    @Test
    public void givenIdentifiedAggregate_whenAppendedInMutualizedStoreStream_thenEntryRetrieved() throws Exception {
        // Create a store managing streamed messages
        persistenceOrientedStore = getPersistenceOrientedStore(false);

        // Simulate creation of a domain object representing a tenant sample
        Tenant tenant = sample("CYBNITY_1");
        checkNoExistingTenantInStore(tenant);
        // Add creation item into a store as persistent fact record
        persistenceOrientedStore.appendToStream(tenant.identified() /* domain object unique identifier */, tenant.changeEvents() /* delta of changes to save as new events on lifecycle archived */);

        // --- TEST RETRIEVE CHECK: Search persisted history (common events stream)
        // Search history stream of the store known business object previous version (created previously)
        EventStream BOLifecycleStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString());
        // Verify found history
        Assertions.assertNotNull(BOLifecycleStream, "Shall exist since tenant original creation event!");
        Assertions.assertFalse(BOLifecycleStream.getEvents().isEmpty(), "Shall contain original creation event!");
        int tenant1CreationEventCount = BOLifecycleStream.getEvents().size();

        // Simulate creation of another domain object representing another identifiable tenant
        Tenant tenant2 = sample("STARK_INDUSTRY");
        checkNoExistingTenantInStore(tenant2);
        // Add second tenant into the store
        persistenceOrientedStore.appendToStream(tenant2.identified() /* domain object unique identifier */, tenant2.changeEvents() /* delta of changes to save as new events on lifecycle archived */);

        // Simulate some changes to apply on the entity as original instance
        tenant.changeEvents().clear(); // Avoid all previous history of change, and simulate a reloaded tenant aggregate
        tenant.activate();// Make modification generating new change events in internal history
        tenant.deactivate();
        tenant.activate();
        Assertions.assertEquals(3, tenant.changeEvents().size(), "shall only includes 3 additional changes applied!");
        // Save status change of first aggregate into the store
        persistenceOrientedStore.appendToStream(tenant.identified(), tenant.changeEvents() /* Get event to commit additionally to the stream managed in store */);
        int totalChangesCount = tenant1CreationEventCount + 3;

        // --- TEST RETRIEVE CHECK: search persisted history
        // Search lifecycle stream including all events relative to the first domain object
        BOLifecycleStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString());
        // Check ths existing events of its life that are confirmed recorded and maintained by the Store
        Assertions.assertEquals(totalChangesCount, BOLifecycleStream.getEvents().size(), "Invalid qty of store events regarding BO lifecycle history!");
        // Check that only event relative to the domain object's identifier have been returned
        for (DomainEvent firstAggregateEvt : BOLifecycleStream.getEvents()) {
            ConcreteDomainChangeEvent changedTenant = (ConcreteDomainChangeEvent) firstAggregateEvt;
            Assertions.assertEquals(tenant.identified(), changedTenant.changeSourceIdentifier());
        }
    }

    /**
     * Test of simple persistence-oriented store write and read capability.
     * This test try to push multiples event records (e.g simulating an aggregate changes) into a dedicated stream (e.g used as event store) via adapter, and check that all records are retrieved from Redis.
     * This usage scenario validate the support of an event store by a Redis adapter as persistence system provider via Redis stream.
     */
    @Test
    public void givenIdentifiedEventRecord_whenAppendedInMutualizedStore_thenEntryRetrieved()
            throws Exception {
        // Create a store managing streamed messages
        persistenceOrientedStore = getPersistenceOrientedStore(false);

        // --- TEST DATA PREPARATION ---
        // Simulate creation of a domain object representing a tenant sample
        Tenant tenant = sample("CYBNITY");
        checkNoExistingTenantInStore(tenant);

        // --- Add subscriber allowing control of normally promoted change events to read-model during appendToStream execution
        EventsCheck checker = registerEventsChecker(tenant);

        // Add creation item into a store as persistent fact record
        persistenceOrientedStore.appendToStream(tenant.identified() /* domain object unique identifier */, tenant.changeEvents() /* delta of changes to save as new events on lifecycle archived */);

        // --- CHANGE NOTIFICATION TO READ-MODEL VERIFICATION
        Assertions.assertTrue(checker.isAllEventsToCheckHaveBeenFound(), checker.notAlreadyChecked.size() + " changes had not been notified to subscriber!");

        // --- TEST RETRIEVE CHECK: Search persisted history
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
        BOLifecycleStream = persistenceOrientedStore.loadEventStream(tenant.identified().value().toString() /* first original uid of domain object (tenant company named) */);
        // Check ths existing events of its life that are confirmed recorded and maintained by the Store
        Assertions.assertEquals(totalChangesCount, BOLifecycleStream.getEvents().size(), "Invalid qty of store events regarding BO lifecycle history!");
    }

    private static class EventsCheck implements IDomainEventSubscriber<DomainEvent> {

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