package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a state of running that is considered as non operational (e.g bad
 * healthy, missing setting).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public class UnoperationalStateException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnoperationalStateException() {
	super();
    }

    public UnoperationalStateException(String message) {
	super(message);
    }

    public UnoperationalStateException(Throwable cause) {
	super(cause);
    }

    public UnoperationalStateException(String message, Throwable cause) {
	super(message, cause);
    }

    public UnoperationalStateException(String message, Throwable cause, boolean enableSuppression,
	    boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

}
