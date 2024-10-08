package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This represents values that change on a property (simple or complex).
 * Mutable Property pattern represents changes to properties over time using
 * only immutable facts. It is desirable in a distributed system for nodes to be
 * able to act in isolation, to have the autonomy to change a property without
 * requiring a connection to any other node (e.g on a smartphone that is
 * temporarily disconnected from the server).
 * It is represented as a fact having the entity as a predecessor and the value
 * as a field. To keep track of changes overtime, it records prior versions in a
 * predecessor set. By convention, the name of the fact appends the property
 * name to the entity name (e.g EntityNamePropertyName). The set of prior
 * versions is conventionally called prior. This set is empty for the initial
 * value.
 * As a user changes the property, the prior set captures only the most recent
 * version.
 * Related pattern: if the mutable property represents a relationship with
 * another entity, the pattern becomes an
 * ({@link org.cybnity.framework.immutable.EntityReference}).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class MutableProperty implements IHistoricalFact {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(MutableProperty.class).hashCode();

	/**
	 * The owner of this mutable property.
	 */
	protected Entity owner;

	/**
	 * Current value of the property. Can be unique (e.g about a simple String field
	 * of an Entity), but also represents a current version of a combined complex
	 * object (e.g aggregation of multiples properties constitute a complex Entity).
	 * 
	 * The key is the property name, and the value is its current captured recent
	 * value version.
	 */
	protected HashMap<String, Object> value;

	/**
	 * Where the changed versions are historized as predecessors. In the chain of
	 * version, each prior item points back to its immediate predecessor.
	 * 
	 * When a node computes a tree with multiples leaves, it recognizes a concurrent
	 * change. In this situation, the application will typically present all leaves
	 * as candidate values where each one represents a value that was concurrently
	 * set for the property and has not been superseded. The user can select among
	 * the candidate values and resolve the dispute (can also be accomplished via a
	 * simple function over the leaves, such as a maximum equals to a several-way
	 * merge).
	 * 
	 * Facts are only generated as a result of a user's decision. When the user
	 * changes a property from a concurrent state, the system includes all of the
	 * leaves of the tree in the newt fact's prior set (value attribute).
	 * 
	 */
	@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3")
	protected LinkedHashSet<MutableProperty> prior;

	/**
	 * When this fact version was created or observed regarding the historized
	 * topic.
	 */
	protected OffsetDateTime changedAt;

	/**
	 * Identify this property value had been confirmed (e.g during a merging
	 * conflict resolution act decided by a user) as official current version.
	 * org.cybnity.framework.immutable.HistoryState.COMMITTED by default for
	 * any new instance of new instantiated property.
	 */
	protected HistoryState historyStatus = HistoryState.COMMITTED;

	/**
	 * Default constructor with automatic initialization of an empty value set
	 * (prior chain).
	 * 
	 * @param propertyOwner        Mandatory entity which is owner of this mutable
	 *                             property chain.
	 * @param propertyCurrentValue Mandatory current version of value(s) regarding
	 *                             the property. Support included keys with null
	 *                             value.
	 * @param status               Optional state of this property version. If null,
	 *                             org.cybnity.framework.immutable.HistoryState.COMMITTED
	 *                             is defined as default state.
	 * @throws IllegalArgumentException When mandatory parameter is missing, or when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException {
		if (propertyOwner == null)
			throw new IllegalArgumentException("PropertyOwner mandatory parameter is missing!");
		if (propertyCurrentValue == null || propertyCurrentValue.isEmpty())
			throw new IllegalArgumentException("PropertyCurrentValue mandatory parameter is missing!");
		try {
			this.owner = (Entity) propertyOwner.immutable();
			// Set of prior versions is empty by default
			this.prior = new LinkedHashSet<>();
			// Set the current states of changed values regarding this property version
			this.value = propertyCurrentValue;
			if (status != null)
				this.historyStatus = status;
			// Create immutable time of this property changed version
			this.changedAt = OffsetDateTime.now();
		} catch (ImmutabilityException ce) {
			throw new IllegalArgumentException(ce);
		}
	}

	/**
	 * Constructor of new property value version linked to previous versions of this
	 * property.
	 * 
	 * @param propertyOwner        Mandatory entity which is owner of this mutable
	 *                             property chain.
	 * @param propertyCurrentValue Mandatory current version of value(s) regarding
	 *                             the property. Support included keys with null
	 *                             value.
	 * @param status               Optional state of this property version. If null,
	 *                             org.cybnity.framework.immutable.HistoryState.COMMITTED
	 *                             is defined as default state.
	 * @param predecessors         Optional original instances (previous versions)
	 *                             that were to consider in the history chain,
	 *                             regarding this property and that were identified
	 *                             as property's original states which had been
	 *                             changed. It's possible that new instance (e.g in
	 *                             org.cybnity.framework.immutable.HistoryState.MERGED
	 *                             status) is based on several merged versions of
	 *                             previous property's states (e.g in case of
	 *                             concurrently changed version with need of
	 *                             conflict resolution). Ignored if null.
	 * @throws IllegalArgumentException When mandatory parameter is missing; when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			@Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") MutableProperty... predecessors)
			throws IllegalArgumentException {
		// Initialize instance as default without previous versions of property's values
		this(propertyOwner, propertyCurrentValue, status);
		if (predecessors != null) {
			// Manage the possible parallel concurrently previous state (e.g regarding
			// original previous values of this property that were evaluated to dedicate
			// this
			// new value)
			for (MutableProperty p : predecessors) {
				if (p != null) {
					this.prior.add(p);
				}
			}
		}
	}

	/**
	 * Default equality of mutable property based on several compared values (class
	 * type, owner entity, current values defining this property, history status,
	 * changed date).
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		boolean isEquals = false;
		if (obj.getClass() == this.getClass()) {
			try {
				MutableProperty compared = (MutableProperty) obj;
				// Check if same property owner
				if (compared.owner().equals(this.owner())) {
					// Check if same status value
					if (compared.historyStatus() == this.historyStatus()) {
						// Check the values equality
						if (compared.value != null && this.value != null) {
							// Comparable values are existing

							// Check that all values of this property are existing and equals into the
							// compared property
							boolean notEqualsValueFound = false;
							for (Map.Entry<String, Object> entry : this.value.entrySet()) {
								if (!compared.value.containsKey(entry.getKey())
										|| !compared.value.containsValue(entry.getValue())) {
									// Missing equals value and key is found
									// Stop search because not equals values into the compared instance with the
									// values hosted by this instance
									notEqualsValueFound = true;
									break;
								}
							}
							if (!notEqualsValueFound) {
								// Check that all values of the compared instance are existing and equals into
								// this property
								for (Map.Entry<String, Object> entry : compared.value.entrySet()) {
									if (!this.value.containsKey(entry.getKey())
											|| !this.value.containsValue(entry.getValue())) {
										// Missing equals value and key is found
										// Stop search because not equals values into this instance with the
										// values hosted by the compared instance
										notEqualsValueFound = true;
										break;
									}
								}
							}
							isEquals = !notEqualsValueFound;
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
	 * Who is the owner of this property
	 * 
	 * @return The owner
	 * @throws ImmutabilityException If impossible creation of immutable version of
	 *                               instance.
	 */
	public Entity owner() throws ImmutabilityException {
		return (Entity) this.owner.immutable();
	}

	/**
	 * This property version state (e.g in a situation of concurrent change of
	 * predecessor values requiring merging of new status to fix the new value of
	 * this one) regarding its anterior versions.
	 * 
	 * @return Official version of this property.
	 *         org.cybnity.framework.immutable.HistoryState.COMMITTED by
	 *         default.
	 */
	public HistoryState historyStatus() {
		return historyStatus;
	}

	/**
	 * Set the state of this property value as an official version of the history
	 * chain, or shall only be saved in history as a concurrent version which had
	 * been archived following a merging or reject decision (e.g by a user in front
	 * of several potential changes requested concurrently of the prior version).
	 * 
	 * @param state Status of this version as current version property in front of
	 *              potential other parallel/concurrent versions regarding a same
	 *              priors. If null, ignored.
	 */
	public void setHistoryStatus(HistoryState state) {
		if (state != null)
			this.historyStatus = state;
	}

	/**
	 * Add the history of this current instance, into a version of property to
	 * enhance (e.g as a new version of this one).
	 * 
	 * @param versionToEnhance Mandatory version to update. Ignored when null.
	 * @param versionState     Optional history status to set on the the
	 *                         versionToEnhance property.
	 * @return The versionToEnhance instance including the added history.
	 * @throws IllegalArgumentException When the property parameter to enhance is
	 *                                  not the same class type than this property.
	 */
	public MutableProperty enhanceHistoryOf(MutableProperty versionToEnhance, HistoryState versionState)
			throws IllegalArgumentException {
		if (versionToEnhance != null) {
			if (this.getClass() != versionToEnhance.getClass())
				throw new IllegalArgumentException("Invalid type of version to enhance!");
			// Archive the previous versions regarding existing history when exist
			Set<MutableProperty> changedPredecessors = this.changesHistory();
			// Add current version into history
			changedPredecessors.add(this);
			if (versionState != null)
				// Set the state to the current new version of this property
				versionToEnhance.setHistoryStatus(versionState);
			// Set the old history (including the previous last version of this property)
			versionToEnhance.updateChangesHistory(changedPredecessors);
		}
		return versionToEnhance;
	}

	/**
	 * Get the history chain of previous versions of this property including
	 * previous changed values versions.
	 * 
	 * @return A changes history. Empty list by default.
	 */
	public Set<MutableProperty> changesHistory() {
		// Read previous changes history (not including the current version)
		LinkedHashSet<MutableProperty> history = new LinkedHashSet<>();
		for (MutableProperty previousChangedProperty : this.prior) {
			history.add(previousChangedProperty);
		}
		return history;
	}

	/**
	 * Update the history old versions of this property.
	 * 
	 * @param versions To set as new version of this property versions history.
	 *                 Ignore if null or empty.
	 */
	public void updateChangesHistory(Set<MutableProperty> versions) {
		if (versions != null && !versions.isEmpty()) {
			// Update the story at end of previous versions
			LinkedHashSet<MutableProperty> history = new LinkedHashSet<>();
			for (MutableProperty aVersion : versions) {
				history.add(aVersion);
			}
			// Replace current history
			this.prior = history;
		}
	}

	/**
	 * Default implementation of fact date when it was created.
	 */
	@Override
	public OffsetDateTime occurredAt() {
		// Return immutable value of the fact time
		return this.changedAt;
	}

	/**
	 * Build a definition of property based on property name and value.
	 * 
	 * @param key   Mandatory name of the property.
	 * @param value Value of the key.
	 * @return A definition of the property.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 */
	static public HashMap<String, Object> buildPropertyValue(String key, Object value) throws IllegalArgumentException {
		if (key == null)
			throw new IllegalArgumentException("key parameter is required!");
		HashMap<String, Object> val = new HashMap<>();
		val.put(key, value);
		return val;
	}
}
