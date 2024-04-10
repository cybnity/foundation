package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;

/**
 * Definition regarding a tenant, that can be changed
 * (e.g company or owner logical name changed during its life), and which need to historized in an
 * immutable way the history of changes (version of this information).
 *
 * @author olivier
 */
public class TenantDescriptor extends MutableProperty {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(TenantDescriptor.class).hashCode();

    private OffsetDateTime versionedAt;

    /**
     * Keys set regarding the multiple attribute defining this complex
     * descriptor, and that each change need to be versioned/treated as a single
     * atomic fact.
     */
    public enum PropertyAttributeKey {
        LABEL;
    }

    public TenantDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
                            HistoryState status) throws IllegalArgumentException {
        super(propertyOwner, propertyCurrentValue, status);
        this.versionedAt = OffsetDateTime.now();
    }

    public TenantDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
                            HistoryState status, TenantDescriptor... prior) throws IllegalArgumentException {
        super(propertyOwner, propertyCurrentValue, status, prior);
        this.versionedAt = OffsetDateTime.now();
    }

    /**
     * Get the logical name of the tenant.
     *
     * @return A label or null.
     */
    public String getLabel() {
        return (String) this.currentValue().getOrDefault(PropertyAttributeKey.LABEL.name(), null);
    }

    @Override
    public OffsetDateTime occurredAt() {
        return this.versionedAt;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        TenantDescriptor copy = new TenantDescriptor(this.owner(), this.currentValue(),
                this.historyStatus());
        // Complete with additional attributes of this complex property
        copy.versionedAt = this.versionedAt;
        copy.changedAt = this.occurredAt();
        copy.updateChangesHistory(this.changesHistory());
        return copy;
    }

    /**
     * Get the current value of this complex property.
     *
     * @return A set of valued attributes.
     */
    public HashMap<String, Object> currentValue() {
        return this.value;
    }

    /**
     * Who is the owner of this property
     *
     * @return The owner
     * @throws ImmutabilityException If impossible creation of immutable version of
     *                               instance
     */
    public Entity owner() throws ImmutabilityException {
        return (Entity) this.owner.immutable();
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return String.valueOf(serialVersionUID);
    }
}
