package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.cybnity.framework.domain.EventIdentifierStringBased;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Simple identifying information regarding a user account creation.
 * 
 * @author olivier
 *
 */
public class UserAccountIdentityCreation extends Entity {

    private static final long serialVersionUID = 1L;

    public UserAccountIdentityCreation(Identifier id) throws IllegalArgumentException {
	super(id);
    }

    public UserAccountIdentityCreation(LinkedHashSet<Identifier> identifiers) throws IllegalArgumentException {
	super(identifiers);
    }

    @Override
    public Identifier identified() {
	StringBuffer combinedId = new StringBuffer();
	for (Identifier id : this.identifiers()) {
	    combinedId.append(id.value());
	}
	// Return combined identifier
	return new EventIdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), combinedId.toString());
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	LinkedHashSet<Identifier> ids = new LinkedHashSet<>(this.identifiers());
	return new UserAccountIdentityCreation(ids);
    }

}
