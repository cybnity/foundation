package org.cybnity.feature.defense_template.domain.model.sample.writemodel;

import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.Identifier;

import java.util.LinkedHashSet;

/**
 * Example of domain entity relative to a company identity.
 *
 */
public class Organization extends DomainEntity {

	private static final long serialVersionUID = 1L;

	public Organization(Identifier id) throws IllegalArgumentException {
		super(id);
	}

	public Organization(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
		super(identifiers);
	}
}
