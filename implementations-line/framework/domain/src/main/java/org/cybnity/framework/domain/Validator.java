package org.cybnity.framework.domain;

/**
 * Validation class implementing Specification pattern or the Strategy pattern.
 * If it detects and invalid state, it informs the observes or otherwise makes a
 * record of the results that can be reviewed later.
 * 
 * It is important for the validation process to collect a full set of results
 * rather than throw an exception at the first sign of trouble.
 * 
 * As Deferred Validation approach, a validation component has the
 * responsibility to determine whether or not an Entity state is valid.
 * 
 * @author olivier
 *
 */
public abstract class Validator {
	private final IValidationNotificationHandler notificationHandler;

	/**
	 * Default constructor.
	 * 
	 * @param notified To notify about validation results.
	 */
	public Validator(IValidationNotificationHandler notified) {
		super();
		this.notificationHandler = notified;
	}

	/**
	 * Get the notification to apply.
	 * 
	 * @return A handler of notification or null.
	 */
	protected IValidationNotificationHandler notificationHandler() {
		return this.notificationHandler;
	}

	/**
	 * Execute a validation process. When a problem is detected on a validated
	 * subject or on a content, the notification handler is informed.
	 */
	public abstract void validate();
}
