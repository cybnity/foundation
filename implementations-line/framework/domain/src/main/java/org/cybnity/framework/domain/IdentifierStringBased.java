package org.cybnity.framework.domain;

import java.io.Serializable;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Identifying information type that is based on a single text chain.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class IdentifierStringBased extends ValueObject<String> implements Identifier {

    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(IdentifierStringBased.class).hashCode();
    private String value;
    private String name;

    /**
     * Default constructor.
     * 
     * @param name  Mandatory name of the identifier (e.g uuid).
     * @param value Mandatory value of the identifier.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public IdentifierStringBased(String name, String value) throws IllegalArgumentException {
	if (name == null || "".equals(name))
	    throw new IllegalArgumentException("The name parameter is required!");
	this.name = name;
	if (value == null || "".equals(value))
	    throw new IllegalArgumentException("The value parameter is required!");
	this.value = value;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new IdentifierStringBased(name.toString(), value.toString());
    }

    @Override
    public String name() {
	return this.name;
    }

    @Override
    public Serializable value() {
	return this.value;
    }

    @Override
    public String[] valueHashCodeContributors() {
	return new String[] { this.value, this.name };
    }

}
