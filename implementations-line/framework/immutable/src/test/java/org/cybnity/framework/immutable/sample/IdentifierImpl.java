package org.cybnity.framework.immutable.sample;

import org.cybnity.framework.immutable.Identifier;

/**
 * Sample of identifier implementation type only based on a single text chain.
 * 
 * @author olivier
 *
 */
public class IdentifierImpl implements Identifier {

    private String value;
    private String name;

    public IdentifierImpl(String name, String value) {
	this.name = name;
	this.value = value;
    }

    @Override
    public Object immutable() throws CloneNotSupportedException {
	return new IdentifierImpl(name.toString(), value.toString());
    }

    @Override
    public String name() {
	return this.name;
    }

    @Override
    public Object value() {
	return this.value;
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
	    return this.value.equals(compared.value());
	}
	return false;
    }
}