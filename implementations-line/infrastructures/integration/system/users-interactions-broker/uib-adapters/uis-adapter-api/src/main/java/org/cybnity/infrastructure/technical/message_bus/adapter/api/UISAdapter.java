package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.UnoperationalStateException;

/**
 * Users Interactions Space adapter which expose the API services that allow
 * interactions with the collaboration space.
 * 
 * @author olivier
 *
 */
public interface UISAdapter {

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the Users Interactions Space.
     * 
     * @throws UnoperationalStateException
     */
    public void checkHealthyState() throws UnoperationalStateException;
}
