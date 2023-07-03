package org.cybnity.feature.defense_template.domain.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cybnity.feature.defense_template.domain.DomainObjectType;
import org.cybnity.feature.defense_template.domain.IReferential;
import org.cybnity.feature.defense_template.domain.ITemplate;
import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Common implementation class regarding a specification object which define a
 * template (e.g process aggregate object).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Template extends Aggregate implements ITemplate {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Template.class).hashCode();

	/**
	 * Origin of this template.
	 */
	private IReferential originReferential;

	/**
	 * Default constructor.
	 * 
	 * @param predecessor       Mandatory parent of this root aggregate entity.
	 * @param id                Unique and optional identifier of this template. For
	 *                          example, identifier is required when the aggregate
	 *                          shall be persistent. Else, can be without identity
	 *                          when not persistent aggregate.
	 * @param originReferential Optional referential which is origin of this
	 *                          template.
	 * @param name              Mandatory label naming this template.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 *                                  When a problem of immutability is occurred.
	 *                                  When predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Template(Entity predecessor, Identifier id, IReferential originReferential/** , String name **/
	) throws IllegalArgumentException {
		super(predecessor, id);
		// if (name==null || "".equals(name)) throw new IllegalArgumentException("Name
		// parameter is required!");
		// TODO creer une mutable property de type string nommée LABEL

		// TODO creer un DomainObjectType du nom/description correspondant au type de
		// template

		this.originReferential = originReferential;
	}

	/**
	 * Specific partial constructor.
	 * 
	 * @param predecessor       Mandatory parent of this root aggregate entity.
	 * @param identifiers       Optional set of identifiers of this template, that
	 *                          contains non-duplicable elements. For example,
	 *                          identifier is required when the aggregate shall be
	 *                          persistent. Else, can be without identity when not
	 *                          persistent aggregate.
	 * @param originReferential Optional referential which is origin of this
	 *                          template.
	 * @param name              Mandatory label naming this template.
	 * @throws IllegalArgumentException When identifiers parameter is null or each
	 *                                  item does not include name and value. When
	 *                                  predecessor mandatory parameter is not
	 *                                  defined or without defined identifier.
	 */
	public Template(Entity predecessor, LinkedHashSet<Identifier> identifiers, IReferential originReferential/*,
			String name*/) throws IllegalArgumentException {
		super(predecessor, identifiers);
		//if (name == null || "".equals(name))
		//	throw new IllegalArgumentException("Name parameter is required!");
		// TODO creer une mutable property de type string nommée LABEL

		this.originReferential = originReferential;
	}

	/**
	 * Get the origin of this template.
	 * 
	 * @return A referential or null.
	 * @throws ImmutabilityException When impossible creation of immutable version.
	 */
	protected IReferential originReferential() throws ImmutabilityException {
		if (this.originReferential != null)
			return (IReferential) this.originReferential.immutable();
		return this.originReferential;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		Template copy = new Template(this.parent(), new LinkedHashSet<>(this.identifiers()), this.originReferential());
		// Complete with additional attributes of this complex aggregate
		copy.createdAt = this.occurredAt();
		return copy;
	}

	/**
	 * Redefined equality evaluation including the identity and name.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;
		// Comparison based on process identity
		if (super.equals(obj) && obj instanceof Template) {
			try {
				Template compared = (Template) obj;
				// Comparison based on version functional attributes
				// Check if same names
				String objNameAttribute = compared.name();
				String thisNameAttribute = this.name();
				if (objNameAttribute != null && thisNameAttribute != null) {
					isEquals = objNameAttribute.equals(thisNameAttribute);
				}
			} catch (Exception e) {
				// any missing information generating null pointer exception or problem of
				// information read
			}
		}
		return isEquals;
	}

	/**
	 * Do nothing about this object.
	 */
	@Override
	public void handle(Command command, IContext ctx) throws IllegalArgumentException {
	}

	@Override
	public Set<String> handledCommandTypeVersions() {
		return null;
	}

	@Override
	public String name() {
		// TODO coder lecture et instantiation en construction avec param obligatoire de
		// nom de template et lecture de la valeur current de la mutable property du
		// label
		return null;
	}

	@Override
	public DomainObjectType type() {
		// TODO Auto-generated method stub
		return null;
	}

}
