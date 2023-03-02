package org.cybnity.framework.immutable;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;

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
public abstract class MutableProperty implements IHistoricalFact {

    private static final long serialVersionUID = 1L;

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
     * 
     */
    protected HashSet<MutableProperty> prior;

    /**
     * When this fact version was created or observed regarding the historized
     * topic.
     */
    protected OffsetDateTime changedAt;

    /**
     * Identify this property value had been confirmed (e.g during a merging
     * conflict resolultion act decided by a user) as official current version.
     * {@link org.cybnity.framework.immutable.HistoryState.Committed} by default for
     * any new instance of new instantiated property.
     */
    private HistoryState historyStatus = HistoryState.COMMITTED;

    /**
     * Default constructor with automatic initialization of an empty value set
     * (prior chain).
     * 
     * @param propertyOwner        Mandatory entity which is owner of this mutable
     *                             property chain.
     * @param propertyCurrentValue Mandatory current version of value(s) regarding
     *                             the property. Support included keys with null
     *                             value.
     * @param status               Optional state of this property version. If null,
     *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
     *                             is defined as default state.
     * @throws IllegalArgumentException When mandatory parameter is missing, or when
     *                                  cant' be cloned regarding immutable entity
     *                                  parameter.
     */
    public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status)
	    throws IllegalArgumentException {
	if (propertyOwner == null)
	    throw new IllegalArgumentException("PropertyOwner mandatory parameter is missing!");
	if (propertyCurrentValue == null || propertyCurrentValue.isEmpty())
	    throw new IllegalArgumentException("PropertyCurrentValue mandatory parameter is missing!");
	try {
	    this.entity = (Entity) propertyOwner.immutable();
	    // Set of prior versions is empty by default
	    this.prior = new HashSet<>();
	    // Set the current states of changed values regarding this property version
	    this.value = propertyCurrentValue;
	    if (status != null)
		this.historyStatus = status;
	    // Create immutable time of this property changed version
	    this.changedAt = OffsetDateTime.now();
	} catch (ImmutabilityException ce) {
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
     * @param status               Optional state of this property version. If null,
     *                             {@link org.cybnity.framework.immutable.HistoryState.Committed}
     *                             is defined as default state.
     * @param predecessors         Optional original instances (previous versions)
     *                             that were to consider in the history chain,
     *                             regarding this property and that were identified
     *                             as property's original states which had been
     *                             changed. It's possible that new instance (e.g in
     *                             {@link org.cybnity.framework.immutable.HistoryState.Merged}
     *                             status) is based on several merged versions of
     *                             previous property's states (e.g in case of
     *                             concurrenlty changed version with need of
     *                             conflict resolution). Ignored if null.
     * @throws IllegalArgumentException When mandatory parameter is missing; when
     *                                  cant' be cloned regarding immutable entity
     *                                  parameter.
     */
    public MutableProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue, HistoryState status,
	    MutableProperty... predecessors) throws IllegalArgumentException {
	// Initialize instance as default without previous versions of property's values
	this(propertyOwner, propertyCurrentValue, status);
	if (predecessors != null) {
	    // Manage the possible parallel concurrently previous state (e.g regarding
	    // original previous values of this property that were evaluated to dedice this
	    // new value)
	    for (MutableProperty p : predecessors) {
		if (p != null) {
		    this.prior.add(p);
		}
	    }
	}
    }

    /**
     * This property version state (e.g in a situation of concurrent change of
     * predecessor values requiring merging of new status to fix the new value of
     * this one) regarding its anterior versions.
     * 
     * @return Official version of this property.
     *         {@link org.cybnity.framework.immutable.HistoryState.Committed} by
     *         default.
     */
    public HistoryState historyStatus() {
	return historyStatus;
    }

    /**
     * Set the state of this property value as an official version of the history
     * chain, or shall only be saved in history as a concurrent version which had
     * been archived following a merging or reject decision (e.g by a user in front
     * of several potential changes requested concurrently of the prior version).
     * 
     * @param state Status of this version as current version property in front of
     *              potential other parallel/concurrent versions regarding a same
     *              priors.
     */
    public void setHistoryStatus(HistoryState state) {
	this.historyStatus = state;
    }

    /**
     * Default implementation of fact date when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
	// Return immutable value of the fact time
	return this.changedAt;
    }
}
