package org.cybnity.framework.domain.application.sample;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.EventIdentifierStringBased;
import org.cybnity.framework.domain.SampleDataEnum;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.sample.ApplicativeRole;
import org.cybnity.framework.domain.model.sample.UserAccountApplicativeRoleAssigned;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Sample of business domain aggregate relative to a user account.
 * 
 * @author olivier
 *
 */
public class UserAccountAggregate extends Entity implements Aggregate {

    private EntityReference user;

    /**
     * Version of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Set of roles allowed to this user account.
     */
    private Set<ApplicativeRole> assignedRoles;

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
	if (!SampleDataEnum.IDENTIFIER_NAME_TECH.name().equals(id.name()))
	    throw new IllegalArgumentException(
		    "id parameter is not valid because identifier name shall be equals to only supported value ("
			    + SampleDataEnum.IDENTIFIER_NAME_TECH.name() + ")!");
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
    public void execute(Command change) throws IllegalArgumentException {
	if (change instanceof AssignRoleToUserAccountCommand) {
	    AssignRoleToUserAccountCommand toProcess = (AssignRoleToUserAccountCommand) change;
	    ApplicativeRole toAssign = toProcess.assignedRole;
	    if (toAssign != null) {
		try {
		    // Identify the property to change in the domain object (e.g user permission
		    // regarding a role supported by this account)

		    // Update the roles assignment according to the requested change

		    // Save the changed state (e.g historization of the entity immutable object's
		    // version) in a data persistence when existing
		    // -------------> SAVE CONFIRMED AS CONSISTENT UNIT OF USERACCOUNTAGGREGATE
		    // VERSION

		    // Creation an event confirming the changed status of the domain object (e.g
		    // assignment of new application roles)
		    // Build event child based on the updated account (parent of immutable story)
		    CommonChildFactImpl modifiedAccountAssignment = new CommonChildFactImpl(this,
			    new EventIdentifierStringBased(SampleDataEnum.IDENTIFIER_NAME_TECH.name(),
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
			new InvalidParameterException("assignedRole shall be defined into the command to process!"));
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
	return new EventIdentifierStringBased(SampleDataEnum.IDENTIFIER_NAME_TECH.name(), combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new UserAccountAggregate(ids);
    }

    /**
     * Add an applicative role allowed to this account.
     * 
     * @param assignedRoles A role to assign at this account. Ignored if null.
     */
    private void addAssignedRole(ApplicativeRole assignedRole) {
	if (assignedRole == null) {
	    // Archive the previous role version regarding existing history when exist

	    // Or add a new role assigned to this account
	}
    }
}
