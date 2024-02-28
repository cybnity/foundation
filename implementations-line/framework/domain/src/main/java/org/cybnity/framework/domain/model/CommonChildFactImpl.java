package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.TransformationUtils;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.DomainEventType;
import org.cybnity.framework.domain.event.IAttribute;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
public class CommonChildFactImpl extends ChildFact implements HydrationCapability {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(CommonChildFactImpl.class).hashCode();

    /**
     * Logger singleton.
     */
    private Logger logger;

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
         * Identifiers of the aggregate.
         */
        IDENTIFIERS,

        /**
         * Date of aggregate creation.
         */
        OCCURRED_AT;
    }

    /**
     * Factory of child fact based on changes history (e.g fact creation, change, deletion events) allowing the instance rehydration.
     *
     * @param instanceId     Mandatory unique identifier of the child fact instance to rehydrate.
     * @param changesHistory Mandatory not empty history. History order shall be ascending ordered with the last list element equals to the more young creation event relative to this instance to rehydrate.
     * @throws IllegalArgumentException When mandatory parameter is not valid or empty. When list does not contain identifiable creation event as first list element.
     */
    public static CommonChildFactImpl instanceOf(Identifier instanceId, List<Hydration> changesHistory) throws IllegalArgumentException {
        if (instanceId == null) throw new IllegalArgumentException("instanceId parameter is required!");
        if (changesHistory == null || changesHistory.isEmpty())
            throw new IllegalArgumentException("changesHistory parameter is required and shall be not empty!");
        CommonChildFactImpl fact = null;
        // Get first element as origin creation event (more old event)
        Hydration event = changesHistory.get(0);
        if (event == null) throw new IllegalArgumentException("First history item shall be not null!");
        // Check if the event allow identification of predecessor
        Entity predecessor = event.parent();
        if (predecessor != null) {
            // Recreate instance
            fact = new CommonChildFactImpl(predecessor, instanceId);
            fact.mutate(changesHistory);// Re-hydrate instance
        }
        return fact;
    }

    /**
     * Default constructor.
     * During the construction, a TENANT_CREATED domain event is automatically added to the lifecycle changes history container.
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
        try {
            // Add a change event into the history
            ConcreteDomainChangeEvent changeEvt = prepareChangeEventInstance(DomainEventType.TENANT_CREATED);

            // Add to changes history
            addChangeEvent(changeEvt);
        } catch (ImmutabilityException ie) {
            // Log potential coding problem relative to immutability support
            logger().log(Level.SEVERE, ie.getMessage(), ie);
        }
    }

    /**
     * Specific partial constructor.
     * During the construction, a TENANT_CREATED domain event is automatically added to the lifecycle changes history container.
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
        try {
            // Add a change event into the history
            ConcreteDomainChangeEvent changeEvt = prepareChangeEventInstance(DomainEventType.TENANT_CREATED);

            // Add to changes history
            addChangeEvent(changeEvt);
        } catch (ImmutabilityException ie) {
            // Log potential coding problem relative to immutability support
            logger().log(Level.SEVERE, ie.getMessage(), ie);
        }
    }

    /**
     * Prepare a common new instance of change event.
     *
     * @param changeType Mandatory type of change.
     * @return A change event initialized and including changed model element reference (aggregate root entity reference), specification about predecessor entity reference
     * @throws IllegalArgumentException When mandatory any parameter is missing.
     * @throws ImmutabilityException    When impossible read of parent reference immutable version.
     */
    protected ConcreteDomainChangeEvent prepareChangeEventInstance(DomainEventType changeType) throws IllegalArgumentException, ImmutabilityException {
        if (changeType == null) throw new IllegalArgumentException("changeType parameter is required!");
        // Add a change event into the history
        // Check if change event is about a persistent identifiable aggregated
        Identifier uid = this.identified();
        ConcreteDomainChangeEvent changeEvt = new ConcreteDomainChangeEvent( /* new technical identifier of the change event fact */
                new DomainEntity(IdentifierStringBased.generate(/* this created instance id as salt */ (uid != null) ? String.valueOf(this.identified().value().hashCode()) : null))
                , /* Type of change committed */ changeType);
        EntityReference rootRef = this.root();
        if (rootRef != null)
            changeEvt.setChangedModelElementRef(rootRef); // Origin model object changed

        // Add mandatory description regarding the fact basic definition attributes
        changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.PARENT_REFERENCE_ID.name(), /* Serialized predecessor identifier value */ this.parent().identified().value().toString()));
        changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.IDENTIFIERS.name(),/* Set of basic identification values */ TransformationUtils.convert(this.identifiedBy)));
        changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.OCCURRED_AT.name(), /* Serialized date of occurrence */ TransformationUtils.convert(this.occurredAt)));
        return changeEvt;
    }

    /**
     * Get the identity of this child fact when existing.
     *
     * @return A domain entity identity instance based on current identifier of fact (as an aggregate root). Null when not identifier defined regarding this fact.
     */
    protected DomainEntity rootEntity() {
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
    protected Logger logger() {
        if (logger == null) {
            this.logger = Logger.getLogger(this.getClass().getName());
        }
        return logger;
    }

    /**
     * This method implementation does not make any change on this instance, and shall be redefined by extended class to apply a change on this instance's attributes according to the type of change event detected.
     * A "When" handling approach method is supported by this instance type based on change type identified, that can be developed as strategy pattern of update to the targeted attribute by the change operation.
     *
     * @param change Mandatory change to apply on subject according to the change type (e.g attribute add, upgrade, delete operation).
     * @throws IllegalArgumentException When missing required parameter.
     */
    @Override
    public void mutateWhen(DomainEvent change) throws IllegalArgumentException {
        if (change == null) throw new IllegalArgumentException("change parameter is required!");
        // Do nothing
    }

    /**
     * Read the domain events provided by the stream as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial event stream range is submitted.
     *
     * @param history Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    @Override
    public void replayEvents(EventStream history) {
        if (history != null) {
            this.mutateWhen(history.getEvents());
        }
    }

    /**
     * Read the domain events as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial changes history range is submitted.
     *
     * @param changes Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    final protected void mutateWhen(List<DomainEvent> changes) {
        if (changes != null) {
            for (DomainEvent evt : changes) {
                this.mutateWhen(evt);
            }
        }
    }

    /**
     * Read the change events as known history of changes relative to this fact, and call the mutateWhen(...) method responsible to replay the change on this instance when event is supported.
     * Remember that the instance state is mutated from the point of the latest snapshot forward when a partial changes history range is submitted.
     *
     * @param changesHistory Events which shall be re-executed as committed changes on this instance. Do nothing when null or including empty events list.
     */
    final protected void mutate(List<Hydration> changesHistory) {
        if (changesHistory != null) {
            // Rehydrate its status for events history
            IdentifiableFact changeFact;
            DomainEvent changeEvt;
            for (Hydration hydration : changesHistory) {
                changeFact = hydration.event();
                if (changeFact != null && DomainEvent.class.isAssignableFrom(changeFact.getClass())) {
                    changeEvt = (DomainEvent) changeFact;
                    // Apply rehydration of all life historized attributes changes on prepared instance
                    mutateWhen(changeEvt);
                }
            }
        }
    }

    /**
     * Get the change history of changes known regarding this instance.
     *
     * @return A list of change or empty list.
     */
    public List<DomainEvent> changeEvents() {
        return this.changeHistory;
    }

    /**
     * Add a change event into this instance lifecycle history.
     * Make sense to be called by any instance method that modify an attribute that allow to identify the performed change during this instance life.
     *
     * @param change To add in history. Ignored when null.
     */
    protected void addChangeEvent(DomainEvent change) {
        if (change != null) {
            // Add in history
            this.changeHistory.add(change);
        }
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

    @Override
    public Serializable immutable() throws ImmutabilityException {
        LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
        CommonChildFactImpl copy = new CommonChildFactImpl(this.parent(), ids);
        // Complete with additional attributes of this complex object
        copy.occurredAt = this.occurredAt();
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
