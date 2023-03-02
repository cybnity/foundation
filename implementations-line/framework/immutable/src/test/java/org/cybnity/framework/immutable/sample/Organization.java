package org.cybnity.framework.immutable.sample;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of domain entity specifically embedding properties that need to be
 * changeable (managed via the Mutable Property pattern).
 * 
 * @author olivier
 *
 */
public class Organization extends EntityImpl {

    private static final long serialVersionUID = 1L;
    private final String unmodifiableOrganizationName;

    public Organization(Identifier id, String organizationName) throws IllegalArgumentException {
	super(id);
	this.unmodifiableOrganizationName = organizationName;
    }

    public Organization(LinkedHashSet<Identifier> identifiers, String organizationName)
	    throws IllegalArgumentException {
	super(identifiers);
	this.unmodifiableOrganizationName = organizationName;
    }

    /**
     * Get the fixed and unmodifiable name of this organization.
     * 
     * @return A name that can be modified.
     */
    public String name() {
	return new String(this.unmodifiableOrganizationName);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	Organization copy = new Organization(ids, this.name());
	copy.createdAt = this.createdAt;
	return copy;
    }
}
