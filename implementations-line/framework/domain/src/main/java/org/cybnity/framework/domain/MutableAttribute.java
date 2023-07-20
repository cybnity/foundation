package org.cybnity.framework.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Attribute that can be changed, and which need to be historized in an
 * immutable way the history of changes (version of this information).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class MutableAttribute extends MutableProperty {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(MutableAttribute.class).hashCode();

	/**
	 * Keys set regarding the multiple attribute defining this complex organization,
	 * and that each change need to be versioned/treated as a single atomic fact.
	 */
	private enum PropertyAttributeKey {
		Value;
	}

	/**
	 * Default constructor.
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
	 *                                  can not be cloned regarding immutable entity
	 *                                  parameter.
	 */
	public MutableAttribute(Entity propertyOwner, Attribute currentValue, HistoryState status)
			throws IllegalArgumentException {
		super(propertyOwner, buildPropertyValue(PropertyAttributeKey.Value.name(), currentValue), status);
	}

	public MutableAttribute(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
	}

	public MutableAttribute(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			MutableAttribute... prior) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status, prior);
	}

	/**
	 * Get the attribute value.
	 * 
	 * @return A value.
	 */
	public Attribute value() {
		return (Attribute) this.currentValue().getOrDefault(PropertyAttributeKey.Value.name(), null);
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		MutableAttribute copy = new MutableAttribute(this.owner(), new HashMap<String, Object>(this.currentValue()),
				this.historyStatus());
		// Complete with additional attributes of this complex property
		copy.changedAt = this.occurredAt();
		copy.updateChangesHistory(this.changesHistory());
		return copy;
	}

	/**
	 * Get the current value of this complex property.
	 * 
	 * @return A set of valued attributes.
	 */
	public Map<String, Object> currentValue() {
		return Collections.unmodifiableMap(this.value);
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
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}
}
