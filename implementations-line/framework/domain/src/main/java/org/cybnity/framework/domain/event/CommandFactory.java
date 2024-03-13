package org.cybnity.framework.domain.event;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.EntityReference;

import java.util.Collection;

/**
 * Factory of command event supported by a domain bounded context api.
 */
public class CommandFactory {

    /**
     * Factory of a type of command supported by a domain Anti-Corruption Layer API.
     *
     * @param type                   Mandatory type of command event to instantiate.
     * @param identifiedBy           Optional command identity that shall identify the concrete command instance to create.
     * @param definition             Collection of attributes defining the command.
     * @param priorCommandRef        Optional original command reference that was previous source of this command
     *                               publication
     * @param changedModelElementRef Optional Identify the element of the domain model which was subject of command.
     * @return Instance of concrete event including all the attributes and standard additional elements.
     * @throws IllegalArgumentException When any mandatory parameter is missing;
     */
    static public Command create(String type, DomainEntity identifiedBy, Collection<Attribute> definition, EntityReference priorCommandRef, EntityReference changedModelElementRef) throws IllegalArgumentException {
        if (type == null || type.isEmpty()) throw new IllegalArgumentException("Type parameter is required!");
        ConcreteCommandEvent command = new ConcreteCommandEvent(identifiedBy, type);
        if (definition != null) {
            // Add command event attributes
            for (Attribute at : definition) {
                command.appendSpecification(at);
            }
        }
        if (priorCommandRef != null) {
            command.setPriorCommandRef(priorCommandRef);
        }
        if (changedModelElementRef != null) {
            command.setChangedModelElementRef(changedModelElementRef);
        }
        return command;
    }

}
