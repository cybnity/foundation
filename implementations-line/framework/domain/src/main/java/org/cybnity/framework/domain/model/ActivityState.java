package org.cybnity.framework.domain.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
				(isActiveStatus != null) ? buildPropertyValue(PropertyAttributeKey.StateValue.name(), isActiveStatus)
						: null,
				HistoryState.COMMITTED, predecessors);
		if (isActiveStatus == null)
			throw new IllegalArgumentException("isActiveStatus parameter is required!");
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
	 * @param status               Optional history state of this property version.
	 *                             If null,
	 *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
	 *                             is defined as default state.
	 * @param predecessors         Optional prior states.
	 * @throws IllegalArgumentException When mandatory parameter is missing, or when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public ActivityState(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") ActivityState... predecessors) {
		super(propertyOwner, propertyCurrentValue, status, predecessors);
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		ActivityState copy = new ActivityState(this.ownerReference(), this.isActive());
		// Complete with additional attributes of this complex property
		copy.changedAt = this.occurredAt();
		copy.historyStatus = this.historyStatus();
		copy.updateChangesHistory(this.changesHistory());
		return copy;
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
	 * @return An unmodifiable set of valued attributes.
	 */
	public Map<String, Object> currentValue() {
		return Collections.unmodifiableMap(this.value);
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

	/**
	 * Verify if the state include basic attributes and values and that property
	 * owner is equals to the owner.
	 * 
	 * @param state Mandatory state to check.
	 * @param owner Mandatory owner of the state to compare as a status condition.
	 * @throws IllegalArgumentException When non conformity cause is detected.
	 * @throws ImmutabilityException    When impossible read of description's owner.
	 */
	public static void checkActivationConformity(ActivityState state, Entity owner)
			throws IllegalArgumentException, ImmutabilityException {
		if (state != null) {
			if (owner == null)
				throw new IllegalArgumentException("State owner parameter is required!");
			// Check that minimum name attribute is defined into the status

			// Check the status value
			if (state.isActive() == null)
				throw new IllegalArgumentException("Status value is required from state!");

			if (owner != null) {
				// Check that owner of the state is equals to this owner identity
				if (state.owner() == null || !state.owner().equals(owner))
					throw new IllegalArgumentException(
							"The owner of the activity state shall be equals to the owner parameter!");
			}
		}
	}
}
