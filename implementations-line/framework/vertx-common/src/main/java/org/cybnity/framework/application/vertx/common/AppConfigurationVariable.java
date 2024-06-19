package org.cybnity.framework.application.vertx.common;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Enumeration defining a set of variables regarding an IO Gateway module.
 * <p>
 * The configuration of each value regarding each environment variable enum, is
 * managed into the Helm values.yaml file regarding the executable system which
 * need to declare the environment variables as available for usage via this set
 * of enum.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum AppConfigurationVariable implements IReadableConfiguration {

    /**
     * HTTP port listened by the module server as entry point.
     */
    ENDPOINT_HTTP_SERVER_PORT("ENDPOINT_HTTP_SERVER_PORT"),

    /**
     * Quantity of thread available as assignable to worker instances to perform a task.
     */
    DOMAIN_WORKER_THREAD_POOL_SIZE("DOMAIN_WORKER_THREAD_POOL_SIZE"),

    /**
     * Quantity of instances deployed in pool regarding each worker type.
     */
    DOMAIN_WORKER_INSTANCES("DOMAIN_WORKER_INSTANCES");

    /**
     * Name of this environment variable currently hosted by the system environment.
     */
    private final String name;

    /**
     * Default constructor of a configuration variable that is readable from the
     * system environment variables set.
     *
     * @param aName Mandatory name of the environment variable that is readable from
     *              the current system environment (e.g defined by the runtime
     *              container or operating system).
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    AppConfigurationVariable(String aName) throws IllegalArgumentException {
        if (aName == null || "".equalsIgnoreCase(aName))
            throw new IllegalArgumentException("The name of this variable shall be defined!");
        this.name = aName;
    }

    @Override
    public String getName() {
        return this.name;
    }
}