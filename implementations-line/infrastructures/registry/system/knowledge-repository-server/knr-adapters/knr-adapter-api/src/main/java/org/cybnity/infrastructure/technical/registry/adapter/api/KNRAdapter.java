package org.cybnity.infrastructure.technical.registry.adapter.api;

import org.cybnity.framework.UnoperationalStateException;

/**
 * KNowledge Repository adapter which expose the API services that allow interactions with the knowledge database.
 */
public interface KNRAdapter {

    /**
     * For example, disconnect the adapter from the Knowledge Repository.
     */
    public void freeUpResources();

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the knowledge Repository.
     *
     * @throws UnoperationalStateException When adapter status problem detected.
     */
    public void checkHealthyState() throws UnoperationalStateException;
}
