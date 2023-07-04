package org.cybnity.feature.defense_template.domain;

/**
 * Represent an exception about a service parameter that is not supported (e.g
 * parameter value not covered by the service).
 * 
 * @author olivier
 *
 */
public class UnsupportedParameterTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedParameterTypeException() {
		super();
	}

	public UnsupportedParameterTypeException(String message) {
		super(message);
	}

	public UnsupportedParameterTypeException(Throwable cause) {
		super(cause);
	}

	public UnsupportedParameterTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedParameterTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
