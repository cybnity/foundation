package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.DomainEventType;
import org.cybnity.framework.domain.event.IAttribute;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.LinkedHashSet;
import java.util.logging.Level;

/**
 * Represents a scope of information providing attributes and/or capabilities
 * as a complex domain object.
 * <p>
 * An aggregate root of the process entity domain is defined via immutable
 * attributes (e.g ValueObject, EntityReference of other domains' objects,
 * ChildFact historical and identified fact) and/or mutable attributes (e.g
 * MutableProperty objects).
 * <p>
 * An aggregate root instance can be persistent (e.g saving a state of mutable
 * properties as a version of a scope relative to value objects in a relation
 * that make sense for a domain topic) and referencable (e.g EntityReference
 * provided), or can be only a dynamic scope of current version of immutable
 * value objects without need of state save (e.g temporary aggregate root is
 * only generated on-fly to maintain capability to be recontacted during use,
 * but that is not persisted in infrastructure layer).
 * <p>
 * An entity defining the aggregate root allow to uniquely identify a persistent
 * aggregate.
 * <p>
 * For example, an identifiable aggregate scope can be retrieved from an
 * original unique identifier defining during its instantiation (persistence
 * capable) or can generate on-fly an identifier of the scope dynamically (e.g
 * aggregation of multiple identifier of referenced instances of its represented
 * scope).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public abstract class Aggregate extends CommonChildFactImpl implements IAggregate {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(Aggregate.class).hashCode();

    /**
     * Attribute type managed via command event allowing change of an aggregate, and/or allowing notification of information changed via a promoted event type.
     */
    public enum Attribute implements IAttribute {
        /**
         * Identifier value of origin predecessor.
         */
        PREDECESSOR_REFERENCE_ID;
    }

    /**
     * Default constructor.
     *
     * @param predecessor Mandatory parent of this root aggregate entity.
     * @param id          Unique and optional identifier of this entity. For
     *                    example, identifier is required when the aggregate shall
     *                    be persistent. Else, can be without identity when not
     *                    persistent aggregate.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When a problem of immutability is occurred.
     *                                  When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Aggregate(Entity predecessor, Identifier id) throws IllegalArgumentException {
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
     *
     * @param predecessor Mandatory parent of this root aggregate entity.
     * @param identifiers Optional set of identifiers of this entity, that contains
     *                    non-duplicable elements. For example, identifier is
     *                    required when the aggregate shall be persistent. Else, can
     *                    be without identity when not persistent aggregate.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value. When
     *                                  predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Aggregate(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
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
     * Prepare a new instance of change event.
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
        // Add gap description regarding only changed information
        changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.PREDECESSOR_REFERENCE_ID.name(), /* Serialized predecessor identifier value */ this.parent().identified().value().toString()));
        return changeEvt;
    }


    @Override
    public EntityReference root() throws ImmutabilityException {
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
     * Get the identity of this aggregate when existing.
     *
     * @return A domain entity identity instance based on current identifier of the
     * aggregate root. Null when not identifier defined regarding this
     * aggregate.
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
}
