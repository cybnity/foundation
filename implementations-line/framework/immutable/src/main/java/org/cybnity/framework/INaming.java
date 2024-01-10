package org.cybnity.framework;

/**
 * Generic contract regarding a capability of component or subject to be identified by a logical name.
 */
public interface INaming {

    /**
     * Get the logical name.
     *
     * @return A logical name.
     */
    public String name();
}
