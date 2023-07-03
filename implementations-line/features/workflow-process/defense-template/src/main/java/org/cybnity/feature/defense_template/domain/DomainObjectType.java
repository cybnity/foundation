package org.cybnity.feature.defense_template.domain;

import java.io.Serializable;

import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a type of domain object (e.g template type) which can define a
 * category of domain object. It's more or less like a generic type assignable
 * to any domain object as specification information.
 * 
 * For example, can be used by a template to define its type (e.g due diligence
 * model, risk assessment process).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class DomainObjectType extends ValueObject<String> implements Serializable, IVersionable {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(DomainObjectType.class).hashCode();

	private String description;
	private String name;

	/**
	 * Default constructor.
	 * 
	 * @param typeName        Mandatory name of this type (allowing topic search by
	 *                        label).
	 * @param typeDescription Optional description text of this type (e.g
	 *                        internationalization code).
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public DomainObjectType(String typeName, String typeDescription) throws IllegalArgumentException {
		super();
		if (typeName == null || "".equals(typeName))
			throw new IllegalArgumentException("Type name parameter must be defined!");
		this.name = typeName;
		this.description = typeDescription;
	}

	/**
	 * Implement the generation of version hash regarding this class type according
	 * to a concrete strategy utility service.
	 */
	@Override
	public String versionHash() {
		return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
	}

	/**
	 * Get a description value of this named type.
	 * 
	 * @return A value (e.g textual description, or international code using for
	 *         description translation) or null.
	 */
	public String description() {
		return description;
	}

	/**
	 * Get the name of this type.
	 * 
	 * @return A name.
	 */
	public String name() {
		return name;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new DomainObjectType(this.name, this.description);
	}

	@Override
	public String[] valueHashCodeContributors() {
		if (description != null) {
			return new String[] { name, description };
		}
		// Only based on mandatory name
		return new String[] { name };
	}
}
