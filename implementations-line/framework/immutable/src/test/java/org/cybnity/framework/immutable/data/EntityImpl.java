package org.cybnity.framework.immutable.data;

import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;

/**
 * Sample of simple Entity.
 * 
 * @author olivier
 *
 */
public class EntityImpl extends Entity {

    public EntityImpl(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    public EntityImpl(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new IdentifierImpl("UUID", combinedId.toString());
    }

    @Override
    public Object immutable() throws CloneNotSupportedException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new EntityImpl(ids);
    }

}
