package org.cybnity.infrastructure.technical.registry.adapter.api;

import org.cybnity.framework.UnoperationalStateException;

/**
 * Knowledge Repository adapter which expose the API services that allow interactions with the knowledge database.
 */
public interface KNRAdapter {

    /**
     * For example, disconnect the adapter from the Knowledge Repository.
     */
    void freeResources();

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the knowledge repository.
     *
     * @throws UnoperationalStateException When adapter status problem detected.
     */
    void checkHealthyState() throws UnoperationalStateException;
}
