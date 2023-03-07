package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Example of event regarding an applicative role assigned to a user account.
 * 
 * @author olivier
 *
 */
public class UserAccountApplicativeRoleAssigned extends DomainEvent {

    private static final long serialVersionUID = 876288332792604981L;
    public EntityReference changeCommandRef;
    public EntityReference changedAccountRef;

    public UserAccountApplicativeRoleAssigned() {
	super();
    }

    public UserAccountApplicativeRoleAssigned(Entity identity) {
	super(identity);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	UserAccountApplicativeRoleAssigned instance = new UserAccountApplicativeRoleAssigned(this.getIdentifiedBy());
	instance.occuredOn = this.occurredAt();
	if (this.changeCommandRef != null)
	    instance.changeCommandRef = (EntityReference) this.changeCommandRef.immutable();
	if (this.changedAccountRef != null)
	    instance.changedAccountRef = (EntityReference) this.changedAccountRef.immutable();
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