package org.cybnity.framework.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Evaluations;
import org.cybnity.framework.immutable.IReferenceable;
import org.cybnity.framework.immutable.IdentifiableFact;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Imperative element that is a request for the system to perform a task of
 * action. Both the sender and the receiver of a comman should be in the same
 * bounded context.
 * 
 * A command is identifiable.
 * 
 * Each command is typically sent to a specific recipient (generally an
 * aggregate instance) that is handled to perform the requested action.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class Command implements IdentifiableFact, IVersionable, Serializable, IReferenceable {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifying information of this event.
     */
    protected Entity identifiedBy;

    /**
     * As event name reflect the past nature of the occurence, an event is not
     * occuring now but it occured previously. This property indicates when the
     * event occured.
     * 
     */
    protected OffsetDateTime occuredOn;

    /**
     * Default constructor of unidentifiable event.
     */
    public Command() {
	// Create immutable time of this event creation
	this.occuredOn = OffsetDateTime.now();
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
     * Get the identification element regarding this event, when it's an
     * identifiable event.
     * 
     * @return Immutable instance of unique identifier of this event, or null.
     */
    @Override
    public Identifier identified() {
	if (this.identifiedBy != null) {
	    try {
		return (Identifier) this.identifiedBy.identified().immutable();
	    } catch (ImmutabilityException ce) {
		// TODO: add runtime log to the LogRegistry if defined
	    }
	}
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
	} catch (Exception ie) {
	    // In case of null pointer exception regarding unknown identifier command
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
		// Impossible creatiopn of immutable version of identifier
		// Log problem
	    }
	}
	return false;
    }

    /**
     * Default implementation of even time when it was created.
     */
    public OffsetDateTime occurredOn() {
	// Return copy of the fact time
	return OffsetDateTime.parse(this.occuredOn.toString());
    }

    @Override
    public EntityReference reference() throws ImmutabilityException {
	try {
	    if (this.identifiedBy != null) {
		return new EntityReference((Entity) this.identifiedBy.immutable(),
			/* Unknown external relation with the caller of this method */ null, null);
	    }
	    return null;
	} catch (Exception e) {
	    throw new ImmutabilityException(e);
	}
    }
}
