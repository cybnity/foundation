package org.cybnity.framework.domain.model.sample.readmodel;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Example of event regarding an account creation executed.
 *
 * @author olivier
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
    public Attribute correlationId() {
        return null;
    }

    /**
     * Do nothing.
     *
     * @return Null.
     */
    @Override
    public Attribute type() {
        return null;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        UserAccountDTOChanged instance = new UserAccountDTOChanged(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();
        if (this.creationCommandRef != null)
            instance.createdAccountDTORef = (EntityReference) this.creationCommandRef.immutable();
        if (this.createdAccountDTORef != null)
            instance.createdAccountDTORef = (EntityReference) this.createdAccountDTORef.immutable();
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

    @Override
    public Collection<Attribute> specification() {
        return new ArrayList<>();
    }

    @Override
    public boolean appendSpecification(Attribute specificationCriteria) {
        return false;
    }
}