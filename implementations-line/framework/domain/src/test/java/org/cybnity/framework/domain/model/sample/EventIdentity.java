package org.cybnity.framework.domain.model.sample;

import java.util.LinkedHashSet;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;

/**
 * Sample of simple indentifying information.
 * 
 * @author olivier
 *
 */
public class EventIdentity extends Entity {

    public EventIdentity(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    public EventIdentity(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new SimpleUIDIdentifier("UUID", combinedId.toString());
    }

    @Override
    public Object immutable() throws CloneNotSupportedException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new EventIdentity(ids);
    }

}
