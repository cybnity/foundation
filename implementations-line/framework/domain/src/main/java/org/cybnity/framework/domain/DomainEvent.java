package org.cybnity.framework.domain;

import java.time.OffsetDateTime;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Evaluations;
import org.cybnity.framework.immutable.IHistoricalFact;
import org.cybnity.framework.immutable.IReferenceable;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.IdentifiableFact;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Determine something that has happened in the system (e.g typically as a
 * result of a command, or a change observed regarding a bounded context). The
 * facts transmitted, treated and recorded as events can be tracked. The
 * interactions between the components of a domain and users are allowed via
 * immutable events.
 * 
 * By convention, a domain event is named according to <<EventType>><<Fact
 * State>> (e.g OrderConfirmed).
 * 
 * An event can be used to read one or several informations of a system, to
 * change a status of a domain model or to be informed of a changed status
 * detected from an element of a bounded context (e.g data object property).
 * 
 * Related patterns: Segregration principle between events that are responsible
 * of write actions (e.g CommandEvent) and others that are responsible of read
 * requests (e.g Query) via Command and Query Responsibility Segregation (CQRS)
 * pattern.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class DomainEvent implements IHistoricalFact, IdentifiableFact, IVersionable, IReferenceable {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = 1L;

    /**
     * As event name reflect the past nature of the occurence, an event is not
     * occuring now but it occured previously. This property indicates when the
     * event occured.
     * 
     */
    protected OffsetDateTime occuredOn;

    /**
     * Unique identifying information of this event.
     */
    protected Entity identifiedBy;

    /**
     * Default constructor of unidentifiable event.
     */
    public DomainEvent() {
	// Create immutable time of this event creation
	this.occuredOn = OffsetDateTime.now();
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
     * Get a immutable copy of the original entity of this event.
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
     * This method has the same contract as valueEquality() method in that all
     * values that are functionally equal also produce equal hash code value. This
     * method is called by default hashCode() method of this ValueObject instance
     * and shall provide the list of values contributing to define the unicity of
     * this instance (e.g also used for valueEquality() comparison).
     * 
     * @return The unique functional values used to idenfity uniquely this instance.
     *         Or empty array.
     */
    @Override
    public String[] valueHashCodeContributors() {
	try {
	    return new String[] { /** Based only on identifier value **/
		    (String) this.identified().value() };
	} catch (ImmutabilityException ie) {
	    return new String[] {};
	}
    }

    /**
     * Redefined hash code calculation method which include the functional contents
     * hash code values into the returned number.
     */
    @Override
    public int hashCode() {
	// Read the contribution values of functional equality regarding this instance
	String[] functionalValues = valueHashCodeContributors();
	int hashCodeValue = +(169065 * 179);
	if (functionalValues != null && functionalValues.length > 0) {
	    for (String s : functionalValues) {
		if (s != null) {
		    hashCodeValue = +s.hashCode();
		}
	    }
	} else {
	    // Keep standard hashcode value calculation default implementation
	    hashCodeValue = super.hashCode();
	}
	return hashCodeValue;
    }

    /**
     * Redefine the comparison of this event with another based on the identifier is
     * known.
     * 
     * @param fact To compare.
     * @return True if this fact is based on the same identifier(s) as the fact
     *         argument; false otherwise.
     */
    @Override
    public boolean equals(Object event) {
	if (event == this)
	    return true;
	if (event != null && IdentifiableFact.class.isAssignableFrom(event.getClass())) {
	    try {
		// Compare equality based on each instance's identifier (unique or based on
		// identifying informations combination)
		return Evaluations.isIdentifiedEquals(this, (IdentifiableFact) event);
	    } catch (ImmutabilityException ie) {
		// Impossible creation of immutable version of identifier
		// Log problem of implementation
	    }
	}
	return false;
    }

    /**
     * Default implementation of even time when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
	// Return copy of the fact time
	return OffsetDateTime.parse(this.occuredOn.toString());
    }

    @Override
    public EntityReference reference() throws ImmutabilityException {
	try {
	    if (this.getIdentifiedBy() != null) {
		return new EntityReference((Entity) this.getIdentifiedBy().immutable(),
			/* Unknown external relation with the caller of this method */ null, null);
	    }
	    return null;
	} catch (Exception e) {
	    throw new ImmutabilityException(e);
	}
    }

}
