package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;

import org.cybnity.framework.domain.model.DomainEvent;
import org.cybnity.framework.immutable.Entity;

/**
 * Example of event regarding an account creation created.
 * 
 * @author olivier
 *
 */
public class UserAccountCreationCommitted extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;

    public UserAccountCreationCommitted() {
	super();
    }

    public UserAccountCreationCommitted(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws CloneNotSupportedException {
	UserAccountCreationCommitted instance = new UserAccountCreationCommitted(this.identifiedBy);
	instance.occuredOn = this.occurredAt();
	return instance;
    }

    @Override
    public Long versionUID() {
	return Long.valueOf(serialVersionUID);
    }
}