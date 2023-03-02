package org.cybnity.framework.domain.model.sample.writemodel;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Sample of simple domain Entity implementation class relative to a domain
 * object.
 * 
 * @author olivier
 *
 */
public class DomainEntityImpl extends Entity {

    private static final long serialVersionUID = 1L;

    public DomainEntityImpl(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    public DomainEntityImpl(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new DomainEntityImpl(ids);
    }

}
