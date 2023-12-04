package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
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
 * Generic event regarding a command to execute requested regarding a topic and that can be interpreted by a domain.
 * It's a Query event of CQRS pattern.
 *
 * @author olivier
 */
@JsonTypeName("Query")
public class ConcreteQueryEvent extends Command {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ConcreteQueryEvent.class).hashCode();

    /**
     * Standard type of the attribute specifying this query type based on a logical
     * type.
     */
    @JsonIgnore
    public static String TYPE = "type";

    /**
     * Identify the original event reference that was previous source of this query
     * publication.
     */
    @JsonProperty
    private EntityReference priorCommandRef;

    /**
     * Identify the element of the domain model which was subject of query.
     */
    @JsonProperty
    private EntityReference queriedModelElementRef;

    /**
     * Collection of contributor to the definition of this event, based on
     * unmodifiable attributes.
     */
    @JsonProperty
    private Collection<Attribute> specification;

    @JsonCreator
    public ConcreteQueryEvent() {
        super();
    }

    public ConcreteQueryEvent(Enum<?> eventType) {
        this();
        if (eventType != null) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType.name()));
        }
    }

    public ConcreteQueryEvent(Entity identifiedBy) {
        super(identifiedBy);
    }

    public ConcreteQueryEvent(Entity identifiedBy, Enum<?> eventType) {
        super(identifiedBy);
        if (eventType != null) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType.name()));
        }
    }

    public ConcreteQueryEvent(Entity identifiedBy, String eventType) {
        super(identifiedBy);
        if (!"".equals(eventType)) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType));
        }
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        ConcreteQueryEvent instance = new ConcreteQueryEvent(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();

        // Add immutable version of each additional attributes hosted by this event
        EntityReference cmdRef = this.priorCommandReference();
        if (cmdRef != null)
            instance.setPriorCommandRef(cmdRef);
        EntityReference subjectRef = this.queriedModelElementReference();
        if (subjectRef != null)
            instance.setQueriedModelElementRef(subjectRef);
        if (this.specification != null && !this.specification.isEmpty()) {
            instance.specification = this.specification();
        }
        return instance;
    }

    /**
     * Define the reference of the domain element that was subject of this query execution.
     *
     * @param ref A domain object reference.
     */
    public void setQueriedModelElementRef(EntityReference ref) {
        this.queriedModelElementRef = ref;
    }

    /**
     * Define collection of contributors to the definition of this event, based on unmodifiable attributes.
     *
     * @param specification Description element relative to this query event.
     */
    public void setSpecification(Collection<Attribute> specification) {
        this.specification = specification;
    }

    /**
     * Get the reference of the domain element that was subject of query.
     *
     * @return A reference immutable version, or null.
     * @throws ImmutabilityException When impossible return of immutable version.
     */
    public EntityReference queriedModelElementReference() throws ImmutabilityException {
        EntityReference r = null;
        if (this.queriedModelElementRef != null)
            r = (EntityReference) this.queriedModelElementRef.immutable();
        return r;
    }

    /**
     * Define the reference of a previous event that causing the publication of
     * this event.
     *
     * @param ref A reference (e.g regarding an event which was treated by a
     *            domain object to treat) or null.
     */
    public void setPriorCommandRef(EntityReference ref) {
        this.priorCommandRef = ref;
    }

    /**
     * Get the reference of the event that was previous to this query.
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
            return EventSpecification.findSpecificationByName(CORRELATION_ID, this.specification);
        }
        return null;
    }

    /**
     * This implementation method create and store a new attribute based on CORRELATION_ID name into the specification of this command.
     *
     * @param eventIdentifier Mandatory defined identifier. None assignment when not defined or empty parameter.
     */
    @Override
    public void assignCorrelationId(String eventIdentifier) {
        if (eventIdentifier != null && !eventIdentifier.isEmpty()) {
            // Create and store attribute dedicated to correlation identifier
            appendSpecification(new Attribute(CORRELATION_ID, eventIdentifier));
        }
    }

}
