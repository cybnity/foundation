package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Example of specific property regarding an organization, that can be changed
 * (e.g by organization manager when the company's physical address is moved),
 * and which need to historized in an immutable way the history of changes
 * (version of this information).
 * 
 * Sample usable to evaluate the
 * {@link org.cybnity.framework.immutable.MutableProperty} pattern.
 * 
 * @author olivier
 *
 */
public class PhysicalAddressProperty extends MutableProperty {

	private static final long serialVersionUID = 1L;
	private final OffsetDateTime versionedAt;

	/**
	 * Example of keys set regarding the multiple attribute defining this complex
	 * physical address, and that each change need to be versioned/treated as a
	 * single atomic fact.
	 */
	public enum PropertyAttributeKey {
		Street, City, Country, State
    }

	public PhysicalAddressProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
			HistoryState status) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
		this.versionedAt = OffsetDateTime.now();
	}

	public PhysicalAddressProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
			HistoryState status, PhysicalAddressProperty... prior) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status, prior);
		this.versionedAt = OffsetDateTime.now();
	}

	@Override
	public OffsetDateTime occurredAt() {
		return this.versionedAt;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return null;
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
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}
}
