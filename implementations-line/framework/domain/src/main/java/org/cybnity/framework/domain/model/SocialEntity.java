package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.LinkedHashSet;

/**
 * Represent a social entity instance (e.g a company, a person) which define an
 * identifiable structure (e.g organizational, physical).
 * 
 * Define a root aggregate entity.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_3")
public abstract class SocialEntity extends Aggregate {

	/**
	 * Version of this class structure.
	 */
	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(SocialEntity.class).hashCode();

	/**
	 * Default constructor.
	 * 
	 * @param predecessor Mandatory parent of this child entity.
	 * @param id          Unique and optional identifier of this instance.
	 * @throws IllegalArgumentException When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public SocialEntity(Entity predecessor, Identifier id) throws IllegalArgumentException {
		super(predecessor, id);
	}

	/**
	 * Default constructor.
	 * 
	 * @param predecessor Mandatory parent of this child entity.
	 * @param identifiers Set of optional base identifiers of this instance, that
	 *                    contains non-duplicable elements.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value.
	 */
	public SocialEntity(Entity predecessor, LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
		super(predecessor, identifiers);
	}

}
