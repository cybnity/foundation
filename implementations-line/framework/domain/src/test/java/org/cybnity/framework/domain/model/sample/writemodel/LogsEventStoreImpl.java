package org.cybnity.framework.domain.model.sample.writemodel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.domain.model.EventStore;
import org.cybnity.framework.domain.model.EventStream;
import org.cybnity.framework.domain.model.sample.EventStoreRecordCommitted;
import org.cybnity.framework.domain.model.sample.readmodel.EventStored;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of simple in-memory store of events.
 * 
 * @author olivier
 *
 */
public class LogsEventStoreImpl extends EventStore {

    /**
     * Registries per type of stored event (key=class type, value=history of events)
     */
    private ConcurrentHashMap<String, LinkedList<EventStored>> registries = new ConcurrentHashMap<String, LinkedList<EventStored>>();

    /**
     * Delegated capability regarding promotion of stored events changes.
     */
    private DomainEventPublisher promotionManager;

    private LogsEventStoreImpl() {
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
	return new LogsEventStoreImpl();
    }

    @Override
    public void append(DomainEvent event) throws IllegalArgumentException, ImmutabilityException {
	// Serialize the event to store into the storage system (generally according to
	// a serializer supported by the persistence system as JSON, table structure's
	// fiedl...)
	EventStored storedEvent = new EventStored(event);
	// Find existing history regarding the event type, or initialize empty dataset
	// for the type
	LinkedList<EventStored> eventTypeDataset = this.registries.getOrDefault(storedEvent.getTypeName(),
		new LinkedList<EventStored>());
	// Add the event to the end of history column regarding all the same event types
	eventTypeDataset.add(storedEvent);
	// Save in registry
	registries.put(event.getClass().getName(), eventTypeDataset);

	// Build event child based on the created account (parent of immutable story)
	CommonChildFactImpl persistedEvent = new CommonChildFactImpl(storedEvent.getIdentifiedBy(),
		new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
			/* identifier as performed transaction number */ UUID.randomUUID().toString()));
	EventStoreRecordCommitted committed = new EventStoreRecordCommitted(persistedEvent.parent());
	committed.originCommandRef = event.reference();
	committed.storedEvent = storedEvent.reference();

	// Notify listeners of this store
	this.promotionManager.publish(committed);
    }

    @Override
    public DomainEvent findEventFrom(Identifier uid) {
	if (uid != null && uid.value() != null) {
	    for (Map.Entry<String, LinkedList<EventStored>> storeColumn : this.registries.entrySet()) {
		LinkedList<EventStored> storedEventColumn = storeColumn.getValue();
		// Search last event historized with this identifier
		Iterator<EventStored> it = storedEventColumn.descendingIterator();
		while (it.hasNext()) {
		    EventStored historizedEvent = it.next();
		    // Compare if equals identifier
		    if (historizedEvent.getEventId() == uid.value().hashCode()) {
			return historizedEvent;
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