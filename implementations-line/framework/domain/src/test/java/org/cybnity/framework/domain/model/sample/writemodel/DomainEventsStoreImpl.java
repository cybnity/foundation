package org.cybnity.framework.domain.model.sample.writemodel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.domain.model.EventRecord;
import org.cybnity.framework.domain.model.EventStore;
import org.cybnity.framework.domain.model.EventStream;
import org.cybnity.framework.domain.model.sample.EventStoreRecordCommitted;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of simple in-memory store of events.
 * 
 * @author olivier
 *
 */
public class DomainEventsStoreImpl extends EventStore {

    /**
     * Registries per type of stored event (key=class type, value=history of events)
     */
    private ConcurrentHashMap<String, LinkedList<EventRecord>> registries = new ConcurrentHashMap<String, LinkedList<EventRecord>>();

    /**
     * Delegated capability regarding promotion of stored events changes.
     */
    private DomainEventPublisher promotionManager;

    private DomainEventsStoreImpl() {
	super();
	// Initialize a delegate for promotion of events changes (e.g to read model's
	// repository)
	this.promotionManager = DomainEventPublisher.instance();
    }

    /**
     * Get an instance of the event store, ready for operating (e.g configured).
     * 
     * @return An instance ensuring the persistence of events.
     */
    public static EventStore instance() {
	return new DomainEventsStoreImpl();
    }

    @Override
    public void append(DomainEvent event) throws IllegalArgumentException, ImmutabilityException {
	// Serialize the event to store into the storage system (generally according to
	// a serializer supported by the persistence system as JSON, table structure's
	// fiedl...)
	EventRecord storedEvent = new EventRecord(event);
	// Find existing history regarding the event type, or initialize empty dataset
	// for the type
	LinkedList<EventRecord> eventTypeDataset = this.registries
		.getOrDefault(storedEvent.factTypeVersion().factType().name(), new LinkedList<EventRecord>());
	// Add the event to the end of history column regarding all the same event types
	eventTypeDataset.add(storedEvent);
	// Save in registry regarding fact type version

	registries.put(event.getClass().getName(), eventTypeDataset);

	// Build event child based on the created account (parent of immutable story)
	CommonChildFactImpl persistedEvent = new CommonChildFactImpl(event.getIdentifiedBy(),
		new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
			/* identifier as performed transaction number */ UUID.randomUUID().toString()));
	EventStoreRecordCommitted committed = new EventStoreRecordCommitted(persistedEvent.parent());
	committed.originCommandRef = event.reference();
	committed.storedEvent = event.reference();

	// Notify listeners of this store
	this.promotionManager.publish(committed);
    }

    @Override
    public DomainEvent findEventFrom(Identifier uid) {
	if (uid != null && uid.value() != null) {
	    for (Map.Entry<String, LinkedList<EventRecord>> storeColumn : this.registries.entrySet()) {
		LinkedList<EventRecord> storedEventColumn = storeColumn.getValue();
		// Search last event historized with this identifier
		Iterator<EventRecord> it = storedEventColumn.descendingIterator();
		while (it.hasNext()) {
		    EventRecord historizedEvent = it.next();
		    // Compare if equals identifier
		    if (historizedEvent.factId() == uid.value().hashCode()) {
			return (DomainEvent) historizedEvent.body();
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public <T> void subscribe(DomainEventSubscriber<T> aSubscriber) {
	if (aSubscriber != null)
	    // Add listener interested by stored events
	    this.promotionManager.subscribe(aSubscriber);
    }

    @Override
    public <T> void remove(DomainEventSubscriber<T> aSubscriber) {
	if (aSubscriber != null)
	    this.promotionManager.remove(aSubscriber);
    }

    @Override
    public EventStream loadEventStream(Identifier id) throws IllegalArgumentException {
	return null;
    }

    @Override
    public EventStream loadEventStream(Identifier id, int skipEvents, int maxCount) throws IllegalArgumentException {
	return null;
    }
}
