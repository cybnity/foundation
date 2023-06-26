package org.cybnity.feature.security_activity_orchestration.domain.model;

import java.io.Serializable;

import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a characteristic which can be add to a topic (e.g a technical named
 * attribute which is defined on-fly on an existing object, including a value).
 * It's more or less like a generic property assignable to any topic or object
 * (e.g property on a workflow step instance).
 * 
 * For example, can be use to defined a tag regarding a property added to a
 * domain or aggregate object.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Attribute extends ValueObject<String> implements Serializable {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Attribute.class).hashCode();

	private String value;
	private String name;

	/**
	 * Default constructor.
	 * 
	 * @param name  Mandatory name of the attribute.
	 * @param value Mandatory value of the attribute.
	 * @throws IllegalArgumentException When any mandatory parameter is missing.
	 */
	public Attribute(String name, String value) throws IllegalArgumentException {
		if (name == null || "".equals(name))
			throw new IllegalArgumentException("The name parameter is required!");
		this.name = name;
		if (value == null || "".equals(value))
			throw new IllegalArgumentException("The value parameter is required!");
		this.value = value;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new Attribute(name, value);
	}

	/**
	 * Get the attribute name.
	 * 
	 * @return An immutable value of this attribute name.
	 */
	public String name() {
		return new String(this.name);
	}

	/**
	 * Get the value of this attribute.
	 * 
	 * @return An immutable version of this attribute value.
	 */
	public String value() {
		return new String(this.value);
	}

	@Override
	public String[] valueHashCodeContributors() {
		return new String[] { this.value, this.name };
	}

}
