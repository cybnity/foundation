package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Definition of a workflow specification based on steps, that can be changed,
 * and which need to be historized in an immutable way the history of changes
 * (version of this information).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Staging extends MutableProperty {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Staging.class).hashCode();

	/**
	 * Keys set regarding the multiple attribute defining this complex organization,
	 * and that each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		/** Steps definition **/
		Steps;
	}

	public Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
	}

	public Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			Staging... prior) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status, prior);
	}

	/**
	 * Get the ordered steps.
	 * 
	 * @return A list including steps or empty list.
	 */
	@SuppressWarnings("unchecked")
	public List<Step> steps() {
		return (List<Step>) this.currentValue().getOrDefault(PropertyAttributeKey.Steps.name(), new LinkedList<>());
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Staging copy = new Staging(this.owner(), new HashMap<String, Object>(this.currentValue()),
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
