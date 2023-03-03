package org.cybnity.framework.domain.model.sample.writemodel;

import java.io.Serializable;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of event regarding an account change executed.
 * 
 * @author olivier
 *
 */
public class UserAccountChanged extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;
    public EntityReference creationCommandRef;
    public EntityReference createdAccountRef;

    public UserAccountChanged() {
	super();
    }

    public UserAccountChanged(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	UserAccountChanged instance = new UserAccountChanged(this.getIdentifiedBy());
	instance.occuredOn = this.occurredAt();
	if (this.creationCommandRef != null)
	    instance.createdAccountRef = (EntityReference) this.creationCommandRef.immutable();
	if (this.createdAccountRef != null)
	    instance.createdAccountRef = (EntityReference) this.createdAccountRef.immutable();
	return instance;
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }

}