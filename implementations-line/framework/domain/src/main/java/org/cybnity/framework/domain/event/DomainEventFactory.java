package org.cybnity.framework.domain.event;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.EntityReference;

import java.util.Collection;

/**
 * Factory of domain event supported by domain Anti-Corruption Layer API.
 */
public class DomainEventFactory {

    /**
     * Factory of a type of domain event supported by the domain Anti-Corruption Layer API.
     *
     * @param type                   Mandatory type of domain event to instantiate.
     * @param identifiedBy           Optional event identity that shall identify the concrete event instance to create.
     * @param definition             Collection of attributes defining the event.
     * @param priorCommandRef        Optional original event reference that was previous source of this event publication.
     * @param changedModelElementRef Optional Identify the element of the domain model which was subject of domain event.
     * @return Instance of concrete event including all the attributes and standard additional elements.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public DomainEvent create(String type, DomainEntity identifiedBy, Collection<Attribute> definition, EntityReference priorCommandRef, EntityReference changedModelElementRef) throws IllegalArgumentException {
        return ConcreteDomainChangeEvent.create(type, identifiedBy, definition, priorCommandRef, changedModelElementRef);
    }

}
