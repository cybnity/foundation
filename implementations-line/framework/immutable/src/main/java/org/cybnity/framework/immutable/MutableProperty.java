package org.cybnity.framework.immutable;

import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * This represents values that change on a property (simple or complex).
 * 
 * Mutable Property pattern represents changes to properties over time using
 * only immutable facts. It is desirable in a distributed system for nodes to be
 * able to act in isolation, to have the autonomy to change a property without
 * requiring a connection to any other node (e.g on a smartphone that is
 * temporarily disconnected from the server).
 * 
 * It is represented as a fact having the entity as a predecessor and the value
 * as a field. To keep track of changes overtime, it records prior versions in a
 * predecessor set. By convention, the name of the fact appends the property
 * name to the entity name (e.g <<EntityNamePropertyName>>). The set of prio
 * versions is conventionnally called prior. This set is empty for the initial
 * value.
 * 
 * As a user changes the property, the prior set captures only the most recent
 * version.
 * 
 * Related pattern: if the mutable property represents a relationship with
 * another entity, the pattern becomes an
 * ({@link org.cybnity.framework.immutable.EntityReference}).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public abstract class MutableProperty implements HistoricalFact {

    /**
     * The owner of this mutable porperty.
     */
    protected Entity entity;

    /**
     * Current value of the property. Can be unique (e.g about a simple String field
     * of an Entity), but also represents a current version of a combined complex
     * object (e.g aggregation of multiples properties constituing a complex
     * Entity).
     * 
     * The key is the property name, and the value is its current captured recent
     * value version.
     */
    protected HashMap<String, Object> value;

    /**
     * Where the changed versions are historized as predecessors. In the chain of
     * version, each prior item points back to its immediate predecessor.
     * 
     * When a node computes a tree with multiples leaves, it recognizes a concurrent
     * change. In this situation, the application will typically present all leaves
     * as candidate values where each one represents a value that was concurrently
     * set for the property and has not been superseded. The user can select among
     * the candidate values and resolve the dispute (can alos be accomplished via a
     * simple function over the leaves, such as a maximum equals to a several-way
     * merge).
     * 
     * Facts are only generated as a result of a user's decision. When the user
     * changes a property from a concurrent state, the system includes all of the
     * leaves of the tree in the newt fact's prior set (value attribute).
     */
    protected LinkedList<MutableProperty> prior;

    /**
     * When this fact was created or observed regarding the historized topic.
     */
    protected OffsetDateTime changedAt;

    /**
     * Default constructor with automatic initialization of an empty value set
     * (prior chain).
     * 
     * @param propertyOwner        Mandatory entity which is owner of this mutable
     *                             property chain.
     * @param propertyCurrentValue Mandatory current version of value(s) regarding
     *                             the property. Support included keys with null
     *                             value.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  cant' be cloned regarding its immutable
     *                                  version.
     */
    public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue)
	    throws IllegalArgumentException {
	if (propertyOwner == null)
	    throw new IllegalArgumentException(
		    new InvalidParameterException("PropertyOwner mandatory parameter is missing!"));
	try {
	    this.entity = (Entity) propertyOwner.immutable();
	    // Set of prior versions is empty by default
	    this.prior = new LinkedList<>();
	    // Set the current states of changed values regarding this property version
	    this.value = propertyCurrentValue;
	    // Create immutable time of this property changed version
	    this.changedAt = OffsetDateTime.now();
	} catch (CloneNotSupportedException ce) {
	    throw new IllegalArgumentException(ce);
	}
    }

    /**
     * Constructor of new property value version linked to previous versions of this
     * property.
     * 
     * @param propertyOwner        Mandatory entity which is owner of this mutable
     *                             property chain.
     * @param propertyCurrentValue Mandatory current version of value(s) regarding
     *                             the property. Support included keys with null
     *                             value.
     * @param prior                Optional history of the previous versions of the
     *                             values (history chain) regarding this property.
     *                             Ignored if null.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  cant' be cloned regarding its immutable
     *                                  version.
     */
    public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
	    LinkedList<? extends MutableProperty> prior) throws IllegalArgumentException {
	// Initialize instance as default without previous versions of property's values
	this(propertyOwner, propertyCurrentValue);
	if (prior != null && !prior.isEmpty()) {
	    // Set the defined history
	    this.prior.addAll(prior);
	}
    }

}
