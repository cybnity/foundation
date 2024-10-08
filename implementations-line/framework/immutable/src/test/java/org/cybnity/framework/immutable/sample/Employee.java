package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.IMember;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Example of member type regarding an organization.
 * 
 * @author olivier
 *
 */
public class Employee implements IMember {

    private static final long serialVersionUID = 1L;
    private final String name;
    private final Identifier id;
    private OffsetDateTime at;

    public Employee(String name, Identifier id) {
	this.name = name;
	this.id = id;
	this.at = OffsetDateTime.now();
    }

    @Override
    public Identifier identified() {
	return new IdentifierImpl(id.name(), id.value().toString());
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.at;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new Employee(this.name, this.id).at = this.at;
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