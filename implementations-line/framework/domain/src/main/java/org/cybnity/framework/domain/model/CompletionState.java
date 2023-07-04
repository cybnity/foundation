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
 * Represent a state of completion (e.g status and specific percentage of
 * finalization regarding a work) regarding a subject (e.g process, task,
 * build). Can be used as an activity tag for any type of subject.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class CompletionState extends MutableProperty {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(CompletionState.class).hashCode();

	/**
	 * The keys set regarding the multiple attributes defining this state, and that
	 * each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		/**
		 * Name of this state (e.g started, partially achieved, fully achieved, done)
		 **/
		StateName,
		/** Current percentage of completion **/
		Percentage;
	}

	/**
	 * Default constructor.
	 * 
	 * @param propertyOwner     Mandatory owner of this state (e.g process),
	 *                          including the entity information.
	 * @param currentStateName  Mandatory value of state property (e.g started, in
	 *                          progress, done).
	 * @param currentPercentage Optional percentage of current completion (e.g 0.5
	 *                          when 50% completed status).
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public CompletionState(EntityReference propertyOwner, String currentStateName, Float currentPercentage)
			throws IllegalArgumentException, ImmutabilityException {
		this(propertyOwner, currentStateName, currentPercentage, (CompletionState) null);
	}

	/**
	 * Constructor with predecessors state automatically to save as history.
	 * 
	 * @param propertyOwner     Mandatory owner of this state (e.g process),
	 *                          including the entity information.
	 * @param currentStateName  Mandatory value of state property (e.g started, in
	 *                          progress, done).
	 * @param currentPercentage Optional percentage of current completion (e.g 0.5
	 *                          when 50% completed status).
	 * @param predecessors      Optional prior states.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public CompletionState(EntityReference propertyOwner, String currentStateName, Float currentPercentage,
			CompletionState... predecessors) throws IllegalArgumentException, ImmutabilityException {
		this( /* Reference identifier equals to the owner of this state */
				(propertyOwner != null) ? propertyOwner.getEntity() : null,
				(currentStateName != null && !"".equals(currentStateName))
						? buildPropertyValue(PropertyAttributeKey.StateName.name(), currentStateName)
						: null,
				HistoryState.COMMITTED, predecessors);
		if (currentPercentage != null) {
			// Add additional properties
			this.value.putAll(buildPropertyValue(PropertyAttributeKey.Percentage.name(), currentPercentage));
		}
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
	public CompletionState(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") CompletionState... predecessors) {
		super(propertyOwner, propertyCurrentValue, status, predecessors);
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		CompletionState copy = new CompletionState(this.ownerReference(), this.stateName(), this.percentage());
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
		return (HashMap<String, Object>) Collections.unmodifiableMap(this.value);
	}

	/**
	 * Get the status name.
	 * 
	 * @return Name of this state.
	 */
	public String stateName() {
		return (String) this.currentValue().get(PropertyAttributeKey.StateName.name());
	}

	/**
	 * Get the completion percentage.
	 * 
	 * @return A percentage (e.g 0.5 for 50% completed) or null.
	 */
	public Float percentage() {
		return (Float) this.currentValue().get(PropertyAttributeKey.Percentage.name());
	}

	/**
	 * Get the entity reference which is owner of this state property.
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
		if (obj instanceof CompletionState) {
			try {
				CompletionState compared = (CompletionState) obj;

				// Check if same property owner
				if (compared.owner().equals(this.owner())) {
					// Check if same status value
					if (compared.stateName().equals(this.stateName())) {
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
