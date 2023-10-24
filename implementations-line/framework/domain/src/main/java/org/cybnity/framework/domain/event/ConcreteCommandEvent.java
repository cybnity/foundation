package org.cybnity.framework.domain.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Generic event regarding a command to execute requested regarding a topic and that can be interpreted by a domain.
 * 
 * @author olivier
 *
 */
public class ConcreteCommandEvent extends Command implements IDescribed {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(ConcreteCommandEvent.class).hashCode();

	/**
	 * Standard name of the attribute specifying this command type based on a logical
	 * type.
	 */
	public static String TYPE = "type";

	/**
	 * Identify the original command reference that was previous source of this command
	 * publication.
	 */
	private EntityReference priorCommandRef;

	/**
	 * Identify the element of the domain model which was subject of command.
	 */
	private EntityReference changedModelElementRef;

	/**
	 * Collection of contributor to the definition of this event, based on
	 * unmodifiable attributes.
	 */
	private Collection<Attribute> specification;

	public ConcreteCommandEvent() {
		super();
	}

	public ConcreteCommandEvent(Enum<?> eventType) {
		this();
		if (eventType != null) {
			// Add type into specification attributes
			appendSpecification(new Attribute(TYPE, eventType.name()));
		}
	}

	public ConcreteCommandEvent(Entity identifiedBy) {
		super(identifiedBy);
	}

	public ConcreteCommandEvent(Entity identifiedBy, Enum<?> eventType) {
		super(identifiedBy);
		if (eventType != null) {
			// Add type into specification attributes
			appendSpecification(new Attribute(TYPE, eventType.name()));
		}
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		ConcreteCommandEvent instance = new ConcreteCommandEvent(this.getIdentifiedBy());
		instance.occuredOn = this.occurredAt();

		// Add immutable version of each additional attributes hosted by this event
		EntityReference cmdRef = this.priorCommandReference();
		if (cmdRef != null)
			instance.setPriorCommandReference(cmdRef);
		EntityReference subjectRef = this.changedModelElementReference();
		if (subjectRef != null)
			instance.setChangedModelElementReference(subjectRef);
		if (this.specification != null && !this.specification.isEmpty()) {
			instance.specification = this.specification();
		}
		return instance;
	}

	/**
	 * Define the reference of the domain element that was subject of this command execution.
	 * 
	 * @param ref A domain object reference.
	 */
	public void setChangedModelElementReference(EntityReference ref) {
		this.changedModelElementRef = ref;
	}

	/**
	 * Get the reference of the domain element that was subject of command.
	 * 
	 * @return A reference immutable version, or null.
	 * @throws ImmutabilityException When impossible return of immutable version.
	 */
	public EntityReference changedModelElementReference() throws ImmutabilityException {
		EntityReference r = null;
		if (this.changedModelElementRef != null)
			r = (EntityReference) this.changedModelElementRef.immutable();
		return r;
	}

	/**
	 * Define the reference of a previous command that causing the publication of
	 * this event.
	 * 
	 * @param ref A reference (e.g regarding a command event which was treated by a
	 *            domain object to treat) or null.
	 */
	public void setPriorCommandReference(EntityReference ref) {
		this.priorCommandRef = ref;
	}

	/**
	 * Get the reference of the command that was previous to this command.
	 * 
	 * @return A reference immutable version, or null.
	 * @throws ImmutabilityException When impossible return of immutable version.
	 */
	public EntityReference priorCommandReference() throws ImmutabilityException {
		EntityReference r = null;
		if (this.priorCommandRef != null)
			r = (EntityReference) this.priorCommandRef.immutable();
		return r;
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}

	@Override
	public boolean appendSpecification(Attribute specificationCriteria) {
		boolean appended = false;
		if (specificationCriteria != null) {
			if (this.specification == null) {
				// Initialize the attribute container of unmodifiable specification
				this.specification = new ArrayList<>();
			}
			// Check that equals named attribute is not already defined in the specification
			if (!this.specification.contains(specificationCriteria)) {
				// Append the criteria
				this.specification.add(specificationCriteria);
			}
		}
		return appended;
	}

	@Override
	public Collection<Attribute> specification() {
		if (this.specification != null && !this.specification.isEmpty()) {
			// Return immutable version
			return Collections.unmodifiableCollection(this.specification);
		}
		return null;
	}
}
