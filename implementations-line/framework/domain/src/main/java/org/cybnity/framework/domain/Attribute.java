package org.cybnity.framework.domain;

import java.io.Serializable;

import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

/**
 * Represent a characteristic which can be add to a topic (e.g an technical
 * named attribute which is defined on-fly on an existing object, including a
 * value). It's more or less like a generic property assignable to any topic or
 * object (e.g property on a workflow step instance).
 * 
 * @author olivier
 *
 */
public class Attribute extends ValueObject<String> implements Serializable, IVersionable {

	private static final long serialVersionUID = new VersionConcreteStrategy()
			.composeCanonicalVersionHash(Attribute.class).hashCode();

	private String value;
	private String name;

	/**
	 * Default constructor.
	 * 
	 * @param attributeName  Mandatory name of this attribute (allowing topic search
	 *                       by attribute name).
	 * @param attributeValue Mandatory value of this named attribute.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public Attribute(String attributeName, String attributeValue) throws IllegalArgumentException {
		super();
		if (attributeName == null || "".equals(attributeName))
			throw new IllegalArgumentException("Attribute name parameter must be defined!");
		if (attributeValue == null || "".equals(attributeValue))
			throw new IllegalArgumentException("Attribute value parameter must be defined!");
		this.name = attributeName;
		this.value = attributeValue;
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
	 * Get the value of this named attribute.
	 * 
	 * @return A value.
	 */
	public String value() {
		return value;
	}

	/**
	 * Get the name of this attribute.
	 * 
	 * @return A name.
	 */
	public String name() {
		return name;
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new Attribute(this.name, this.value);
	}

	@Override
	public String[] valueHashCodeContributors() {
		return new String[] { name, value };
	}
}
