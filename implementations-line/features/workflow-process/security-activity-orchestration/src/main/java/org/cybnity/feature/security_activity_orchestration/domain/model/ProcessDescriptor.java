package org.cybnity.feature.security_activity_orchestration.domain.model;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Definition regarding a process, that can be changed, and which need to be
 * historized in an immutable way the history of changes (version of this
 * information).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ProcessDescriptor extends MutableProperty {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(ProcessDescriptor.class).hashCode();

	/**
	 * Keys set regarding the multiple attribute defining this complex organization,
	 * and that each change need to be versioned/treated as a single atomic fact.
	 */
	public enum PropertyAttributeKey {
		/** Name of a process **/
		Name,
		/** Attributes collection specifying the process as a description of it */
		Properties,
		/**
		 * Template reference (EntityReference) that was source of structure regarding a
		 * process
		 **/
		TemplateEntityRef;
	}

	public ProcessDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
			throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status);
	}

	public ProcessDescriptor(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
			ProcessDescriptor... prior) throws IllegalArgumentException {
		super(propertyOwner, propertyCurrentValue, status, prior);
	}

	/**
	 * Get the reference of a template domain entity which was the origin of the
	 * process structure.
	 * 
	 * @return A template entity reference or null.
	 */
	public EntityReference templateEntityRef() {
		return (EntityReference) this.currentValue().getOrDefault(PropertyAttributeKey.TemplateEntityRef.name(), null);
	}

	/**
	 * Get the process name.
	 * 
	 * @return A label or null.
	 */
	public String name() {
		return (String) this.currentValue().getOrDefault(PropertyAttributeKey.Name.name(), null);
	}

	/**
	 * Get the description properties regarding this process.
	 * 
	 * @return A set of properties specifying the process description, or null.
	 */
	@SuppressWarnings("unchecked")
	public Collection<Attribute> properties() {
		try {
			return (Collection<Attribute>) this.currentValue().getOrDefault(PropertyAttributeKey.Properties.name(),
					null);
		} catch (Exception cce) {
			// Invalid type of collection object implemented. Add developer log about coding
			// problem
		}
		return null;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		ProcessDescriptor copy = new ProcessDescriptor(this.owner(), new HashMap<String, Object>(this.currentValue()),
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
