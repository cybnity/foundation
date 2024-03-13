package org.cybnity.feature.security_activity_orchestration.domain.model.sample.readmodel;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * Simple identifying information regarding a process creation.
 * 
 * @author olivier
 *
 */
public class ProcessIdentityCreation extends Entity {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(ProcessIdentityCreation.class).hashCode();

	public ProcessIdentityCreation(Identifier id) throws IllegalArgumentException {
		super(id);
	}

	public ProcessIdentityCreation(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
		super(identifiers);
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
		return new ProcessIdentityCreation(ids);
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}
}
