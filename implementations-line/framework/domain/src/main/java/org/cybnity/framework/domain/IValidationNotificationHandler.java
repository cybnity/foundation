package org.cybnity.framework.domain;

/**
 * Contract of handling regarding detected any problem on a subject (e.g Entity
 * attributes, parameters values) which is notified.
 * 
 * @author olivier
 *
 */
public interface IValidationNotificationHandler {

    /**
     * Notify a problem observed during the validation executed.
     * 
     * @param message
     */
    public void handleError(String message);
}
