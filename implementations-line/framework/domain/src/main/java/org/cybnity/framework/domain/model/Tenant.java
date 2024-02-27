package org.cybnity.framework.domain.model;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.DomainEventType;
import org.cybnity.framework.domain.event.IAttribute;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Represent an organization subscription that allow to define a scope of
 * multi-tenant application regarding a named organization which facilitates the
 * users registrations through invitation.
 * <p>
 * This tenant resolve queries of application data (e.g segregation per
 * organization) and isolation of persistent contents (e.g database structure
 * including key of tenant for each stable; shared database per tenant about
 * each Write/Read model) and/or resources allocation (e.g pool isolation model
 * per shared database relative to the pool proportion user-base and resource
 * usage).
 * <p>
 * Domain root aggregate object.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_3")
public class Tenant extends Aggregate {

    /**
     * Version of this class
     */
    private static final long serialVersionUID = new VersionConcreteStrategy().composeCanonicalVersionHash(Tenant.class)
            .hashCode();

    /**
     * Attribute type managed via command event allowing change of an aggregate, and/or allowing notification of information changed via a promoted event type.
     */
    public enum Attribute implements IAttribute {
        /**
         * Tenant logical label value.
         */
        LABEL,
        /**
         * True or False value regarding the tenant's activity state.
         */
        ACTIVITY_STATUS;
    }

    /**
     * Logical label naming this tenant (e.g business name of a company) that facilitate to resolve queries.
     */
    private TenantDescriptor label;

    /**
     * Current mutable status of activity regarding this tenant.
     */
    private ActivityState activityStatus;


    /**
     * Factory of instance from historized facts (e.g fact creation, change, deletion events) allowing the instance rehydration.
     *
     * @param instanceId     Mandatory unique identifier of the child fact instance to rehydrate.
     * @param changesHistory Mandatory not empty history. History order shall be ascending ordered with the last list element equals to the more young creation event relative to this instance to rehydrate.
     * @throws IllegalArgumentException When mandatory parameter is not valid or empty. When list does not contain identifiable creation event as first list element.
     */
    public static Tenant instanceOf(Identifier instanceId, List<Hydration> changesHistory) throws IllegalArgumentException {
        if (instanceId == null) throw new IllegalArgumentException("instanceId parameter is required!");
        if (changesHistory == null || changesHistory.isEmpty())
            throw new IllegalArgumentException("changesHistory parameter is required and shall be not empty!");
        Tenant fact = null;
        // Get first element as origin creation event (more old event)
        Hydration event = changesHistory.get(0);
        if (event == null) throw new IllegalArgumentException("First history item shall be not null!");
        // Check if the event allow identification of predecessor
        Entity predecessor = event.predecessor();
        if (predecessor != null) {
            // Recreate instance
            fact = new Tenant(predecessor, instanceId);
            // Rehydrate its status for events history
            fact.mutate(changesHistory);// Re-hydrate instance
        }
        return fact;
    }

    /**
     * Default constructor.
     *
     * @param predecessor Mandatory parent of this tenant root aggregate entity.
     * @param id          Optional identifier of this tenant.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When id parameter's name is not equals to
     *                                  BaseConstants.IDENTIFIER_ID. When a problem
     *                                  of immutability is occurred. When
     *                                  predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Tenant(Entity predecessor, Identifier id) throws IllegalArgumentException {
        super(predecessor, id); // Automatic creation event added into history
    }

    /**
     * Default constructor.
     *
     * @param predecessor   Mandatory parent of this tenant root aggregate instance.
     * @param id            Optional identifier of this tenant.
     * @param currentStatus Optional current status of this tenant subscription (e.g
     *                      True when active).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When id parameter's name is not equals to
     *                                  BaseConstants.IDENTIFIER_ID. When a problem
     *                                  of immutability is occurred. When
     *                                  predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Tenant(Entity predecessor, Identifier id, Boolean currentStatus) throws IllegalArgumentException {
        super(predecessor, id);// Automatic creation event added into history
        if (id != null && !BaseConstants.IDENTIFIER_ID.name().equals(id.name()))
            throw new IllegalArgumentException(
                    "id parameter is not valid because identifier name shall be equals to only supported value ("
                            + BaseConstants.IDENTIFIER_ID.name() + ")!");
        if (currentStatus != null) {
            try {
                setStatus(new ActivityState(parent().reference(), currentStatus));
            } catch (ImmutabilityException ie) {
                // Normally shall never arrive
                logger().log(Level.SEVERE, ie.getMessage(), ie);
            }
        }
    }

    /**
     * Specific partial constructor of an identifiable tenant.
     *
     * @param predecessor Mandatory parent of this child tenant.
     * @param identifiers Optional set of identifiers of this entity, that contains
     *                    non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value. When
     *                                  predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    private Tenant(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
        super(predecessor, identifiers); // Automatic creation event added into history
    }

    /**
     * Specific and redefined implementation of change re-hydration.
     *
     * @param change Mandatory change to apply on subject according to the change type (e.g attribute add, upgrade, delete operation).
     * @throws IllegalArgumentException When missing required parameter.
     */
    @Override
    public void mutateWhen(DomainEvent change) throws IllegalArgumentException {
        super.mutateWhen(change);// Execute potential re-hydration of super class
        // Apply local change
        // TODO implementation of change on Tenant according to change event type (command supported) producing this tenant's attribute value modification
    }

