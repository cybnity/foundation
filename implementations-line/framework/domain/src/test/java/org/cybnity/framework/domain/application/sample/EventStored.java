package org.cybnity.framework.domain.application.sample;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of destructured event manageable by a store (e.g as history of facts
 * serialized and tracked).
 * 
 * @author olivier
 *
 */
public class EventStored extends DomainEvent {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = 1L;

    private Serializable eventBody;
    private int eventId;
    private String occuredOn;
    private String typeName;
    private String versionStoredAt;

    public EventStored(DomainEvent event) throws IllegalArgumentException {
	if (event == null) {
	    throw new IllegalArgumentException("Event parameter is required!");
	}
	Identifier id = event.identified();
	if (id == null || id.value() == null) {
	    throw new IllegalArgumentException("Only identifiable event is eligible to store. Identifier is required!");
	}
	this.eventBody = event;
	this.eventId = event.identified().value().hashCode();
	this.occuredOn = event.occurredAt().toString();
	this.typeName = event.getClass().getName();
	this.versionStoredAt = OffsetDateTime.now().toString();
    }

    public Serializable getEventBody() {
	return eventBody;
    }

    public int getEventId() {
	return eventId;
    }

    public String getOccuredOn() {
	return occuredOn;
    }

    public String getTypeName() {
	return typeName;
    }

    public String getVersionStoredAt() {
	return versionStoredAt;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	try {
	    return (EventStored) this.clone();
	} catch (Exception e) {
	    throw new ImmutabilityException(e);
	}
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }

}
