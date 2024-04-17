package org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.framework.domain.infrastructure.IDomainStore;
import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.cybnity.framework.domain.infrastructure.SnapshotProcessEventStreamPersistenceBased;
import org.cybnity.framework.domain.model.*;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.persistence.FactRecord;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.DomainResourceStoreRedisImpl;
import org.cybnity.infastructure.technical.persistence.store.impl.redis.PersistentObjectNamingConvention;

import java.io.Serializable;

/**
 * Mocked store implementation class simulating a normal implementation of a store dedicated to a custom domain object.
 * This store is implemented with support of snapshot capabilities.
 */
public class TenantAggregateStoreImplExample extends DomainResourceStoreRedisImpl implements IDomainStore<Tenant> {

    private static TenantAggregateStoreImplExample singleton;

    /**
     * Default constructor.
     *
     * @param ctx                   Mandatory context.
     * @param dataOwner             Mandatory domain which is owner of the persisted object types into the store.
     * @param managedObjectCategory Mandatory type of convention applicable for the type of object which is managed by this store.
     * @param snapshotsCapability   Optional snapshots repository able to be used by this store helping to optimize events rehydration.
     * @throws UnoperationalStateException When impossible instantiation of UISAdapter based on context parameter.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     */
    public TenantAggregateStoreImplExample(IContext ctx, IDomainModel dataOwner, PersistentObjectNamingConvention.NamingConventionApplicability managedObjectCategory, ISnapshotRepository snapshotsCapability) throws UnoperationalStateException, IllegalArgumentException {
        super(ctx, dataOwner, managedObjectCategory, snapshotsCapability);
    }

    /**
     * Get a store instance.
     *
     * @param ctx                   Mandatory context.
     * @param dataOwner             Mandatory domain which is owner of the persisted object types into the store.
     * @param managedObjectCategory Mandatory type of convention applicable for the type of object which is managed by this store.
     * @param snapshotsCapability   Optional snapshots repository able to be used by this store helping to optimize events rehydration.
     * @return A singleton instance.
     * @throws UnoperationalStateException When impossible instantiation of UISAdapter based on context parameter.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     */
    public static TenantAggregateStoreImplExample instance(IContext ctx, IDomainModel dataOwner, PersistentObjectNamingConvention.NamingConventionApplicability managedObjectCategory, ISnapshotRepository snapshotsCapability) throws UnoperationalStateException, IllegalArgumentException {
        if (singleton == null) {
            // Initializes singleton instance
            singleton = new TenantAggregateStoreImplExample(ctx, dataOwner, managedObjectCategory, snapshotsCapability);
        }
        return singleton;
    }

    @Override
    public void append(Tenant tenant, ISessionContext ctx) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException {
        if (tenant != null) {
            this.append(tenant);
        }
    }

    @Override
    public void append(Tenant tenant) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException {
        if (tenant != null) {
            // Execute the storage action to the store's stream
            appendToStream(tenant.identified(), tenant.changeEvents());

            // --- SNAPSHOT ACTIVATION ---
            ISnapshotRepository snapRepo = this.snapshotsRepository();
            if (snapRepo != null) {
                final Identifier id = tenant.identified();
                // Create and save a snapshot version into snapshots repository
                SnapshotProcessEventStreamPersistenceBased snapshotProcess = new SnapshotProcessEventStreamPersistenceBased(/* streamedEventsProvider*/ this, /* snapshotsPersistenceSystem */ snapRepo) {
                    @Override
                    protected HydrationCapability getRehydratedInstanceFrom(EventStream eventStream) throws IllegalArgumentException {
                        // Re-hydrate events from stream
                        return Tenant.instanceOf(id, eventStream.getEvents());
                    }

                    @Override
                    protected String snapshotsNamespace() {
                        return snapshotsStorageNameSpace;
                    }
                };

                // Generate snapshot and save it into snapshots repository
                snapshotProcess.generateSnapshot(tenant.identified().value().toString());
            }
        }
    }

    @Override
    public Tenant findEventFrom(Identifier identifier, ISessionContext ctx) throws IllegalArgumentException, UnoperationalStateException {
        if (ctx == null) throw new IllegalArgumentException("ctx parameter is required!");
        return findEventFrom(identifier);
    }

    /**
     * Get the last version of Tenant full state from its write-model, as last version known when the change identifier was applied.
     * When snapshot repository is existing, this service attempt to find latest snapshot version for re-hydration performance optimization;
     * else read origin event stream for re-hydration of the tenant to search and return.
     *
     * @param identifier Mandatory identifier of the Tenant to load.
     * @return A tenant full state valued.
     */
    @Override
    public Tenant findEventFrom(Identifier identifier) throws IllegalArgumentException, UnoperationalStateException {
        if (identifier != null) {
            // Read potential existing snapshot when available repository
            String snapshotVersion = String.valueOf(Tenant.serialVersionUID());
            ISnapshotRepository snapRepo = snapshotsRepository();
            if (snapRepo != null) {
                ISnapshot rehydratedVersionContainer = snapRepo.getLatestSnapshotById(identifier.value().toString(), snapshotsStorageNameSpace);
                if (rehydratedVersionContainer != null && FactRecord.class.isAssignableFrom(rehydratedVersionContainer.getClass())) {
                    FactRecord fact = (FactRecord) rehydratedVersionContainer;
                    Serializable rehydratableObject = fact.body();
                    if (rehydratableObject != null && HydrationCapability.class.isAssignableFrom(rehydratableObject.getClass())) {
                        // Load any events since snapshot was taken
                        EventStream stream = loadEventStreamAfterVersion(identifier.value().toString(), snapshotVersion);
                        if (stream != null) {
                            // Replay these events to update snapshot
                            Tenant tenantObj = (Tenant) rehydratableObject;
                            tenantObj.replayEvents(stream);
                            return tenantObj;// Return rehydrated snapshot based instance
                        }
                    }

                } // else  None available persisted snapshot
            }

            // --- None active snapshot supporting this store, or none rehydrated version retrieved from snapshots repository ---
            // Load events and aggregate from store
            EventStream stream = loadEventStream(identifier.value().toString());
            if (stream != null) {
                // Re-hydrate event from origin stream and return instance
                return Tenant.instanceOf(identifier, stream.getEvents());
            }
        }
        return null;
    }

}
