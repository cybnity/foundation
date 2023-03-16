package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a missing setting regarding a component (e.g applicative module).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public class MissingConfigurationException extends UnoperationalStateException {

    private static final long serialVersionUID = 1L;

    public MissingConfigurationException() {
	super();
    }

    public MissingConfigurationException(String message, Throwable cause, boolean enableSuppression,
	    boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public MissingConfigurationException(String message, Throwable cause) {
	super(message, cause);
    }

    public MissingConfigurationException(String message) {
	super(message);
    }

    public MissingConfigurationException(Throwable cause) {
	super(cause);
    }

}
