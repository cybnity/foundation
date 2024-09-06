package org.cybnity.framework.domain;

/**
 * Clean up contract ensuring free up of previous allocated resources (e.g file opened, stream listening, memory allowed...).
 */
public interface ICleanup {

    /**
     * Clean resources allocated previously.
     * For example, disconnect a component from another system.
     */
    public void freeUpResources();
}
