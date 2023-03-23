package org.cybnity.framework.domain.model;

import java.io.Serializable;
import java.util.HashMap;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a state of activity (e.g active or not active) regarding a subject.
 * Can be used as an activity tag for any type of subject.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_3")
public class ActivityState extends MutableProperty {

    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(ActivityState.class).hashCode();

    /**
     * The keys set regarding the multiple attributes defining this state, and that
     * each change need to be versioned/treated as a single atomic fact.
     */
    public enum PropertyAttributeKey {
	/** Boolean active/deactive state **/
	StateValue;
    }

    /**
     * Default constructor.
     * 
     * @param propertyOwner  Mandatory owner of this state (e.g account entity),
     *                       including the entity information.
     * @param isActiveStatus Mandatory value of state property (e.g true when
     *                       active).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When impossible creation of immutable
     *                                  version regarding the owner instance.
     */
    public ActivityState(EntityReference propertyOwner, Boolean isActiveStatus)
	    throws IllegalArgumentException, ImmutabilityException {
	this(propertyOwner, isActiveStatus, (ActivityState) null);
    }

    /**
     * Constructor with predecessors state automatically to save as history.
     * 
     * @param propertyOwner  Mandatory owner of this state (e.g account entity),
     *                       including the entity information.
     * @param isActiveStatus Mandatory value of state property (e.g true when
     *                       active).
     * @param predecessors   Optional prior states.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws ImmutabilityException    When impossible creation of immutable
     *                                  version regarding the owner instance.
     */
    public ActivityState(EntityReference propertyOwner, Boolean isActiveStatus, ActivityState... predecessors)
	    throws IllegalArgumentException, ImmutabilityException {
	this( /* Reference identifier equals to the owner of this state */
		(propertyOwner != null) ? propertyOwner.getEntity() : null,
		(isActiveStatus != null) ? buildPropertyValue(PropertyAttributeKey.StateValue, isActiveStatus) : null,
		HistoryState.COMMITTED, predecessors);
    }

    /**
     * Constructor with automatic initialization of an empty value set (prior
     * chain).
     * 
     * @param propertyOwner        Mandatory entity which is owner of this mutable
     *                             property chain.
     * @param propertyCurrentValue Mandatory current version of value(s) regarding
     *                             the property. Support included keys with null
     *                             value.
     * @param status               Optional state of this property version. If null,
     *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
     *                             is defined as default state.
     * @param predecessors         Optional prior states.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  cant' be cloned regarding immutable entity
     *                                  parameter.
     */
    public ActivityState(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
	    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") ActivityState... predecessors) {
	super(propertyOwner, propertyCurrentValue, status, predecessors);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	ActivityState copy = new ActivityState(this.ownerReference(),
		(Boolean) this.currentValue().get(PropertyAttributeKey.StateValue.name()));
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
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public HashMap<String, Object> buildPropertyValue(PropertyAttributeKey key, Object value)
	    throws IllegalArgumentException {
	if (key == null)
	    throw new IllegalArgumentException("key parameter is required!");
	HashMap<String, Object> val = new HashMap<>();
	val.put(key.name(), value);
	return val;
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
     * Get the current value of this complex property.
     * 
     * @return A set of valued attributes.
     */
    public HashMap<String, Object> currentValue() {
	return this.value;
    }

    /**
     * Get the status.
     * 
     * @return True if active state. False if deactivated.
     */
    public Boolean isActive() {
	return (Boolean) this.currentValue().get(PropertyAttributeKey.StateValue.name());
    }

    /**
     * Get the entity reference which is owner of this activity state property.
     * 
     * @return An owner reference.
     * @throws ImmutabilityException If impossible creation of immutable version of
     *                               instance.
     */
    public EntityReference ownerReference() throws ImmutabilityException {
	return this.owner().reference();
    }

    /**
     * Redefined equality evaluation including the owner, the status, the version in
     * history in terms of functional attributes compared.
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	boolean isEquals = false;
	if (obj instanceof ActivityState) {
	    try {
		ActivityState compared = (ActivityState) obj;
		// Check if same property owner
		if (compared.owner().equals(this.owner())) {
		    // Check if same status value
		    if (compared.isActive().equals(this.isActive())) {
			// Check if same history version
			if (compared.historyStatus() == this.historyStatus()) {
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