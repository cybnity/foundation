package org.cybnity.framework.domain.model.sample.readmodel;

import java.time.OffsetDateTime;
import java.util.Set;

import org.cybnity.framework.immutable.Entity;

/**
 * Example of denormalized version of an instance of UserAccountAggregate, ready
 * for read by a UI layer.
 * 
 * @author olivier
 *
 */
public class UserAccountDTO {

    private Entity userAccountEntityIdentifier;
    private OffsetDateTime versionOf;
    private Set<ApplicativeRoleDTO> roles;

    public UserAccountDTO(Entity accountId, OffsetDateTime versionOf, Set<ApplicativeRoleDTO> assignedRoles) {
	this.userAccountEntityIdentifier = accountId;
	this.versionOf = versionOf;
	this.roles = assignedRoles;
    }

    public Entity getUserAccountEntityIdentifier() {
	return this.userAccountEntityIdentifier;
    }

    public Set<ApplicativeRoleDTO> getRoles() {
	return roles;
    }

    public OffsetDateTime getVersionOf() {
	return this.versionOf;
    }

}