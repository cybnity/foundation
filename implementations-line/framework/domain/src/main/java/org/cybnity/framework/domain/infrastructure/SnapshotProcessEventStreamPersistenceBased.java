package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.domain.ISnapshotRepository;
import org.cybnity.framework.domain.model.EventStream;
import org.cybnity.framework.domain.model.HydrationCapability;
import org.cybnity.framework.domain.model.IEventStore;

/**
 * Implementation class of a snapshot process which produce and manage snapshots over an event stream (e.g origin stream of source subject eligible to snapshot).
 */
public abstract class SnapshotProcessEventStreamPersistenceBased extends AbstractSnapshotProcess {

    /**
     * Streamed events provider that are eligible to snapshots.
     */
    private final IEventStore streamStore;

    /**
     * Store of event stream use as persistence system for generated snapshots.
     */
    private final ISnapshotRepository snapshotsPersistenceSystem;

    /**
     * Default constructor.
     * @param streamedEventsProvider Mandatory readable store regarding change events eligible to snapshot process.
     * @param snapshotsPersistenceSystem Mandatory persistence system supporting the storage of snapshots generated by this process execution(s).
     * @throws IllegalArgumentException When any mandatory parameter is not defined.
     */
    public SnapshotProcessEventStreamPersistenceBased(IEventStore streamedEventsProvider, ISnapshotRepository snapshotsPersistenceSystem) throws IllegalArgumentException {
        super();
        if (streamedEventsProvider == null)
            throw new IllegalArgumentException("The stream events provider parameter is required!");
        this.streamStore = streamedEventsProvider;
        if (snapshotsPersistenceSystem == null)
            throw new IllegalArgumentException("The snapshots persistence system parameter is required!");
        this.snapshotsPersistenceSystem = snapshotsPersistenceSystem;

    }

    /**
     * Snapshot generation capability callable according to the cycle of creation managed externally.
     *
     * @param streamedObjectIdentifier Mandatory identifier of the source event type that is subject to snapshot.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    @Override
    public void generateSnapshot(String streamedObjectIdentifier) throws IllegalArgumentException {
        if (streamedObjectIdentifier == null || streamedObjectIdentifier.isEmpty())
            throw new IllegalArgumentException("Streamed object identifier parameter is required!");
        // Load all events from the source domain object's life history
        EventStream stream = streamStore.loadEventStream(streamedObjectIdentifier);
        // Get re-hydrated version of instance type based on change events history
        HydrationCapability hydratedInstance = getRehydratedInstanceFrom(stream);
        // Save full state version of instance into the stream store
        snapshotsPersistenceSystem.saveSnapshot(streamedObjectIdentifier, hydratedInstance, stream.getVersion());
    }

    /**
     * Build instance type according to the type as a rehydrated full state version.
     *
     * @param history Mandatory stream hosting the event type changes history.
     * @return Re-hydrated instance eligible to snapshot.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    protected abstract HydrationCapability getRehydratedInstanceFrom(EventStream history) throws IllegalArgumentException;
}