package org.cybnity.framework.domain.model.sample.writemodel;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Example of specific definition regarding an organization, that can be changed
 * (e.g company name change during its life), and which need to historized in an
 * immutable way the history of changes (version of this information).
 * 
 * Sample usable to evaluate the
 * {@link org.cybnity.framework.immutable.MutableProperty} pattern.
 * 
 * @author olivier
 *
 */
public class OrganizationDescriptor extends MutableProperty {

    private static final long serialVersionUID = 1L;
    private OffsetDateTime versionedAt;

    /**
     * Example of keys set regarding the multiple attribute defining this complex
     * organization, and that each change need to be versioned/treated as a single
     * atomic fact.
     */
    public enum PropertyAttributeKey {
	Name, LocationCity, LocationCountry;
    }

    public OrganizationDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
	    HistoryState status) throws IllegalArgumentException {
	super(propertyOwner, propertyCurrentValue, status);
	this.versionedAt = OffsetDateTime.now();
    }

    public OrganizationDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
	    HistoryState status, OrganizationDescriptor... prior) throws IllegalArgumentException {
	super(propertyOwner, propertyCurrentValue, status, prior);
	this.versionedAt = OffsetDateTime.now();
    }

    /**
     * Get the name of the organization.
     * 
     * @return A label or null.
     */
    public String getOrganizationName() {
	return (String) this.currentValue().getOrDefault(PropertyAttributeKey.Name.name(), null);
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.versionedAt;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	OrganizationDescriptor copy = new OrganizationDescriptor(this.owner(), this.currentValue(),
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
	return (Entity) this.entity.immutable();
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
