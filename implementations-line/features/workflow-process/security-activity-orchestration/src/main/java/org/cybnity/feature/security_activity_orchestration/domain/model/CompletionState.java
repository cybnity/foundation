package org.cybnity.feature.security_activity_orchestration.domain.model;

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
 * Represent a state of completion defining by a name and optionally by a
 * percentage value about reached completion rate.
 * 
 * Can be used as an activity state for any type of subject.
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
		/** Name of a completion state **/
		Name,
		/** Numeric rate of completion based on a Float value */
		Percentage;
	}

	/**
	 * Default constructor.
	 * 
	 * @param propertyOwner Mandatory owner of this state (e.g step entity),
	 *                      including the entity information.
	 * @param named         Mandatory name of this state property.
	 * @param percentage    Optional current rate of completion.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public CompletionState(EntityReference propertyOwner, String named, Float currentPercentageOfCompletion)
			throws IllegalArgumentException, ImmutabilityException {
		this(propertyOwner, named, currentPercentageOfCompletion, (CompletionState) null);
	}

	/**
	 * Constructor with predecessors state automatically to save as history.
	 * 
	 * @param propertyOwner Mandatory owner of this state (e.g process), including
	 *                      the entity information.
	 * @param named         Mandatory name of this state property.
	 * @param percentage    Optional current rate of completion.
	 * @param predecessors  Optional prior states.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    When impossible creation of immutable
	 *                                  version regarding the owner instance.
	 */
	public CompletionState(EntityReference propertyOwner, String named, Float currentPercentageOfCompletion,
			CompletionState... predecessors) throws IllegalArgumentException, ImmutabilityException {
		this( /* Reference identifier equals to the owner of this state */
				(propertyOwner != null) ? propertyOwner.getEntity() : null,
				(named != null && !"".equals(named)) ? buildPropertyValue(PropertyAttributeKey.Name, named) : null,
				HistoryState.COMMITTED, predecessors);
		if (named == null || "".equals(named))
			throw new IllegalArgumentException("Name parameter is required!");
		if (currentPercentageOfCompletion != null) {
			this.value.put(PropertyAttributeKey.Percentage.name(), currentPercentageOfCompletion);
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
		CompletionState copy = new CompletionState(this.owner(), new HashMap<String, Object>(this.currentValue()),
				this.historyStatus(), (CompletionState[]) null);
		// Complete with additional attributes of this complex property
		copy.changedAt = this.occurredAt();
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
	 * @return An unmodifiable set of valued attributes.
	 */
	public Map<String, Object> currentValue() {
		return Collections.unmodifiableMap(this.value);
	}

	/**
	 * Get the state name.
	 * 
	 * @return A name.
	 */
	public String name() {
		return (String) this.currentValue().get(PropertyAttributeKey.Name.name());
	}

	/**
	 * Get the percentage of completion.
	 * 
	 * @return A percentage or null.
	 */
	public Float percentage() {
		return (Float) this.currentValue().getOrDefault(PropertyAttributeKey.Percentage.name(), null);
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
		if (obj instanceof CompletionState) {
			try {
				CompletionState compared = (CompletionState) obj;

				// Check if same property owner
				if (compared.owner().equals(this.owner())) {
					// Check if same name value
					if (compared.name().equals(this.name())) {
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
