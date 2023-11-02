package org.cybnity.framework.domain.model.sample.writemodel;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;

/**
 * Example of event regarding an account change executed.
 *
 * @author olivier
 */
public class UserAccountChanged extends DomainEvent {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(UserAccountChanged.class).hashCode();

    public EntityReference creationCommandRef;
    public EntityReference createdAccountRef;

    public UserAccountChanged() {
        super();
    }

    public UserAccountChanged(Entity identity) {
        super(identity);
    }

    @Override
    protected Attribute correlationId() {
        return null;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        UserAccountChanged instance = new UserAccountChanged(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();
        if (this.creationCommandRef != null)
            instance.createdAccountRef = (EntityReference) this.creationCommandRef.immutable();
        if (this.createdAccountRef != null)
            instance.createdAccountRef = (EntityReference) this.createdAccountRef.immutable();
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