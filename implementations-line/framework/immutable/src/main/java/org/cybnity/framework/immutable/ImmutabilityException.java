package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Exception regarding a problem of architecture support regarding a
 * immutability requirements (e.g impossible creation of an immutable copy of a
 * fact).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class ImmutabilityException extends Exception {

    private static final long serialVersionUID = new VersionConcreteStrategy()
	    .composeCanonicalVersionHash(ImmutabilityException.class).hashCode();

    public ImmutabilityException() {
	super();
    }

    public ImmutabilityException(String message) {
	super(message);
    }

    public ImmutabilityException(Throwable cause) {
	super(cause);
    }

    public ImmutabilityException(String message, Throwable cause) {
	super(message, cause);
    }

    public ImmutabilityException(String message, Throwable cause, boolean enableSuppression,
	    boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

}
