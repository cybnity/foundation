package org.cybnity.framework.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.IAttribute;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Standard and common child fact implementation class.
 * <p>
 * Can be extended as a root aggregate object.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@class")
public class CommonChildFactImpl extends ChildFact implements HydrationCapability {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(CommonChildFactImpl.class).hashCode();

    /**
     * Logger singleton.
     */
    private transient Logger logger;

    /**
     * Stream of changes relative to this instance.
     */
    private final List<DomainEvent> changeHistory = new LinkedList<>();

    /**
     * Attribute type managed via command event allowing change of an aggregate, and/or allowing notification of information changed via a promoted event type.
     */
    public enum Attribute implements IAttribute {
        /**
         * Identifier value of origin predecessor.
         */
        PARENT_REFERENCE_ID,

        /**
         * Identifier of the aggregate.
         */
        IDENTIFIER,

        /**
         * Date of aggregate creation.
         */
        OCCURRED_AT
    }

    /**
     * Commit version of this instance based on the last change identifier.
     */
    private String commitVersion;

    /**
     * Default constructor.
     *
     * @param predecessor Mandatory parent of this child entity.
     * @param id          Unique and optional identifier of this entity.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When a problem of immutability is occurred.
     *                                  When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public CommonChildFactImpl(Entity predecessor, Identifier id) throws IllegalArgumentException {
        super(predecessor, id);
    }

    /**
     * Specific partial constructor.
     *
     * @param predecessor Mandatory parent of this child entity.
     * @param identifiers Optional set of identifiers of this entity, that contains
     *                    non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value. When
     *                                  predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public CommonChildFactImpl(Entity predecessor, LinkedHashSet<Identifier> identifiers)
            throws IllegalArgumentException {
        super(predecessor, identifiers);
    }

    /**
     * Prepare a common new instance of change event.
     *
     * @param changeType Mandatory type of change.
     * @return A change event initialized and including changed model element reference (aggregate root entity reference), specification about predecessor entity reference
     * @throws IllegalArgumentException When mandatory any parameter is missing.
     * @throws ImmutabilityException    When impossible read of parent reference immutable version.
     */
    protected final ConcreteDomainChangeEvent prepareChangeEventInstance(IEventType changeType) throws IllegalArgumentException, ImmutabilityException {
        if (changeType == null) throw new IllegalArgumentException("changeType parameter is required!");
        // Add a change event into the history
        // Check if change event is about a persistent identifiable aggregated
        Identifier uid = this.identified();
        ConcreteDomainChangeEvent changeEvt = new ConcreteDomainChangeEvent( /* new technical identifier of the change event fact */
                new DomainEntity(IdentifierStringBased.generate(/* this created instance id as salt */ (uid != null) ? String.valueOf(this.identified().value().hashCode()) : null))
                , /* Type of change committed */ changeType.name());

        EntityReference rootRef = this.root();
        if (rootRef != null)
            changeEvt.setChangedModelElementRef(rootRef); // Origin domain model object changed

        // Add mandatory description regarding the fact basic definition attributes
        changeEvt.setChangeSourcePredecessorReferenceId(this.parent().identified());
        changeEvt.setChangeSourceIdentifier(this.identified()); // Origin domain model object changed
        changeEvt.setChangeSourceOccurredAt(this.occurredAt);
        return changeEvt;
    }

    /**
     * Get the identity of this child fact when existing.
     *
     * @return A domain entity identity instance based on current identifier of fact (as an aggregate root). Null when not identifier defined regarding this fact.
     */
    protected final DomainEntity rootEntity() {
        // Read the identity of this root aggregate domain object
        Identifier id = this.identified();
        DomainEntity aggregateRootEntity = null;
        if (id != null) {
            aggregateRootEntity = new DomainEntity(id);
        } // Else it's an aggregate representing a dynamic domain boundary without
        // persistence capability
        return aggregateRootEntity;
    }

    /**
     * Get the root entity reference.
     *
     * @return Reference to this fact (considered as root entity).
     * @throws ImmutabilityException When impossible read of copy regarding this domain entity.
     */
    protected EntityReference root() throws ImmutabilityException {
        // Read the identity of this root aggregate domain object
        DomainEntity aggregateRootEntity = rootEntity();
        EntityReference ref = null;
        if (aggregateRootEntity != null) {
            // Build an identification reference
            ref = aggregateRootEntity.reference();
        } // Else it's an aggregate representing a dynamic domain boundary without
        // persistence capability
        return ref;
    }

    /**
     * Get logger regarding this instance type.
     *
     * @return A logger singleton.
     */
    protected final Logger logger() {
        if (logger == null) {
            this.logger = Logger.getLogger(this.getClass().getName());
        }
        return logger;
    }

