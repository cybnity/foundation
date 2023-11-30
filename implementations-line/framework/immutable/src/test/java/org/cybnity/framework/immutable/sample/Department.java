package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.IGroup;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Example of logical group regarding an organization.
 * 
 * @author olivier
 *
 */
public class Department implements IGroup {
    private static final long serialVersionUID = 1L;
    private String label;
    private Identifier id;
    private OffsetDateTime at;

    public Department(String label, Identifier id) {
	this.label = label;
	this.id = id;
	this.at = OffsetDateTime.now();
    }

    @Override
    public Identifier identified() {
	return new IdentifierImpl(new String(id.name()), new String(id.value().toString()));
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.at;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new Department(this.label, this.id).at = this.at;
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