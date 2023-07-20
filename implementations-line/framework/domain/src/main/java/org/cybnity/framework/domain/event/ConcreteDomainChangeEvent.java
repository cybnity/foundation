package org.cybnity.framework.domain.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Generic event regarding a change occurred on a topic relative to a domain.
 * 
 * @author olivier
 *
 */
public class ConcreteDomainChangeEvent extends DomainEvent implements IDescribed {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(ConcreteDomainChangeEvent.class).hashCode();

	/**
	 * Standard name of the attribute specifying this event type based on a logical
	 * type.
	 */
	public static String TYPE = "type";

	/**
	 * Identify the original command reference that was cause of this event
	 * publication.
	 */
	private EntityReference changeCommandRef;

	/**
	 * Identify the element of the domain model which was changed.
	 */
	private EntityReference changedModelElementRef;

	/**
	 * Collection of contributor to the definition of this event, based on
	 * unmodifiable attributes.
	 */
	private Collection<Attribute> specification;

	public ConcreteDomainChangeEvent() {
		super();
	}

	public ConcreteDomainChangeEvent(Enum<?> eventType) {
		this();
		if (eventType != null) {
			// Add type into specification attributes
			appendSpecification(new Attribute(TYPE, eventType.name()));
		}
	}

	public ConcreteDomainChangeEvent(Entity identifiedBy) {
		super(identifiedBy);
	}

	public ConcreteDomainChangeEvent(Entity identifiedBy, Enum<?> eventType) {
		super(identifiedBy);
		if (eventType != null) {
			// Add type into specification attributes
			appendSpecification(new Attribute(TYPE, eventType.name()));
		}
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		ConcreteDomainChangeEvent instance = new ConcreteDomainChangeEvent(this.getIdentifiedBy());
		instance.occuredOn = this.occurredAt();

		// Add immutable version of each additional attributes hosted by this event
		EntityReference cmdRef = this.changeCommandReference();
		if (cmdRef != null)
			instance.setChangeCommandReference(cmdRef);
		EntityReference subjectRef = this.changedModelElementReference();
		if (subjectRef != null)
			instance.setChangedModelElementReference(subjectRef);
		if (this.specification != null && !this.specification.isEmpty()) {
			instance.specification = this.specification();
		}
		return instance;
	}

	/**
	 * Define the reference of the domain element that was changed.
	 * 
	 * @param ref A domain object reference.
	 */
	public void setChangedModelElementReference(EntityReference ref) {
		this.changedModelElementRef = ref;
	}

	/**
	 * Get the reference of the domain element that was subject of change.
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
	 * Define the reference of the original command that causing the publication of
	 * this event.
	 * 
	 * @param ref A reference (e.g regarding a command event which was treated by a
	 *            domain object to change one or several of its values) or null.
	 */
	public void setChangeCommandReference(EntityReference ref) {
		this.changeCommandRef = ref;
	}

	/**
	 * Get the reference of the command that was origin of a domain object change.
	 * 
	 * @return A reference immutable version, or null.
	 * @throws ImmutabilityException When impossible return of immutable version.
	 */
	public EntityReference changeCommandReference() throws ImmutabilityException {
		EntityReference r = null;
		if (this.changeCommandRef != null)
			r = (EntityReference) this.changeCommandRef.immutable();
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
