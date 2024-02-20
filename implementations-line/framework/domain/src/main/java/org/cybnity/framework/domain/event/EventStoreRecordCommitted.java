package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;

/**
 * Record of event stored with success into a store.
 *
 * @author olivier
 */
@JsonTypeName("EventStoreRecordCommitted")
public class EventStoreRecordCommitted extends ConcreteDomainChangeEvent {

    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(EventStoreRecordCommitted.class).hashCode();

    /**
     * Supported attributes set by a record commit.
     */
    public enum SpecificationAttribute implements IAttribute {
        ;
    }

    public EventStoreRecordCommitted() {
        super();
    }

    public EventStoreRecordCommitted(Entity identity) {
        super(identity);
    }

    public EventStoreRecordCommitted(Enum<?> eventType) {
        super(eventType);
    }

    public EventStoreRecordCommitted(Entity identifiedBy, Enum<?> eventType) {
        super(identifiedBy, eventType);
    }

    public EventStoreRecordCommitted(Entity identifiedBy, String eventType) {
        super(identifiedBy, eventType);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        EventStoreRecordCommitted instance = new EventStoreRecordCommitted(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();

        // Add immutable version of each additional attributes hosted by this event
        EntityReference cmdRef = this.changeCommandReference();
        if (cmdRef != null)
            instance.setChangeCommandRef(cmdRef);
        EntityReference subjectRef = this.changedModelElementReference();
        if (subjectRef != null)
            instance.setChangedModelElementRef(subjectRef);
        if (this.specification != null && !this.specification.isEmpty()) {
            instance.specification = this.specification();
        }
        return instance;
    }

}