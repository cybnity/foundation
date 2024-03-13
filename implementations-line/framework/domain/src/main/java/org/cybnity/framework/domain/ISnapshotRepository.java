package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.HydrationCapability;

/**
 * To reduce (avoiding loading and replaying of a large portion of Event Stream) the loading time (and potential performance decrease) of Aggregates version from large Streams,
 * some snapshots can be managed regarding versions of Aggregate instances.
 * This capability allow to produce or to find the latest snapshot of an Aggregate instance as snapshots which are serialized copies of an Aggregate's full state, taken at certain moments in time, and that
 * can reside in an Event Stream as specific versions.
 */
public interface ISnapshotRepository {

    /**
     * Find a latest snapshot of a domain object identifier (subject that have been source of snapshot).
     *
     * @param streamedObjectIdentifier    Mandatory identifier of the subject that is source of latest snapshot to find.
     * @param eventStreamVersion          Optional version of the origin event stream supporting the re-hydratable object version.
     * @return A full state version of the snapshot. Or null when none found.
     * @throws IllegalArgumentException When any mandatory parameter is not defined.
     */
    public HydrationCapability getLatestSnapshotById(String streamedObjectIdentifier, String eventStreamVersion) throws IllegalArgumentException;

    /**
     * Create and save an object version as snapshot (full state instance) into a persistent layer (e.g into independent cache repository; or into the origin event stream like a additional event appended between change events).
     *
     * @param streamedObjectIdentifier Mandatory identifier of the subject that is source of snapshot to create.
     * @param snapshot                 Mandatory full state of source object version.
     * @param eventStreamVersion       Optional version of the origin event stream supporting the re-hydratable object version.
     * @throws IllegalArgumentException When any mandatory parameter is not defined.
     */
    public void saveSnapshot(String streamedObjectIdentifier, HydrationCapability snapshot, String eventStreamVersion) throws IllegalArgumentException;
}
