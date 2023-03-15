package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Sample regarding an applicative role managed by a domain (e.g that could be
 * assigned to an account), and that can allow permissions (e.g read, modify,
 * delete of informations relative to a domain).
 * 
 * @author olivier
 *
 */
public class ApplicativeRole extends MutableProperty {

    private static final long serialVersionUID = 1L;

    private OffsetDateTime versionedAt;

    /**
     * Example of keys set regarding the multiple attribute defining this role, and
     * that each change need to be versioned/treated as a single atomic fact.
     */
    public enum PropertyAttributeKey {
	RoleName, OwnerRef, VersionedAt;
    }

    /**
     * Default constructor.
     * 
     * @param roleOwner Mandatory owner of this role (e.g user account entity),
     *                  including the entity information.
     * @param name      Mandatory label naming this role.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When impossible creation of immutable
     *                                  version regarding the owner instance.
     */
    public ApplicativeRole(EntityReference roleOwner, String name)
	    throws IllegalArgumentException, ImmutabilityException {
	this(/* Reference identifier equals to the owner of this role */ roleOwner.getEntity(),
		buildPropertyValue(PropertyAttributeKey.RoleName, name), HistoryState.COMMITTED);
	// Save owner original entity reference object (allowing the build of future
	// immutable version of this role)
	this.currentValue().put(PropertyAttributeKey.OwnerRef.name(), roleOwner);
    }

    /**
     * Internal constructor with automatic initialization of an empty value set
     * (prior chain).
     * 
     * @param propertyOwner        Mandatory entity which is owner of this mutable
     *                             property chain.
     * @param propertyCurrentValue Mandatory current version of value(s) regarding
     *                             the property. Support included keys with null
     *                             value.
     * @param status               Optional state of this property version. If null,
     *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
     *                             is defined as default state.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  cant' be cloned regarding immutable entity
     *                                  parameter.
     */
    private ApplicativeRole(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
	    throws IllegalArgumentException {
	super(propertyOwner, propertyCurrentValue, status);
	this.versionedAt = OffsetDateTime.now();
	// Save the current (last) version date
	this.currentValue().put(PropertyAttributeKey.VersionedAt.name(), this.versionedAt);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	ApplicativeRole copy = new ApplicativeRole(
		(EntityReference) this.currentValue().get(PropertyAttributeKey.OwnerRef.name()),
		(String) this.currentValue().get(PropertyAttributeKey.RoleName.name()));
	// Complete with additional attributes of this complex property
	copy.changedAt = this.occurredAt();
	copy.historyStatus = this.historyStatus();
	copy.updateChangesHistory(this.changesHistory());
	return copy;
    }

    /**
     * Build a definition of property based on property name and value.
     * 
     * @param key   Mandatory key name of the property.
     * @param value Value of the key.
     * @return A definition of the property.
     */
    static private HashMap<String, Object> buildPropertyValue(PropertyAttributeKey key, Object value) {
	HashMap<String, Object> val = new HashMap<>();
	val.put(key.name(), value);
	return val;
    }

    /**
     * Get the history chain of previous versions of this property including
     * previous changed values states.
     * 
     * @return A changes history. Empty list by default.
     */
    public Set<ApplicativeRole> changesHistory() {
	// Read previous changes history (not including the current version)
	LinkedHashSet<ApplicativeRole> history = new LinkedHashSet<>();
	for (MutableProperty previousChangedProperty : this.prior) {
	    history.add((ApplicativeRole) previousChangedProperty);
	}
	return history;
    }

    /**
     * Update the history of this role.
     * 
     * @param roles To set as new version of this role history. Ignore if null or
     *              empty.
     */
    public void updateChangesHistory(Set<ApplicativeRole> roles) {
	if (roles != null && !roles.isEmpty()) {
	    // Update the story at end of previous versions
	    LinkedHashSet<MutableProperty> history = new LinkedHashSet<>();
	    for (ApplicativeRole aRole : roles) {
		history.add(aRole);
	    }
	    // Replace current history
	    this.prior = history;
	}
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
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
     * Get the current value of this complex property.
     * 
     * @return A set of valued attributes.
     */
    public HashMap<String, Object> currentValue() {
	return this.value;
    }

    /**
     * Get the logical name of this role.
     * 
     * @return A label naming this role.
     */
    public String getName() {
	return (String) this.currentValue().get(PropertyAttributeKey.RoleName.name());
    }

    /**
     * Get the entity reference which is owner of this role.
     * 
     * @return An owner reference.
     */
    public EntityReference ownerReference() {
	return (EntityReference) this.currentValue().get(PropertyAttributeKey.OwnerRef.name());
    }

    /**
     * Get the time when this role was versioned.
     * 
     * @return A date of this role creation.
     */
    public OffsetDateTime versionedAt() {
	return (OffsetDateTime) this.currentValue().get(PropertyAttributeKey.VersionedAt.name());
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	boolean isEquals = false;
	if (obj instanceof ApplicativeRole) {
	    try {
		ApplicativeRole compared = (ApplicativeRole) obj;
		// Check if same role name
		if (compared.getName().equals(this.getName())) {
		    // Check if same status
		    if (compared.historyStatus() == this.historyStatus()) {
			// Check if same role versioned
			if (compared.versionedAt().equals(this.versionedAt)) {
			    isEquals = true;
			}
		    }
		}
	    } catch (Exception e) {
		// any missing information generating null pointer exception or problem of
		// information read
	    }
	}
	return isEquals;
    }
}
