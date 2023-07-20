package org.cybnity.feature.defense_template.service;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent an exception relative to a value (e.g node element name, attribute
 * name, value of attribute or node element) that is not supported by the
 * applicative component (e.g parser).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class NotSupportedTemplateValueException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotSupportedTemplateValueException() {
		super();
	}

	public NotSupportedTemplateValueException(String message) {
		super(message);
	}

	public NotSupportedTemplateValueException(Throwable cause) {
		super(cause);
	}

	public NotSupportedTemplateValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotSupportedTemplateValueException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
