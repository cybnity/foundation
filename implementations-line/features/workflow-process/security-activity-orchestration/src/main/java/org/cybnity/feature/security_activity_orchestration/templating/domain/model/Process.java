package org.cybnity.feature.security_activity_orchestration.templating.domain.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;

import org.cybnity.feature.security_activity_orchestration.ITemplate;
import org.cybnity.framework.domain.model.ActivityState;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

public class Process extends ActivityState implements ITemplate {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Process.class).hashCode();

	private OffsetDateTime versionedAt;
	
	private ProcessDescriptor description;

	/**
	 * Keys set regarding the multiple attribute defining this complex template, and
	 * that each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		/** Name of the template **/
		Name;
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
	public Process(EntityReference propertyOwner, Boolean isActiveStatus)
			throws IllegalArgumentException, ImmutabilityException {
		super(propertyOwner, isActiveStatus);
		this.versionedAt = OffsetDateTime.now();
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
	public Process(EntityReference propertyOwner, Boolean isActiveStatus, Process... predecessors)
			throws IllegalArgumentException, ImmutabilityException {
		super(propertyOwner, isActiveStatus, predecessors);
		this.versionedAt = OffsetDateTime.now();
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
	 * @param status               Optional history version state of this property
	 *                             version. If null,
	 *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
	 *                             is defined as default state.
	 * @param predecessors         Optional prior states.
	 * @throws IllegalArgumentException When mandatory parameter is missing, or when
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public Process(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			Process... predecessors) {
		super(propertyOwner, propertyCurrentValue, status, predecessors);
		this.versionedAt = OffsetDateTime.now();
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Process copy = new Process(this.owner(), this.currentValue(), this.historyStatus());
		// Complete with additional attributes of this complex property
		copy.versionedAt = this.versionedAt;
		copy.changedAt = this.occurredAt();
		copy.updateChangesHistory(this.changesHistory());
		return copy;
	}

	/**
	 * Redefined equality evaluation including the property owner, the status, the
	 * version in history in terms of functional attributes compared.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;
		// Comparison based on owner, status, version functional attributes
		if (super.equals(obj) && obj instanceof Process) {
			try {
				Process compared = (Process) obj;
				// Check if same names
				String objNameAttribute = compared.name();
				String thisNameAttribute = this.name();
				if (objNameAttribute != null && thisNameAttribute != null) {
					isEquals = objNameAttribute.equals(thisNameAttribute);
				}
			} catch (Exception e) {
				// any missing information generating null pointer exception or problem of
				// information read
			}
		}
		return isEquals;
	}

	/**
	 * Get the name of the template.
	 * 
	 * @return A label or null.
	 */
	@Override
	public String name() {
		if (this.currentValue() != null) {
			return (String) this.currentValue().getOrDefault(PropertyAttributeKey.Name.name(), null);
		}
		return null;
	}

	@Override
	public OffsetDateTime occurredAt() {
		return this.versionedAt;
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
