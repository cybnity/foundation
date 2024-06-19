package org.cybnity.feature.security_activity_orchestration.domain.model;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.MutableProperty;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.*;

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
		Steps
    }

	public Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException, ImmutabilityException {
		super(propertyOwner, propertyCurrentValue, status);
		// Check that minimum one step is included
		checkStepsConformity();
	}

	public Staging(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			Staging... prior) throws IllegalArgumentException, ImmutabilityException {
		super(propertyOwner, propertyCurrentValue, status, prior);
		// Check that minimum one step is included
		checkStepsConformity();
	}

	/**
	 * Verify if the steps include a minimum one step, and that steps are valid (e.g
	 * owner equals to this staging owner).
	 * 
	 * @throws IllegalArgumentException When cause of invalidity is detected.
	 * @throws ImmutabilityException    When problem of property read.
	 */
	private void checkStepsConformity() throws IllegalArgumentException, ImmutabilityException {
		List<Step> steps = steps();
		// Check that minimum one step is defined
		if (steps == null || steps.isEmpty() || steps.size() < 1)
			throw new IllegalArgumentException("Minimum one step is required to define a valid staging!");
		Entity stagingOwnerIdentity = this.owner();
		// Verify that each step owner is equals to the process entity (property owner)
		for (Step step : steps) {
			if (!step.owner().equals(stagingOwnerIdentity))
				throw new IllegalArgumentException(
						"Each step must be owned by this staging identity! Invalid defined property owner on item: "
								+ step.name());
		}
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
