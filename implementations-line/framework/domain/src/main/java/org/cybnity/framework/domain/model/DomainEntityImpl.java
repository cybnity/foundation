package org.cybnity.framework.domain.model;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Basic and common domain entity implementation object.
 * 
 * @author olivier
 *
 */
public class DomainEntityImpl extends Entity {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(DomainEntityImpl.class).hashCode();

    /**
     * Default constructor.
     * 
     * @param id Unique and mandatory identifier of this entity.
     * @throws IllegalArgumentException When id parameter is null and does not
     *                                  include name and value.
     */
    public DomainEntityImpl(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    /**
     * Default constructor.
     * 
     * @param identifiers Set of mandatory identifiers of this entity, that contains
     *                    non-duplicable elements.
     * @throws IllegalArgumentException When identifiers parameter is null or each
     *                                  item does not include name and value.
     */
    protected DomainEntityImpl(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	return IdentifierStringBased.build(this.identifiers());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new DomainEntityImpl(ids);
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
