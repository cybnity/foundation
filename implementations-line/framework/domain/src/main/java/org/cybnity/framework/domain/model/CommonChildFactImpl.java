package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.ChildFact;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Generic child fact implementation class.
 * 
 * Can be extended as a root aggregate object.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class CommonChildFactImpl extends ChildFact {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(CommonChildFactImpl.class).hashCode();

	/**
	 * Default constructor.
	 * 
	 * @param predecessor Mandatory parent of this child entity.
	 * @param id          Unique and optional identifier of this entity.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public CommonChildFactImpl(Entity predecessor, Identifier id) throws IllegalArgumentException {
		super(predecessor, id);
	}

	/**
	 * Specific partial constructor.
	 * 
	 * @param predecessor Mandatory parent of this child entity.
	 * @param identifiers Optional set of identifiers of this entity, that contains
	 *                    non-duplicable elements.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value. When
	 *                                  predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public CommonChildFactImpl(Entity predecessor, LinkedHashSet<Identifier> identifiers)
			throws IllegalArgumentException {
		super(predecessor, identifiers);
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}

	@Override
	public Identifier identified() {
		Identifier id = null;
		try {
			id = IdentifierStringBased.build(this.identifiers());
		} catch (Exception e) {
			// When none identifier defined regarding this child fact (e.g boundary of
			// domain aggregate without persistence capability that doesn't need to be
			// identified in long-time period)
		}
		return id;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
		CommonChildFactImpl copy = new CommonChildFactImpl(this.parent(), ids);
		// Complete with additional attributes of this complex object
		copy.createdAt = this.occurredAt();
		return copy;
	}

	/**
	 * Define the generation rule of identifier based on original child id name or
	 * BaseConstants.IDENTIFIER_ID.name().
	 */
	@Override
	protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Identifier childOriginalId)
			throws IllegalArgumentException {
		return Predecessors.generateIdentifierPredecessorBased(predecessor, childOriginalId);
	}

	/**
	 * Define the generation rule of identifier based on original child identifiers
	 * name or BaseConstants.IDENTIFIER_ID.name().
	 */
	@Override
	protected Identifier generateIdentifierPredecessorBased(Entity predecessor, Collection<Identifier> childOriginalIds)
			throws IllegalArgumentException {
		return Predecessors.generateIdentifierPredecessorBased(predecessor, childOriginalIds);
	}

}
