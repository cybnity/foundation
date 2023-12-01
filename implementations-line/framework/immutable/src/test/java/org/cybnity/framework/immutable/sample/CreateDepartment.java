package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Example of command regarding a department creation request.
 * 
 * @author olivier
 *
 */
public class CreateDepartment implements IHistoricalFact, IdentifiableFact, IVersionable {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(CreateDepartment.class).hashCode();
    private Entity identifiedBy;
    private OffsetDateTime occuredAt;
    private Department updatedVersion;

    public CreateDepartment(Entity identity, Department changed) {
	this.identifiedBy = identity;
	this.updatedVersion = changed;
	this.occuredAt = OffsetDateTime.now();
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	CreateDepartment instance = new CreateDepartment(this.identifiedBy, this.updatedVersion);
	instance.occuredAt = this.occuredAt;
	return instance;
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
    public Identifier identified() throws ImmutabilityException {
	return this.identifiedBy.identified();
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

    @Override
    public OffsetDateTime occurredAt() {
	return this.occuredAt;
    }
}