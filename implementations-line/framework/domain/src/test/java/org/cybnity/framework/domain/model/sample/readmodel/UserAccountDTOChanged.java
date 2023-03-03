package org.cybnity.framework.domain.model.sample.readmodel;

import java.io.Serializable;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of event regarding an account creation executed.
 * 
 * @author olivier
 *
 */
public class UserAccountDTOChanged extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;
    public EntityReference creationCommandRef;
    public EntityReference createdAccountDTORef;

    public UserAccountDTOChanged() {
	super();
    }

    public UserAccountDTOChanged(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	UserAccountDTOChanged instance = new UserAccountDTOChanged(this.getIdentifiedBy());
	instance.occuredOn = this.occurredAt();
	if (this.creationCommandRef != null)
	    instance.createdAccountDTORef = (EntityReference) this.creationCommandRef.immutable();
	if (this.createdAccountDTORef != null)
	    instance.createdAccountDTORef = (EntityReference) this.createdAccountDTORef.immutable();
	return instance;
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }

}