    @Override
    public void handle(Command command, IContext ctx) throws IllegalArgumentException {
        throw new IllegalArgumentException("not implemented!");
    }

    @Override
    public Set<String> handledCommandTypeVersions() {
        return null;
    }

    /**
     * Get the current state of activity regarding this tenant.
     *
     * @return A status or null when unknown.
     * @throws ImmutabilityException When problem of instantiation regarding the
     *                               immutable version of the current status.
     */
    public ActivityState status() throws ImmutabilityException {
        ActivityState current = null;
        if (this.activityStatus != null)
            current = (ActivityState) this.activityStatus.immutable();
        return current;
    }

    /**
     * Update the current activity status of this tenant.
     *
     * @param status A status. If existent tenant's status is not included already
     *               in this parameter, this method verify it and add the current
     *               state as previous version to maintain the changes history
     *               regarding the mutable state. If null, the method ignore the
     *               requested change and maintain the already existent state.
     */
    private void setStatus(ActivityState status) {
        if (status != null) {
            if (this.activityStatus != null) {
                // Check that status is already included into the history
                if (!status.changesHistory().contains(this.activityStatus)) {
                    // Add the current status as old (prior) regarding the new version of the status
                    // to save
                    status.changesHistory().add(this.activityStatus);
                    // Current status become last historized status in history chain of new status
                    // to save
                }
            }
            // Replace current version of mutable state of this tenant
            this.activityStatus = status;

            try {
                // Add a change event into the history regarding modified status
                ConcreteDomainChangeEvent changeEvt = prepareChangeEventInstance(DomainEventType.TENANT_CHANGED);
                // Add activity status changed into description of change
                changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.ACTIVITY_STATUS.name(), this.activityStatus.isActive().toString()));
                addChangeEvent(changeEvt); // Add to changes history
            } catch (ImmutabilityException ie) {
                logger().log(Level.SEVERE, ie.getMessage(), ie);
            }
        }
    }

    /**
     * Change the current activity state as active. This method update the history
     * of previous activity states and replace the current state with a new state
     * version.
     *
     * @return A mutable version of the new current state.
     * @throws ImmutabilityException When impossible assigning of this reference as
     *                               owner of an activity state change.
     */
    public MutableProperty activate() throws ImmutabilityException {
        // Create initial status as active
        setStatus(new ActivityState(parent().reference(), Boolean.TRUE, this.activityStatus));
        return status();
    }

    /**
     * Change the current activity state as inactive. This method update the history
     * of previous activity states and replace the current state with a new state
     * version.
     *
     * @return A mutable version of the new current state.
     * @throws ImmutabilityException When impossible assignation of this reference
     *                               as owner of an activity state change.
     */
    public MutableProperty deactivate() throws ImmutabilityException {
        // Create initial status as inactive
        setStatus(new ActivityState(parent.reference(), Boolean.FALSE, this.activityStatus));
        return status();
    }

    /**
     * Define a logical name regarding this tenant when none previously
     * defined. When existing previous label about this tenant, this method
     * make a change on the previous defined name (changes history is saved)
     * to define the new one as current.
     *
     * @param tenantRepresentedBy A specification.
     */
    public void setLabel(TenantDescriptor tenantRepresentedBy) {
        if (this.label != null) {
            // Check if history shall be maintained
            if (!tenantRepresentedBy.changesHistory().contains(this.label)) {
                // Update the current label of this tenant with the new version enhanced
                // with auto-saving of the previous name into the new name's versions history
                this.label = (TenantDescriptor) this.label.enhanceHistoryOf(tenantRepresentedBy,
                        /* Don't manage the already defined history state */ null);
            } else {
                // new version is already instantiated with prior versions defined
                // No need to enhance, but only to replace this current label
                this.label = tenantRepresentedBy;
            }
        } else {
            // Initialize the first defined name of this tenant
            this.label = tenantRepresentedBy;
        }

        try {
            // Add a change event into the history regarding changed label descriptor
            ConcreteDomainChangeEvent changeEvt = prepareChangeEventInstance(DomainEventType.TENANT_CHANGED);
            // Add changed label value into description of change
            changeEvt.appendSpecification(new org.cybnity.framework.domain.Attribute(Attribute.LABEL.name(), this.label.getLabel()));
            addChangeEvent(changeEvt); // Add to changes history
        } catch (ImmutabilityException ie) {
            logger().log(Level.SEVERE, ie.getMessage(), ie);
        }
    }

    /**
     * Get the representative label of this tenant.
     *
     * @return A name or null if unknown.
     * @throws ImmutabilityException When problem of immutable version
     *                               instantiation.
     */
    public TenantDescriptor label() throws ImmutabilityException {
        if (this.label != null)
            return (TenantDescriptor) this.label.immutable();
        return null;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
        Tenant tenant = new Tenant(parent(), ids);
        tenant.createdAt = this.occurredAt();
        tenant.label = this.label();
        tenant.activityStatus = this.status();
        return tenant;
    }

    /**
     * Implement the generation of version hash regarding this class type.
     */
    @Override
    public String versionHash() {
        return String.valueOf(serialVersionUID);
    }

}
