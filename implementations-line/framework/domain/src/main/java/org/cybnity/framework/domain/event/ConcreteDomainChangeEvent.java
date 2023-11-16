package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Generic event regarding a change occurred on a topic relative to a domain.
 *
 * @author olivier
 */
@JsonTypeName("changeEvent")
public class ConcreteDomainChangeEvent extends DomainEvent {

    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ConcreteDomainChangeEvent.class).hashCode();

    /**
     * Standard name of the attribute specifying this event type based on a logical
     * type.
     */
    @JsonIgnore
    public static String TYPE = "type";

    /**
     * Identify the original command reference that was cause of this event
     * publication.
     */
    @JsonProperty
    private EntityReference changeCommandRef;

    /**
     * Identify the element of the domain model which was changed.
     */
    @JsonProperty
    private EntityReference changedModelElementRef;

    /**
     * Collection of contributor to the definition of this event, based on
     * unmodifiable attributes.
     */
    @JsonProperty
    private Collection<Attribute> specification;

    @JsonCreator
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

    public ConcreteDomainChangeEvent(Entity identifiedBy, String eventType) {
        super(identifiedBy);
        if (!"".equals(eventType)) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType));
        }
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        ConcreteDomainChangeEvent instance = new ConcreteDomainChangeEvent(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();

        // Add immutable version of each additional attributes hosted by this event
        EntityReference cmdRef = this.changeCommandReference();
        if (cmdRef != null)
            instance.setChangeCommandRef(cmdRef);
        EntityReference subjectRef = this.changedModelElementReference();
        if (subjectRef != null)
            instance.setChangedModelElementRef(subjectRef);
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
    public void setChangedModelElementRef(EntityReference ref) {
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
    public void setChangeCommandRef(EntityReference ref) {
        this.changeCommandRef = ref;
    }

    /**
     * Define collection of contributors to the definition of this event, based on unmodifiable attributes.
     *
     * @param specification Description element relative to this command event.
     */
    public void setSpecification(Collection<Attribute> specification) {
        this.specification = specification;
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
    @JsonIgnore
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    @JsonIgnore
    @Override
    public boolean appendSpecification(Attribute specificationCriteria) {
        if (specification == null) {
            // Initialize the attribute container of unmodifiable specification
            specification = new ArrayList<>();
        }
        return EventSpecification.appendSpecification(specificationCriteria, specification);
    }

    @Override
    public Collection<Attribute> specification() {
        if (this.specification != null && !this.specification.isEmpty()) {
            // Return immutable version
            return Collections.unmodifiableCollection(this.specification);
        }
        return null;
    }

    /**
     * Define when this event occurred.
     *
     * @param occurredOn A date.
     */
    public void setOccurredOn(OffsetDateTime occurredOn) {
        this.occurredOn = occurredOn;
    }

    /**
     * Search existing assigned correlation identifier based on the CORRELATION_ID attribute which could have been generated and stored into the specification set.
     *
     * @return Assigned correlation identifier, or null.
     */
    @Override
    public Attribute correlationId() {
        if (this.specification != null) {
            // Search optionally and previously generated correlation id
            return EventSpecification.findSpecificationByName(Command.CORRELATION_ID, this.specification);
        }
        return null;
    }
}
