package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a stream of domain events in order of occurrence that guarantee the
 * reason for each change to an Aggregate instance will not be lost (as defined
 * by the Aggregates and Event Sourcing pattern A+ES).
 * 
 * The append-only nature of Event Streams supports an array of data
 * replications options.
 * 
 * An Event stream can be completed by events published (as unit of work) to an
 * event store allowing to persist the state of the Aggregate. The events
 * history can be reloaded from an event stream regarding an Aggregate state.
 * 
 * Event streams are considered to be immutable by nature.
 * 
 * @author olivier
 *
 */
public class EventStream implements Unmodifiable, Serializable {

    /**
     * Class specification version.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(EventStream.class).hashCode();

    /**
     * Version of the event stream.
     */
    private int version;

    /**
     * All events in the stream.
     */
    private List<DomainEvent> events = new LinkedList<>();

    public EventStream() {
    }

    public EventStream(int version, DomainEvent... history) {
	if (history != null && history.length > 0)
	    // save optional known origin facts
	    events = Arrays.asList(history);
	this.version = version;
    }

    public int getVersion() {
	return this.version;
    }

    public void setVersion(int version) {
	this.version = version;
    }

    public List<DomainEvent> getEvents() {
	return this.events;
    }

    public void setEvents(List<DomainEvent> events) {
	this.events = events;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	List<DomainEvent> origins = new ArrayList<>();
	if (this.events != null) {
	    // Get an immutable version of facts
	    for (DomainEvent historicalFact : this.events) {
		if (historicalFact != null)
		    origins.add((DomainEvent) historicalFact.immutable());
	    }
	}
	return new EventStream(this.version, origins.toArray(new DomainEvent[origins.size()]));
    }

}
