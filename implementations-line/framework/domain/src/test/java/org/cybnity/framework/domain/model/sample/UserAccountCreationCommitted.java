package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of event regarding an account creation created.
 * 
 * @author olivier
 *
 */
public class UserAccountCreationCommitted extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;
    public EntityReference creationCommandRef;
    public EntityReference createdAccountRef;

    public UserAccountCreationCommitted() {
	super();
    }

    public UserAccountCreationCommitted(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	UserAccountCreationCommitted instance = new UserAccountCreationCommitted(this.identifiedBy);
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