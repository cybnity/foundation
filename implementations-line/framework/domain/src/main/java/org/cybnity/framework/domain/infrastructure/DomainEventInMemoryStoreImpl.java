package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.model.EventRecord;
import org.cybnity.framework.domain.model.EventStore;
import org.cybnity.framework.domain.model.EventStream;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store of events.
 *
 * @author olivier
 */
public class DomainEventInMemoryStoreImpl extends EventStore {

    /**
     * Registries per type of stored event (key=changed domain object id, value=history of record events)
     */
    private final ConcurrentHashMap<String, LinkedList<EventRecord>> registries = new ConcurrentHashMap<>();

    /**
     * Default constructor.
     */
    protected DomainEventInMemoryStoreImpl() {
        super();
    }

    /**
     * Get an instance of the event store, ready for operating (e.g configured).
     *
     * @return An instance ensuring the persistence of events.
     */
    public static EventStore instance() {
        return new DomainEventInMemoryStoreImpl();
    }

    /**
     * Get container of record implemented in-memory.
     *
     * @return Store's container instance.
     */
    protected ConcurrentHashMap<String, LinkedList<EventRecord>> registries() {
        return this.registries;
    }

    @Override
    public void freeResources() {
    }

    @Override
    public void appendToStream(Identifier domainSubjectId, List<DomainEvent> changes) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException {
        if (domainSubjectId == null) throw new IllegalArgumentException("domainSubjectId parameter is required!");
        if (changes == null) throw new IllegalArgumentException("changes parameter is required!");
        if (changes.isEmpty()) return; // noting to change on domain event

        // --- PREPARE A CHANGE RECORD FOR EACH CHANGE EVENT RELATIVE TO THE SUBJECT IDENTIFIED ---
        LinkedList<EventRecord> changesEligibleToHistoryStorage = new LinkedList<>();

        @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
        EventRecord item;
        for (DomainEvent changeEvt : changes) {
            // Serializable version of record item to save into the storage system (generally according to
            // a serializer supported by the persistence system as JSON, table structure's field...)
            item = new EventRecord(changeEvt); // type version auto-based on change class type version
            changesEligibleToHistoryStorage.add(item);
        }

        // Find existing history regarding the origin domain object identified, or initialize empty dataset
        // for the type (expectedVersion is not based on domain event but is based on event record container version)
        @Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
        LinkedList<EventRecord> eventTypeDataset = registries
                .getOrDefault(domainSubjectId.value().toString(), new LinkedList<>());
        // Add the event records to the end of history column regarding all the same event record type version
        eventTypeDataset.addAll(changesEligibleToHistoryStorage);

        // Save all domain event changes into registry stream regarding domain event uid
        registries.put(domainSubjectId.value().toString(), eventTypeDataset);

        // Promote to subscribers (e.g read-model repositories) the change events that have been stored
        for (DomainEvent changeEvt : changes) {
            subscribersManager().publish(changeEvt);
        }
    }

    @Override
    public EventStream loadEventStream(String domainSubjectId) throws IllegalArgumentException, UnoperationalStateException {
        if (domainSubjectId == null || domainSubjectId.isEmpty())
            throw new IllegalArgumentException("domainSubjectId parameter is required!");
        // Search event stream according to all event record versions supported (all columns per event record class version)
        LinkedList<DomainEvent> foundEventDomainHistory = new LinkedList<>();
        EventStream domainObjEventsHistory = new EventStream();
        for (Map.Entry<String, LinkedList<EventRecord>> storeColumn : registries.entrySet()) {
            // For any stream version supported by the registry regarding a domain object
            if (domainSubjectId.equals(storeColumn.getKey())) { // Detected the identifier of domain object owner of event records column
                // Read the existing recorded events relative to domain events
                LinkedList<EventRecord> storedEventRecordsColumn = storeColumn.getValue();
                // Select descending order of historized event records regarding an origin domain object's identifier
                Iterator<EventRecord> unfilteredVersions = storedEventRecordsColumn.descendingIterator();
                while (unfilteredVersions.hasNext()) {
                    // Read recorded event
                    EventRecord historizedEvent = unfilteredVersions.next();
                    // Compare if equals the record event origin domain event have equals identifier
                    // It's a domain event without consideration of event record container's version used by the storage system
                    foundEventDomainHistory.add((DomainEvent) historizedEvent.body());

                    // Synchronize the event stream version based on the type of record type version hash (aligned with the domain object class serial UID)
                    domainObjEventsHistory.setVersion(historizedEvent.factTypeVersion().hash());
                }
            }
        }
        if (!foundEventDomainHistory.isEmpty()) {
            domainObjEventsHistory.setEvents(foundEventDomainHistory);
            return domainObjEventsHistory;
        }
        return null;
    }

    @Override
    public EventStream loadEventStreamAfterVersion(String domainSubjectId, String snapshotExpectedVersion) throws IllegalArgumentException, UnoperationalStateException {
        if (domainSubjectId == null || domainSubjectId.isEmpty())
            throw new IllegalArgumentException("domainSubjectId parameter is required!");
        if (snapshotExpectedVersion == null || snapshotExpectedVersion.isEmpty())
            throw new IllegalArgumentException("snapshotExpectedVersion parameter is required!");
        // Search latest event stream supporting event type after the snapshot version requested
        LinkedList<DomainEvent> foundEventDomainHistory = new LinkedList<>();
        EventStream domainObjEventsHistory = new EventStream();
        for (Map.Entry<String, LinkedList<EventRecord>> storeColumn : registries.entrySet()) {
            // For any stream version supported by the registry regarding a domain object
            if (domainSubjectId.equals(storeColumn.getKey())) { // Detected the id of domain object owner of event records column
                // Read the existing recorded events relative to domain events
                LinkedList<EventRecord> storedEventRecordsColumn = storeColumn.getValue();
                // Select historized event records regarding an origin domain object's identifier
                // Without any change on recorded order
                for (EventRecord historizedEvent : storedEventRecordsColumn) {
                    // Read recorded event
                    // Compare if equals the record event origin domain event have equals identifier
                    // It's a domain event without consideration of event record container's version used by the storage system
                    // But check that object version if equals or superior to the filtered snapshot version parameter
                    if (historizedEvent.factTypeVersion().hash().equals(snapshotExpectedVersion) || (historizedEvent.factTypeVersion().hash().compareTo(snapshotExpectedVersion) > 0)) {
                        foundEventDomainHistory.add((DomainEvent) historizedEvent.body());
                        // Synchronize the event stream version based on the type of record type version hash (aligned with the domain object class serial UID)
                        domainObjEventsHistory.setVersion(historizedEvent.factTypeVersion().hash());
                    }
                }
            }
        }
        if (!foundEventDomainHistory.isEmpty()) {
            domainObjEventsHistory.setEvents(foundEventDomainHistory);
            return domainObjEventsHistory;
        }
        return null;
    }

    @Override
    public EventStream loadEventStream(String domainSubjectId, int skipEvents, int maxCount) throws IllegalArgumentException, UnoperationalStateException {
        throw new IllegalArgumentException("to implement!");
    }
}
