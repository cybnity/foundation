package org.cybnity.framework.domain.event;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic event regarding a change occurred on a topic relative to a domain.
 *
 * @author olivier
 */
@JsonTypeName("ConcreteDomainChangeEvent")
@JsonSubTypes({@JsonSubTypes.Type(value = ProcessingUnitPresenceAnnounced.class, name = "ProcessingUnitPresenceAnnounced")})
public class ConcreteDomainChangeEvent extends DomainEvent implements HydrationAttributeProvider {

    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ConcreteDomainChangeEvent.class).hashCode();

    @JsonIgnore
    private transient final Logger logger = Logger.getLogger(ConcreteDomainChangeEvent.class.getName());

    /**
     * Standard type of the attribute specifying this event type based on a logical
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
    protected Collection<Attribute> specification;

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

    public ConcreteDomainChangeEvent(String eventType) {
        this();
        if (eventType != null && !eventType.isEmpty()) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType));
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
        if (!eventType.isEmpty()) {
            // Add type into specification attributes
            appendSpecification(new Attribute(TYPE, eventType));
        }
    }

    /**
     * Factory of a type of domain event supported by the domain Anti-Corruption Layer API.
     *
     * @param type                   Mandatory type of domain event to instantiate.
     * @param identifiedBy           Optional event identity that shall identify the concrete event instance to create.
     * @param definition             Collection of attributes defining the event.
     * @param priorCommandRef        Optional original event reference that was previous source of this event
     *                               publication
     * @param changedModelElementRef Optional Identify the element of the domain model which was subject of domain event.
     * @return Instance of concrete event including all the attributes and standard additional elements.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    static public ConcreteDomainChangeEvent create(String type, DomainEntity identifiedBy, Collection<Attribute> definition, EntityReference priorCommandRef, EntityReference changedModelElementRef) throws IllegalArgumentException {
        if (type == null || type.isEmpty()) throw new IllegalArgumentException("Type parameter is required!");
        ConcreteDomainChangeEvent event = new ConcreteDomainChangeEvent(identifiedBy, type);
        if (definition != null) {
            // Add change event attributes
            for (Attribute at : definition) {
                event.appendSpecification(at);
            }
        }
        if (priorCommandRef != null) {
            event.setChangeCommandRef(priorCommandRef);
        }
        if (changedModelElementRef != null) {
            event.setChangedModelElementRef(changedModelElementRef);
        }
        return event;
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
            return EventSpecification.findSpecificationByName(Command.CORRELATION_ID, this.specification);
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

    @Override
    public Identifier changeSourcePredecessorReferenceId() {
        Attribute at = EventSpecification.findSpecificationByName(CommonChildFactImpl.Attribute.PARENT_REFERENCE_ID.name(), this.specification());
        if (at != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                return mapper.readValue(at.value(), Identifier.class);
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
        return null;
    }

    @Override
    public void setChangeSourcePredecessorReferenceId(Identifier id) {
        if (id != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                appendSpecification(new org.cybnity.framework.domain.Attribute(CommonChildFactImpl.Attribute.PARENT_REFERENCE_ID.name(), /* Serialized predecessor identifier value */ mapper.writeValueAsString(id)));
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
    }

    @Override
    public Identifier changeSourceIdentifier() {
        Attribute at = EventSpecification.findSpecificationByName(CommonChildFactImpl.Attribute.IDENTIFIER.name(), this.specification());
        if (at != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                return mapper.readValue(at.value(), Identifier.class);
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
        return null;
    }

    @Override
    public void setChangeSourceIdentifier(Identifier id) {
        if (id != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                appendSpecification(new org.cybnity.framework.domain.Attribute(CommonChildFactImpl.Attribute.IDENTIFIER.name(), mapper.writeValueAsString(id)));
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
    }

    @Override
    public OffsetDateTime changeSourceOccurredAt() {
        Attribute at = EventSpecification.findSpecificationByName(CommonChildFactImpl.Attribute.OCCURRED_AT.name(), this.specification());
        if (at != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                return mapper.readValue(at.value(), OffsetDateTime.class);
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
        return null;
    }

    @Override
    public void setChangeSourceOccurredAt(OffsetDateTime date) {
        if (date != null) {
            try {
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                appendSpecification(new org.cybnity.framework.domain.Attribute(CommonChildFactImpl.Attribute.OCCURRED_AT.name(), mapper.writeValueAsString(date)));
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, jpe.getMessage(), jpe);
            }
        }
    }
}
