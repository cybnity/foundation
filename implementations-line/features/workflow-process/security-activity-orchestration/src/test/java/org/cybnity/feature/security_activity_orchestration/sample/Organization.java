package org.cybnity.feature.security_activity_orchestration.sample;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of domain entity specifically embedding properties that need to be
 * changeable (managed via the Mutable Property pattern).
 * 
 * @author olivier
 *
 */
public class Organization extends Aggregate implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String unmodifiableOrganizationName;
	private DomainEntityImpl id;

	public Organization(Identifier id, String organizationName) throws IllegalArgumentException {
		this.id = new DomainEntityImpl(id);
		this.unmodifiableOrganizationName = organizationName;
	}

	public Organization(LinkedHashSet<Identifier> identifiers, String organizationName)
			throws IllegalArgumentException {
		this.id = new DomainEntityImpl(identifiers);
		this.unmodifiableOrganizationName = organizationName;
	}

	public Entity identity() {
		return this.id;
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
	public void execute(Command change, IContext ctx) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public Identifier identified() throws ImmutabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] valueHashCodeContributors() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public Serializable immutable() throws ImmutabilityException {
	 * LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.id.identifiers());
	 * Organization copy = new Organization(ids, this.name());
	 * copy.id.setCreatedAt(this.id.occurredAt()); return copy; }
	 */
}
