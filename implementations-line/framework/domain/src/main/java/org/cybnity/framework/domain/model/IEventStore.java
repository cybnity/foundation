package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IEventStore {

    /**
     * Add an event into the store.
     * 
     * @param event Mandatory event to store.
     * @throws IllegalArgumentException When event to store is not compatible to be
     *                                  stored (e.g missing mandatory content into
     *                                  the event to store).
     * @throws ImmutabilityException    When problem of immutable version of stored
     *                                  event is occurred.
     */
    public void append(DomainEvent event) throws IllegalArgumentException, ImmutabilityException;

    /**
     * Search in store an event logged.
     * 
     * @param uid Mandatory identifier of the event to find.
     * @return Found event or null.
     */
    public DomainEvent findEventFrom(Identifier uid);

    /**
     * Load all the events regarding a stream.
     * 
     * @param id Mandatory identifier of the stream to load.
     * @return Found event history or null.
     * @throws IllegalArgumentException When missing mandatory parameter.
     */
    public EventStream loadEventStream(Identifier id) throws IllegalArgumentException;

    /**
     * Load a subset of events regarding a stream.
     * 
     * @param id         Mandatory identifier of the event stream subset to load.
     * @param skipEvents How many event versions shall be skip before to load event
     *                   versions.
     * @param maxCount   How many event instance shall be taken regarding the
     *                   history flow.
     * @return A found subset or null.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public EventStream loadEventStream(Identifier id, int skipEvents, int maxCount) throws IllegalArgumentException;

}
