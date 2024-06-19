package org.cybnity.framework.domain.model.sample.readmodel;

import org.cybnity.framework.immutable.Entity;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Example of denormalized version of an instance of UserAccountAggregate, ready
 * for read by a UI layer.
 * 
 * @author olivier
 *
 */
public class UserAccountDTO {

    private final Entity userAccountEntityIdentifier;
    private final OffsetDateTime versionOf;
    private final Set<ApplicativeRoleDTO> roles;

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