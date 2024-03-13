package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a stream of domain events in order of occurrence that guarantee the
 * reason for each change to an Aggregate instance will not be lost (as defined
 * by the Aggregates and Event Sourcing pattern A+ES).
 * <p>
 * The append-only nature of Event Streams supports an array of data
 * replications options.
 * <p>
 * An Event stream can be completed by events published (as unit of work) to an
 * event store allowing to persist the state of the Aggregate. The events
 * history can be reloaded from an event stream regarding an Aggregate state.
 * <p>
 * Event streams are considered to be immutable by nature.
 *
 * @author olivier
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
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    private String version;

    /**
     * All events historized as a stream.
     */
    private List<DomainEvent> events = new LinkedList<>();

    public EventStream() {
    }

    public EventStream(@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8") String version, DomainEvent... history) {
        if (history != null && history.length > 0)
            // save optional known origin facts
            events = Arrays.asList(history);
        this.version = version;
    }

    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    public String getVersion() {
        return this.version;
    }

    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    public void setVersion(String version) {
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
            // Get an immutable version of facts maintaining the ordered history
            for (DomainEvent historicalFact : this.events) {
                if (historicalFact != null)
                    origins.add((DomainEvent) historicalFact.immutable());
            }
        }
        return new EventStream(this.version, origins.toArray(new DomainEvent[0]));
    }

}
