package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

import java.io.Serializable;

/**
 * Sample of identifier implementation type only based on a single text chain.
 * 
 * @author olivier
 *
 */
public class IdentifierImpl implements Identifier {

    private static final long serialVersionUID = 1L;
    private final String value;
    private final String name;

    public IdentifierImpl(String name, String value) {
	this.name = name;
	this.value = value;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	return new IdentifierImpl(name, value);
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

    /**
     * Redefine the comparison of this fact with another based on the identifier.
     * 
     * @param fact To compare.
     * @return True if this fact is based on the same identifier(s) as the fact
     *         argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	if (obj != null && Identifier.class.isAssignableFrom(obj.getClass())) {
	    Identifier compared = (Identifier) obj;
	    // Compare equality based on each instance's identifier (unique or based on
	    // identifying informations combination)
	    return this.value.equals(compared.value()) && this.name.equals(compared.name());
	}
	return false;
    }
}
