package org.cybnity.framework.application.vertx.common.module;

/**
 * Contract of configuration relative to a module settings supporting its specificities.
 */
public interface ConfigurableModule {

    /**
     * Name of the pool including all the executed workers managed by this module.
     * @return A name.
     */
    public String poolName();

    /**
     * Get unique logical name of this domain of features.
     * @return A label.
     */
    public String featuresDomainName();

    /**
     * Get unique logical name of this processing module.
     * @return A label.
     */
    public String processUnitLogicalName();
}
