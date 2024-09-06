package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.ICleanup;
import org.cybnity.framework.domain.model.ISnapshot;

/**
 * To reduce (avoiding loading and replaying of a large portion of Event Stream) the loading time (and potential performance decrease) of Aggregates version from large Streams,
 * some snapshots can be managed regarding versions of Aggregate instances.
 * This capability allow to produce or to find the latest snapshot of an Aggregate instance as snapshots which are serialized copies of an Aggregate's full state, taken at certain moments in time, and that
 * can reside in an Event Stream as specific versions.
 */
public interface ISnapshotRepository extends ICleanup {

    /**
     * Find latest snapshot of a domain object identifier (subject that have been source of snapshot).
     *
     * @param originObjectIdentifier Mandatory logical identifier of the subject that is source of snapshot to find.
     * @param resourceNamespaceName  Optional namespace name of the resource to find.
     * @return A full state version of the snapshot. Or null when none found.
     * @throws IllegalArgumentException    When any mandatory parameter is not defined.
     * @throws UnoperationalStateException When system access is in failure.
     */
    ISnapshot getLatestSnapshotById(String originObjectIdentifier, String resourceNamespaceName) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Create and save an object version as snapshot (full state instance) into a persistent layer (e.g into independent cache repository; or into the origin event stream like a additional event appended between change events).
     *
     * @param snapshot              Mandatory full state of source object version.
     * @param resourceNamespaceName Optional namespace name of the resource to find.
     * @param expireIn              Optional duration in seconds that snapshot resource shall be persistent before to expire (becoming unavailable).
     * @throws IllegalArgumentException    When any mandatory parameter is not defined.
     * @throws UnoperationalStateException When system access is in failure.
     */
    void saveSnapshot(ISnapshot snapshot, String resourceNamespaceName, Long expireIn) throws IllegalArgumentException, UnoperationalStateException;
}
