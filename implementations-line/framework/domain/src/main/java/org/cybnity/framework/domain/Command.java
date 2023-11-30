package org.cybnity.framework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cybnity.framework.domain.event.ConcreteCommandEvent;
import org.cybnity.framework.domain.event.ConcreteQueryEvent;
import org.cybnity.framework.domain.event.CorrelationIdFactory;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Imperative element that is a request for the system to perform a task of
 * action. Both the sender and the receiver of a command should be in the same
 * bounded context.
 * <p>
 * A command is identifiable.
 * <p>
 * Each command is typically sent to a specific recipient (generally an
 * aggregate instance) that is handled to perform the requested action.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@class")
@JsonSubTypes({@JsonSubTypes.Type(value = ConcreteCommandEvent.class, name = "Command"), @JsonSubTypes.Type(value = ConcreteQueryEvent.class, name = "Query")})
public abstract class Command implements IHistoricalFact, IdentifiableFact, IReferenceable, IDescribed {

    /**
     * Version of this class type.
     */
    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(Command.class).hashCode();

    /**
     * As event name reflect the past nature of the occurrence, an event is not
     * occurring now, but it occurred previously. This property indicates when the
     * event occurred.
     */
    protected OffsetDateTime occurredOn;

    /**
     * Identifying information of this event.
     */
    protected Entity identifiedBy;

    /**
     * Standard name of the attribute specifying a correlation identifier generated and assigned to this command.
     */
    @JsonIgnore
    public static String CORRELATION_ID = "correlationId";

    /**
     * Default constructor of unidentifiable event.
     */
    public Command() {
        // Create immutable time of this event creation
        this.occurredOn = OffsetDateTime.now();
    }

    /**
     * Default constructor of an identifiable event.
     *
     * @param identifiedBy Optional unique identifier of this event.
     */
    public Command(Entity identifiedBy) {
        this();
        this.identifiedBy = identifiedBy;
    }

    /**
     * Get an immutable copy of the original entity of this event.
     *
     * @return Identity of this event, or null.
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
     * This method get all values that are functionally equal also produce equal
     * hash code value. This method is called by default hashCode() method of this
     * ValueObject instance and shall provide the list of values contributing to
     * define the unicity of this instance (e.g also used for valueEquality()
     * comparison).
     *
     * @return The unique functional values used to identify uniquely this instance.
     * Or empty array.
     */
    @Override
    public String[] valueHashCodeContributors() {
        try {
            Identifier id = this.identified();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SerializationFormat.DATE_FORMAT_PATTERN);
            return new String[]{
                    /* Type of identity */
                    id.name(),
                    /* Hashed version of this command identifier */
                    Integer.toString(id.value().hashCode()),
                    /* Specific time when this command occurred */
                    formatter.format(occurredOn)
            };
        } catch (Exception ie) {
            // In case of null pointer exception regarding unknown identifier command
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
     * @return A correlation identifier assigned to this command. Else null.
     */
    public abstract Attribute correlationId();

    /**
     * Generate and assign correlation identifier to this command.
     *
     * @param salt Optional value to be included into the correlation identifier automatically generated and assigned.
     */
    public void generateCorrelationId(String salt) {
        assignCorrelationId(CorrelationIdFactory.generate(salt));
    }

    /**
     * Assign a correlation identifier to this command.
     * This method is called when the generateCorrelationId(String salt) is executed, and shall manage the storage of the generated correlation identifier into this command.
     *
     * @param eventIdentifier Mandatory defined identifier. None assignment when not defined or empty parameter.
     */
    protected abstract void assignCorrelationId(String eventIdentifier);

}
