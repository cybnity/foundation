package org.cybnity.framework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.time.OffsetDateTime;

/**
 * Determine something that has happened in the system (e.g typically as a
 * result of a command, or a change observed regarding a bounded context). The
 * facts transmitted, treated and recorded as events can be tracked. The
 * interactions between the components of a domain and users are allowed via
 * immutable events.
 * <p>
 * By convention, a domain event is named according to EventTypeFactState (e.g OrderConfirmed).
 * <p>
 * An event can be used to read one or many data of a system, to
 * change a status of a domain model or to be informed of a changed status
 * detected from an element of a bounded context (e.g data object property).
 * <p>
 * Related patterns: Segregation principle between events that have responsibility
 * of write actions (e.g CommandEvent) and others that are in charge of read
 * requests (e.g Query) via Command and Query Responsibility Segregation (CQRS)
 * pattern.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@class")
@JsonSubTypes({@JsonSubTypes.Type(value = ConcreteDomainChangeEvent.class, name = "ConcreteDomainChangeEvent")})
public abstract class DomainEvent implements IHistoricalFact, IdentifiableFact, IReferenceable, IDescribed {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(DomainEvent.class).hashCode();

    /**
     * As event name reflect the past nature of the occurrence, an event is not
     * occurring now, but it occurred previously. This property indicates when the
     * event occurred.
     */
    protected OffsetDateTime occurredOn;

    /**
     * Unique identifying information of this event.
     */
    private Entity identifiedBy;

    /**
     * Default constructor of unidentifiable event.
     */
    public DomainEvent() {
        // Create immutable time of this event creation
        this.occurredOn = OffsetDateTime.now();
    }

    /**
     * Default constructor of an identifiable event.
     *
     * @param identifiedBy Optional unique identity of this event.
     */
    public DomainEvent(Entity identifiedBy) {
        this();
        this.identifiedBy = identifiedBy;
    }

    /**
     * Get an immutable copy of the original entity of this event.
     *
     * @return Identity of this event, or null.
     * @throws ImmutabilityException when impossible creation of immutable version of identifier.
     */
    public Entity getIdentifiedBy() throws ImmutabilityException {
        if (this.identifiedBy != null) {
            return (Entity) identifiedBy.immutable();
        }
        return null;
    }

    /**
     * Get the identification element regarding this event, when it's an
     * identifiable event.
     *
     * @return Immutable instance of unique identifier of this event, or null.
     * @throws ImmutabilityException If impossible identifier duplication.
     */
    @Override
    public Identifier identified() throws ImmutabilityException {
        Entity entity = getIdentifiedBy();
        if (entity != null)
            return entity.identified();
        return null;
    }

    /**
     * This method provide the list of values contributing to define the unicity of
     * this instance (e.g also used for hashCode() comparison).
     *
     * @return The unique functional attributes (value(), name()) used to identify
     * uniquely this instance. Or empty array.
     */
    @Override
    public String[] valueHashCodeContributors() {
        try {
            Identifier id = this.identified();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SerializationFormat.DATE_FORMAT_PATTERN);
            return new String[]{
                    /* Type of identity */
                    id.name(),
                    /* Hashed version of this event identifier */
                    Integer.toString(id.value().hashCode()),
                    /* Specific time when this command occurred */
                    occurredOn.toString()
                    //formatter.format(occurredOn)
            };
        } catch (ImmutabilityException ie) {
            return new String[]{};
        }
    }

    /**
     * Redefined hash code calculation method which include the functional contents
     * hash code values into the returned number.
     */
    @Override
    public int hashCode() {
        return new EventHashingCapability().getHashCode(this);
    }

    /**
     * Redefine the comparison of this event with another based on the identifier is
     * known.
     *
     * @param event To compare.
     * @return True if this fact is based on the same identifier(s) as the fact
     * argument; false otherwise.
     */
    @Override
    public boolean equals(Object event) {
        try {
            return new EventComparisonCapability().isEquals(event, this);
        } catch (ImmutabilityException ie) {
            // Impossible creation of immutable version of identifier
            // TODO: create a problem log
        }
        return false;
    }

    /**
     * Default implementation of even time when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
        // Return copy of the fact time
        return OffsetDateTime.parse(this.occurredOn.toString());
    }

    @Override
    public EntityReference reference() throws ImmutabilityException {
        try {
            if (this.identifiedBy != null) {
                return new EntityReference((Entity) this.identifiedBy.immutable(),
                        /* Unknown external relation with the caller of this method */ null, null, null);
            }
            return null;
        } catch (Exception e) {
            throw new ImmutabilityException(e);
        }
    }

    /**
     * Get correlation identifier when existing.
     *
     * @return A correlation identifier of origin cause (e.g command event source generating this event) regarding this event. Else null.
     */
    public abstract Attribute correlationId();

}