    /**
     * This method implementation only set the commit version of this instance based on the change event's identifier (considered as commit version of this instance state).
     * It can be redefined by extended class to apply a change on this instance's attributes according to the type of change event detected.
     * A "When" handling approach method is supported by this instance type based on change type identified, that can be developed as strategy pattern of update to the targeted attribute by the change operation.
     *
     * @param change Mandatory change to apply on subject according to the change type (e.g attribute add, upgrade, delete operation).
     * @throws IllegalArgumentException When missing required parameter.
     */
    @Override
    public void mutateWhen(DomainEvent change) throws IllegalArgumentException {
        if (change == null) throw new IllegalArgumentException("change parameter is required!");
        try {
            // Set the commit version of this instance as equals to the latest change
            setCommitVersion(change);
        } catch (ImmutabilityException ie) {
            logger().log(Level.SEVERE, "Impossible mutation of commitVersion attribute when history's change event identifier is unknown!", ie);
        }
    }

    /**
     * Read the domain events provided by the stream as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial event stream range is submitted.
     *
     * @param history Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    @Override
    public final void replayEvents(EventStream history) {
        if (history != null) {
            mutateWhen(history.getEvents());
        }
    }

    /**
     * Read the domain events as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial changes history range is submitted.
     * Automatically, this method update the commitVersion of this instance with last change's identifier value (e.g considered like committed id).
     *
     * @param changes Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    protected final void mutateWhen(List<DomainEvent> changes) {
        if (changes != null) {
            for (DomainEvent evt : changes) {
                mutateWhen(evt);
            }
        }
    }

    /**
     * Read the change events as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial changes history range is submitted.
     * Automatically, this method update the commitVersion of this instance with last change's identifier value (e.g considered like committed id).
     *
     * @param changesHistory Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    protected final void mutate(List<DomainEvent> changesHistory) {
        if (changesHistory != null) {
            // Rehydrate its status for events history
            for (DomainEvent changeFact : changesHistory) {
                if (changeFact != null) {
                    // Apply rehydration of all life historized attributes changes on prepared instance
                    mutateWhen(changeFact);
                }
            }
        }
    }

    /**
     * Get the change history of changes known regarding this instance.
     *
     * @return A list of change or empty list.
     */
    public final List<DomainEvent> changeEvents() {
        return this.changeHistory;
    }

    /**
     * Add a change event into this instance lifecycle history.
     * Make sense to be called by any instance method that modify an attribute that allow to identify the performed change during this instance life.
     * Automatically, this method update the commitVersion of this instance with the change's identifier value (e.g considered like committed id).
     *
     * @param change To add in history. Ignored when null.
     * @throws ImmutabilityException When impossible read of the change identifier required to define the commit version to assign on this fact instance.
     */
    protected final void addChangeEvent(DomainEvent change) throws ImmutabilityException {
        if (change != null) {
            // Add in history
            this.changeHistory.add(change);
            // Set the commit version of this instance as equals to the latest change
            setCommitVersion(change);
        }
    }

    /**
     * Update the commit version relative to this fact.
     * This method read the event identifier and store it as commit version when domain event's identifier is known.
     *
     * @param basedOn Change event that is considered like generator of commit on this instance.
     * @throws ImmutabilityException When problem of event read.
     */
    protected void setCommitVersion(DomainEvent basedOn) throws ImmutabilityException {
        if (basedOn != null) {
            // Set the commit version of this instance as equals to the latest change
            Identifier id = basedOn.identified();
            if (id != null)
                commitVersion = id.value().toString();
        }
    }

    /**
     * Get commit version regarding this instance.
     *
     * @return A version identifier equals to the latest change event identifier which modified this instance.
     */
    public String getCommitVersion() {
        return String.valueOf(this.commitVersion);
    }

    /**
     * Implement the generation of version hash regarding the current class type instance, according
     * to a concrete strategy utility service.
     *
     * @return String version of this instance class type (based on canonical version hash).
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    @Override
    public Identifier identified() {
        Identifier id = null;
        try {
            id = IdentifierStringBased.build(this.identifiers());
        } catch (Exception e) {
            // When none identifier defined regarding this child fact (e.g boundary of
            // domain aggregate without persistence capability that doesn't need to be
            // identified in long-time period)
        }
        return id;
    }

    /**
     * Get immutable version without change events history.
     *
     * @return Immutable version without the changes events list potentially existing in this instance lifecycle history.
     * @throws ImmutabilityException When impossible read of immutable versions copy regarding identifiers of this instance.
     */
    @Override
    public Serializable immutable() throws ImmutabilityException {
        LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
        CommonChildFactImpl copy = new CommonChildFactImpl(this.parent(), ids);
        // Complete with additional attributes of this complex object
        copy.occurredAt = this.occurredAt();
        copy.commitVersion = this.getCommitVersion();
        copy.changeEvents().addAll(Collections.unmodifiableCollection(this.changeEvents()));
        return copy;
    }

    /**
     * Define the generation rule of identifier based on original child id name or
     * BaseConstants.IDENTIFIER_ID.name().
     */
    @Override
    protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
            throws IllegalArgumentException {
        return Predecessors.generateIdentifierPredecessorBased(predecessor, childOriginalId);
    }

    /**
     * Define the generation rule of identifier based on original child identifiers
     * name or BaseConstants.IDENTIFIER_ID.name().
     */
    @Override
    protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Collection<Identifier> childOriginalIds)
            throws IllegalArgumentException {
        return Predecessors.generateIdentifierPredecessorBased(predecessor, childOriginalIds);
    }
}
