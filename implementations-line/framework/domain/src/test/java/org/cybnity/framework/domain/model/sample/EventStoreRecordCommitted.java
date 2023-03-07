package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Example of event regarding a store event finalized with success.
 * 
 * @author olivier
 *
 */
public class EventStoreRecordCommitted extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;
    public EntityReference originCommandRef;
    public EntityReference storedEvent;

    public EventStoreRecordCommitted() {
	super();
    }

    public EventStoreRecordCommitted(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	EventStoreRecordCommitted instance = new EventStoreRecordCommitted(this.getIdentifiedBy());
	instance.occuredOn = this.occurredAt();
	if (this.originCommandRef != null)
	    instance.originCommandRef = (EntityReference) this.originCommandRef.immutable();
	if (this.storedEvent != null)
	    instance.storedEvent = (EntityReference) this.storedEvent.immutable();
	return instance;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }
}