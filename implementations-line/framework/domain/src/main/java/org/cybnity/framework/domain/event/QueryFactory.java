package org.cybnity.framework.domain.event;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.EntityReference;

import java.util.Collection;

/**
 * Factory of query command supported by a domain bounded context api.
 */
public class QueryFactory {

    /**
     * Factory of a type of query supported by a domain Anti-Corruption Layer API.
     *
     * @param type            Mandatory type of query command to instantiate.
     * @param identifiedBy    Optional query identity that shall identify the concrete query instance to create.
     * @param definition      Collection of attributes defining the query.
     * @param priorCommandRef Optional original event reference that was previous source of this query publication.
     * @return Instance of concrete event including all the attributes and standard additional elements.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public Command create(String type, DomainEntity identifiedBy, Collection<Attribute> definition, EntityReference priorCommandRef) throws IllegalArgumentException {
        return ConcreteQueryEvent.create(type, identifiedBy, definition, priorCommandRef);
    }
}
