package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.DomainEntity;
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
 * It's a Command event of CQRS pattern.
 *
 * @author olivier
 */
@JsonTypeName("Command")
public class ConcreteCommandEvent extends Command {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ConcreteCommandEvent.class).hashCode();

    /**
     * Identify the original event reference that was previous source of this command
     * publication.
     */
    @JsonProperty
    private EntityReference priorCommandRef;

    /**
     * Identify the element of the domain model which was subject of command.
     */
    @JsonProperty
    private EntityReference changedModelElementRef;

    /**
     * Collection of contributor to the definition of this event, based on
     * unmodifiable attributes.
     */
    @JsonProperty
    protected Collection<Attribute> specification;

    /**
     * Constructor usable by binding framework allowing mapping of instance.
     */
    @JsonCreator
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

    public ConcreteCommandEvent(Entity identifiedBy, String eventType) {
        super(identifiedBy);
        if (!"".equals(eventType)) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType));
        }
    }

    /**
     * Factory of a type of command supported by a domain Anti-Corruption Layer API.
     *
     * @param type                   Mandatory type of command event to instantiate.
     * @param identifiedBy           Optional command identity that shall identify the concrete command instance to create.
     * @param definition             Collection of attributes defining the command.
     * @param priorCommandRef        Optional original command reference that was previous source of this command publication.
     * @param changedModelElementRef Optional Identify the element of the domain model which was subject of command.
     * @return Instance of concrete event including all the attributes and standard additional elements.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public ConcreteCommandEvent create(String type, DomainEntity identifiedBy, Collection<Attribute> definition, EntityReference priorCommandRef, EntityReference changedModelElementRef) throws IllegalArgumentException {
        if (type == null || type.isEmpty()) throw new IllegalArgumentException("Type parameter is required!");
        ConcreteCommandEvent command = new ConcreteCommandEvent(identifiedBy, type);
        if (definition != null) {
            // Add command event attributes
            for (Attribute at : definition) {
                command.appendSpecification(at);
            }
        }
        if (priorCommandRef != null) {
            command.setPriorCommandRef(priorCommandRef);
        }
        if (changedModelElementRef != null) {
            command.setChangedModelElementRef(changedModelElementRef);
        }
        return command;
    }

    @JsonIgnore
    @Override
    public Serializable immutable() throws ImmutabilityException {
        ConcreteCommandEvent instance = new ConcreteCommandEvent(this.getIdentifiedBy());
        instance.occurredOn = this.occurredAt();

        // Add immutable version of each additional attributes hosted by this event
        EntityReference cmdRef = this.priorCommandReference();
        if (cmdRef != null)
            instance.setPriorCommandRef(cmdRef);
        EntityReference subjectRef = this.changedModelElementReference();
        if (subjectRef != null)
            instance.setChangedModelElementRef(subjectRef);
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
    public void setChangedModelElementRef(EntityReference ref) {
        this.changedModelElementRef = ref;
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
     * Get the reference of the event that was previous to this command.
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
        return String.valueOf(serialVersionUID);
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

    @Override
    public Attribute type() {
        if (this.specification != null) {
            // Search optionally and previously defined type
            return EventSpecification.findSpecificationByName(TYPE, this.specification);
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
