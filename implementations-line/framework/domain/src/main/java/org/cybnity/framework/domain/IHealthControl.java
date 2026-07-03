package org.cybnity.framework.domain;

import org.cybnity.framework.UnoperationalStateException;

/**
 * Contract relative to the health control regarding a component in terms of operational state.
 */
public interface IHealthControl {

    /**
     * Verify the current status of the resource or of its adapter as healthy and operable for
     * interactions with it (e.g; via its system in ownership).
     *
     * @throws UnoperationalStateException When operational status problem detected.
     */
    void checkHealthyState() throws UnoperationalStateException;

    /**
     * Activate the resource or its adapter to become healthy and operable. For example, this method can initialize a connection when is an adapter.
     *
     * @throws UnoperationalStateException When activation is not realized with success.
     */
    void enable() throws UnoperationalStateException;

    /**
     * Stop or close the resource or its adapter which make this resource non-operable.
     *
     * @throws UnoperationalStateException When disabling occurred a problem.
     */
    void disable() throws UnoperationalStateException;

    /**
     * Restore a resource operational state to retrieve an operational status based on its current existing configuration.
     *
     * @throws UnoperationalStateException When operational status problem detected.
     */
    void resume() throws UnoperationalStateException;

}
