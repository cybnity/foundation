package org.cybnity.framework.domain.model;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.List;

/**
 * Stream store (with an append-only approach) which maintain history of a type
 * of event (e.g Aggregate versions).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IEventStore {

    /**
     * Stop allocated resources specific to this store (e.g listener of stream, database access...).
     */
    public void freeResources();

    /**
     * Add an event into the store stream and commit the list changes.
     *
     * @param domainSubjectId Mandatory identifier of the root domain event which is subject of the changes.
     * @param changes       Mandatory ordered new events to commit at end of the stream managed by the store.
     * @throws IllegalArgumentException    When event to store is not compatible to be
     *                                     stored (e.g missing mandatory content into
     *                                     the event to store).
     * @throws ImmutabilityException       When problem of immutable version of stored
     *                                     event is occurred.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    public void appendToStream(Identifier domainSubjectId, List<DomainEvent> changes) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException;

    /**
     * Load all the events regarding a stored subject .
     *
     * @param domainSubjectId Mandatory identifier of the subject to load (e.g stream name based on subject identifier path).
     * @return A found stream in descending ordering (last event is first of list) or null.
     * @throws IllegalArgumentException    When missing mandatory parameter.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    public EventStream loadEventStream(String domainSubjectId) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Load all the events since a snapshot version that was taken.
     *
     * @param domainSubjectId   Mandatory identifier of the stored subject's stream to load (e.g path name of stream).
     * @param snapshotExpectedVersion Mandatory version of the snapshot stored event.
     * @return A found stream or null.
     * @throws IllegalArgumentException    When missing mandatory parameter.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    public EventStream loadEventStreamAfterVersion(String domainSubjectId, String snapshotExpectedVersion) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Load a subset of events (as a range) regarding a stream subject.
     *
     * @param domainSubjectId Mandatory identifier of the event stream subset to load.
     * @param skipEvents    How many event items shall be skipped before to load stream.
     * @param maxCount      How many event instance shall be taken regarding the
     *                      history flow.
     * @return A found stream in descending ordering (last event is first of list) or null.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When technical problem is occurred regarding this store usage.
     */
    public EventStream loadEventStream(String domainSubjectId, int skipEvents, int maxCount) throws IllegalArgumentException, UnoperationalStateException;

}
