package org.cybnity.framework.immutable.sample;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Sample of simple Entity.
 * 
 * @author olivier
 *
 */
public class EntityImpl extends Entity {

    private static final long serialVersionUID = 1L;

    public EntityImpl(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    public EntityImpl(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new IdentifierImpl("UUID", combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new EntityImpl(ids);
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
