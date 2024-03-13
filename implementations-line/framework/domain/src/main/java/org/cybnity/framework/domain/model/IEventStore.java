package org.cybnity.framework.domain.model;

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
     * Add an event into the store stream and commit the list changes.
     *
     * @param domainEventId Mandatory identifier of the root domain event which is subject of the changes.
     * @param changes       Mandatory ordered new events to commit at end of the stream managed by the store.
     * @throws IllegalArgumentException When event to store is not compatible to be
     *                                  stored (e.g missing mandatory content into
     *                                  the event to store).
     * @throws ImmutabilityException    When problem of immutable version of stored
     *                                  event is occurred.
     */
    public void appendToStream(Identifier domainEventId, List<DomainEvent> changes) throws IllegalArgumentException, ImmutabilityException;

    /**
     * Load all the events regarding a stream type.
     *
     * @param domainEventId Mandatory identifier of the stream to load.
     * @return A found stream in descending ordering (last event is first of list) or null.
     * @throws IllegalArgumentException When missing mandatory parameter.
     */
    public EventStream loadEventStream(String domainEventId) throws IllegalArgumentException;

    /**
     * Load all the events since a snapshot version that was taken.
     *
     * @param domainEventId   Mandatory identifier of the stream to load.
     * @param snapshotVersion Mandatory identifier of the snapshot stored event.
     * @return A found stream or null.
     * @throws IllegalArgumentException When missing mandatory parameter.
     */
    public EventStream loadEventStreamAfterVersion(String domainEventId, String snapshotVersion) throws IllegalArgumentException;

    /**
     * Load a subset of events (as a range) regarding a stream.
     *
     * @param domainEventId Mandatory identifier of the event stream subset to load.
     * @param skipEvents    How many event items shall be skipped before to load stream.
     * @param maxCount      How many event instance shall be taken regarding the
     *                      history flow.
     * @return A found stream in descending ordering (last event is first of list) or null.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public EventStream loadEventStream(String domainEventId, int skipEvents, int maxCount) throws IllegalArgumentException;

}
