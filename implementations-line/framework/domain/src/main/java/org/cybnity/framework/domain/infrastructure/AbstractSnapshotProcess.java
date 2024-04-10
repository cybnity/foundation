package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.UnoperationalStateException;

/**
 * Snapshot generation and persistence can be delegated to a background thread, following an event stream evolutions.
 * This capability shall manage the generation of snapshots according time rules (e.g generation of a new snapshot only after some set number of change events have occurred since the latest snapshot).
 */
public abstract class AbstractSnapshotProcess {

    /**
     * Produce snapshot of an event (e.g aggregate) as full state version, and store it into a persistence system (e.g origin event stream, independent snapshot store).
     *
     * @param streamedObjectIdentifier Mandatory identifier of the source event type that is subject to snapshot.
     * @throws IllegalArgumentException    When any mandatory parameter is not defined.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    public abstract void generateSnapshot(String streamedObjectIdentifier) throws IllegalArgumentException, UnoperationalStateException;
}
