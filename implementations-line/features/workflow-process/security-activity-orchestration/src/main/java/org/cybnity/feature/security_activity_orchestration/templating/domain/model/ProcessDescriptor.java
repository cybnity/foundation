package org.cybnity.feature.security_activity_orchestration.templating.domain.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Definition regarding a process, that can be changed, and which need to be
 * historized in an immutable way the history of changes (version of this
 * information).
 * 
 * Sample usable to evaluate the
 * {@link org.cybnity.framework.immutable.MutableProperty} pattern.
 * 
 * @author olivier
 *
 */
public class ProcessDescriptor extends MutableProperty {

	private static final long serialVersionUID = 1L;
	private OffsetDateTime versionedAt;

	/**
	 * Keys set regarding the multiple attribute defining this complex organization,
	 * and that each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		Name;
	}

	public ProcessDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
		this.versionedAt = OffsetDateTime.now();
	}

	public ProcessDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ProcessDescriptor... prior) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status, prior);
		this.versionedAt = OffsetDateTime.now();
	}

	/**
	 * Get the process name.
	 * 
	 * @return A label or null.
	 */
	public String getName() {
		return (String) this.currentValue().getOrDefault(PropertyAttributeKey.Name.name(), null);
	}

	@Override
	public OffsetDateTime occurredAt() {
		return this.versionedAt;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		ProcessDescriptor copy = new ProcessDescriptor(this.owner(), this.currentValue(), this.historyStatus());
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
