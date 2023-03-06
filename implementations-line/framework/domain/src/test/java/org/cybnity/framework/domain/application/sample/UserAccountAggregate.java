package org.cybnity.framework.domain.application.sample;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.IAggregate;
import org.cybnity.framework.domain.model.sample.ApplicativeRole;
import org.cybnity.framework.domain.model.sample.UserAccountApplicativeRoleAssigned;
import org.cybnity.framework.domain.model.sample.readmodel.ApplicativeRoleDTO;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Sample of business domain aggregate relative to a user account.
 * 
 * @author olivier
 *
 */
public class UserAccountAggregate extends Entity implements IAggregate {

    private EntityReference user;

    /**
     * Version of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Set of roles allowed to this user account.
     */
    private LinkedHashSet<ApplicativeRole> assignedRoles;

    /**
     * Default constructor of an account.
     * 
     * @param id            Mandatory identifier of this user account.
     * @param userIdentity  Mandatory user identity who is owner of this account.
     * @param assignedRoles Optional roles allowed to the user via this account.
     * @throws IllegalArgumentException When mandatory parameter is missing (e.g id
     *                                  parameter is null and does not include name
     *                                  and value).
     */
    public UserAccountAggregate(Identifier id, EntityReference userIdentity) throws IllegalArgumentException {
	super(id);
	if (!BaseConstants.IDENTIFIER_ID.name().equals(id.name()))
	    throw new IllegalArgumentException(
		    "id parameter is not valid because identifier name shall be equals to only supported value ("
			    + BaseConstants.IDENTIFIER_ID.name() + ")!");
	if (userIdentity == null)
	    throw new IllegalArgumentException("userIdentity parameter is required!");
	// Save unmodifiable user identity which is owner of this account
	this.user = userIdentity;
    }

    /**
     * Default constructor.
     * 
     * @param identifiers Set of mandatory identifiers of this entity, that contains
     *                    non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    protected UserAccountAggregate(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public void execute(Command change, IContext ctx) throws IllegalArgumentException {
	if (ctx == null)
	    throw new IllegalArgumentException("Context parameter is required!");
	if (change instanceof AssignRoleToUserAccountCommand) {
	    AssignRoleToUserAccountCommand toProcess = (AssignRoleToUserAccountCommand) change;
	    ApplicativeRoleDTO toAssign = toProcess.assignedRole;
	    String userAccountId = toProcess.userAccountIdentifier;
	    // Security check regarding a modification requested for this user account
	    // entity
	    if (toAssign != null && userAccountId != null && userAccountId.equals((String) this.identified().value())) {
		try {
		    // Identify the property to change in the domain object (e.g user permission
		    // regarding a role supported by this account)

		    // Update the roles assignment according to the requested change
		    addAssignedRole(toAssign.getName(), toAssign.status);

		    // Save the changed state (e.g historization of the entity immutable object's
		    // version) in a data persistence when existing
		    UserAccountStore accountStore = (UserAccountStore) ctx.get(UserAccountStore.class.getName());
		    accountStore.append(this, toProcess);

		    // -------------> SAVE CONFIRMED AS CONSISTENT UNIT OF USERACCOUNTAGGREGATE
		    // VERSION BY UserAccountChanged event automatically send by the store

		    // Creation an event confirming the changed status of the domain object (e.g
		    // assignment of new application roles)
		    // Build event child based on the updated account (parent of immutable story)
		    CommonChildFactImpl modifiedAccountAssignment = new CommonChildFactImpl(this,
			    new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
				    /* identifier as performed transaction number */ UUID.randomUUID().toString()));

		    // Prepare notification of the read model about changed user account's roles
		    UserAccountApplicativeRoleAssigned modifiedAccount = new UserAccountApplicativeRoleAssigned(
			    modifiedAccountAssignment.parent());
		    modifiedAccount.changeCommandRef = toProcess.reference();
		    modifiedAccount.changedAccountRef = this.reference();

		    // Notify the changed state to the possible observers
		    // Publish event occured on the write model regarding the changed user account
		    DomainEventPublisher.instance().publish(modifiedAccount);
		    return;
		} catch (ImmutabilityException cnse) {
		    // Cloning exception regarding persised account change
		    // Log the implementation problem and notify the error occured during the
		    // command processing for information of command sender
		}
	    } else {
		throw new IllegalArgumentException(
			"assignedRole shall be defined into the command to process for the good user account entity!");
	    }
	}
	throw new IllegalArgumentException(
		"Unsupported type of command by " + UserAccountAggregate.class.getName() + "!");
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new UserAccountAggregate(ids);
    }

    /**
     * Add an applicative role allowed to this account.
     * 
     * @param roleName A role name to assign at this account. Ignored if null.
     * @param state    Role status to consider.
     * @throws ImmutabilityException When problem of role instantiation.
     */
    private void addAssignedRole(String roleName, HistoryState state) throws ImmutabilityException {
	if (roleName != null && !roleName.equals("")) {
	    if (this.assignedRoles == null) {
		this.assignedRoles = new LinkedHashSet<>();
	    }
	    // Find existing role with the same name in the Set, in a same status of
	    // assignment (perhaps previous same role was committed, but after was removed,
	    // and need to be re-assigned)
	    ApplicativeRole existingRoleToUpdate = null;
	    for (ApplicativeRole aRole : this.assignedRoles) {
		if (aRole.getName().equalsIgnoreCase(roleName)) {
		    existingRoleToUpdate = aRole;
		    // Existant role that should be enhanced with a new version (e.g permissions
		    // allowed)
		    break;
		}
	    }
	    if (existingRoleToUpdate != null) {
		// Archive the previous role version regarding existing history when exist
		Set<ApplicativeRole> changedPredecessors = existingRoleToUpdate.changesHistory();
		// Add last version of current role into history
		changedPredecessors.add(existingRoleToUpdate);
		// Create a new version of role as current version (which perhaps content
		// updated permissions etc...)
		ApplicativeRole newCurrentRoleVersion = new ApplicativeRole(this.user, roleName);
		newCurrentRoleVersion.setHistoryStatus(state);
		// Set the old history (including the previous last version of role)
		newCurrentRoleVersion.updateChangesHistory(changedPredecessors);
		// Replace the new current role version in the roles attribute
		this.assignedRoles.remove(existingRoleToUpdate);
		this.assignedRoles.add(newCurrentRoleVersion);
	    } else {
		// Or add a new role assigned to this account
		this.assignedRoles.add(new ApplicativeRole(this.user, roleName));
	    }
	}
    }

    /**
     * Get the roles allowed to this account.
     * 
     * @return A set of immutable roles or empty set.
     * @throws ImmutabilityException When problem of immutable instances occurred.
     */
    public Set<ApplicativeRole> assignedRoles() throws ImmutabilityException {
	if (this.assignedRoles == null) {
	    // Return empty set
	    return new LinkedHashSet<>();
	}
	Set<ApplicativeRole> immutableSet = new LinkedHashSet<>(this.assignedRoles.size());
	for (ApplicativeRole role : this.assignedRoles) {
	    immutableSet.add((ApplicativeRole) role.immutable());
	}
	return immutableSet;
    }
}
