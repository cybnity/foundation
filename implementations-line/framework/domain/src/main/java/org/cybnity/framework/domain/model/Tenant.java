package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.LinkedHashSet;

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
public class Tenant extends CommonChildFactImpl {

    /**
     * Version of this class
     */
    private static final long serialVersionUID = new VersionConcreteStrategy().composeCanonicalVersionHash(Tenant.class)
            .hashCode();

    /**
     * Logical organization representing this tenant (e.g business name of a
     * company) that facilitate to resolve queries.
     */
    private MutableProperty organization;

    /**
     * Current mutable status of activity regarding this tenant.
     */
    private ActivityState activityStatus;

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
        super(predecessor, id);
        if (id != null && !BaseConstants.IDENTIFIER_ID.name().equals(id.name()))
            throw new IllegalArgumentException(
                    "id parameter is not valid because identifier name shall be equals to only supported value ("
                            + BaseConstants.IDENTIFIER_ID.name() + ")!");
        if (currentStatus != null) {
            try {
                this.activityStatus = new ActivityState(parent().reference(), currentStatus);
            } catch (ImmutabilityException ie) {
                // Normally shall never arrive
                // TODO : add technical log
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
        super(predecessor, identifiers);
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
     * Define a logical organization regarding this tenant when none previously
     * defined. When existing previous organization about this tenant, this method
     * make a change on the previous defined organization (changes history is saved)
     * to defined the new one as current.
     *
     * @param tenantRepresentedBy An organization (e.g social entity, physical
     *                            entity).
     */
    public void setOrganization(MutableProperty tenantRepresentedBy) {
        if (this.organization != null) {
            // Check if history shall be maintained
            if (!tenantRepresentedBy.changesHistory().contains(this.organization)) {
                // Update the current organization of this tenant with the new version enhanced
                // with auto-saving of the previous name into the new organization's versions history
                this.organization = this.organization.enhanceHistoryOf(tenantRepresentedBy,
                        /* Don't manage the already defined history state */ null);
            } else {
                // new version is already instantiated with prior versions defined
                // No need to enhance, but only to replace this current organization
                this.organization = tenantRepresentedBy;
            }
        } else {
            // Initialize the first defined name of this tenant
            this.organization = tenantRepresentedBy;
        }
    }

    /**
     * Get the representative organization of this tenant.
     *
     * @return An organization or null if unknown.
     * @throws ImmutabilityException When problem of immutable version
     *                               instantiation.
     */
    public MutableProperty organization() throws ImmutabilityException {
        if (this.organization != null)
            return (MutableProperty) this.organization.immutable();
        return null;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
        Tenant tenant = new Tenant(parent(), ids);
        tenant.createdAt = this.occurredAt();
        tenant.organization = this.organization();
        tenant.activityStatus = this.status();
        return tenant;
    }

}
