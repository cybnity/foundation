package org.cybnity.framework.domain;

import org.cybnity.framework.UnoperationalStateException;

/**
 * Contract relative to the health control regarding a component in terms of operational state.
 */
public interface IHealthControl {

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the SSO server.
     *
     * @throws UnoperationalStateException When adapter status problem detected.
     */
    void checkHealthyState() throws UnoperationalStateException;
}
