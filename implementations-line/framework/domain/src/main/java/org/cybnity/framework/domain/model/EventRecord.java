package org.cybnity.framework.domain.model;

import java.io.Serializable;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.persistence.FactRecord;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a recorded fact relative to an event which is manageable by a store
 * (e.g as history of facts serialized and tracked).
 * 
 * Each recorded fact include the original version of event tracked, and
 * extracted informations allowing to store/retrieve it (e.g during specific
 * steps of a process, for hydratation of a messaging system).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class EventRecord extends FactRecord {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor of a fact record based on a domain event.
     * 
     * @param event Mandatory event that is subject of recording.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When problem of read regarding immutable
     *                                  contents sourced from the event.
     */
    public EventRecord(DomainEvent event) throws IllegalArgumentException, ImmutabilityException {
	super(event);
	if (event == null) {
	    throw new IllegalArgumentException("Event parameter is required!");
	}
	Identifier id = event.identified();
	if (id == null || id.value() == null) {
	    throw new IllegalArgumentException("Only identifiable event is eligible to store. Identifier is required!");
	}
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	try {
	    return (EventRecord) this.clone();
	} catch (Exception e) {
	    throw new ImmutabilityException(e);
	}
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