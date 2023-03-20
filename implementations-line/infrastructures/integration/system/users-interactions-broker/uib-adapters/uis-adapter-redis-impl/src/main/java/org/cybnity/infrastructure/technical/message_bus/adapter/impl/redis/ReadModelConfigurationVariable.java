package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Enumeration defining a set of variables regarding a ReadModel provided by the
 * Redis server.
 * 
 * The configuration of each value regarding each environment variable enum, is
 * managed into the Helm values.yaml file regarding the executable system which
 * need to declare the environment variables as available for usage via this set
 * of enum.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum ReadModelConfigurationVariable implements IReadableConfiguration {

    /**
     * Redis server instance regarding the read model instance
     */
    REDIS_READMODEL_SERVER_HOST("REDIS_READMODEL_SERVER_HOST"),
    /**
     * Redis server port regarding the read model instance
     */
    REDIS_READMODEL_SERVER_PORT("REDIS_READMODEL_SERVER_PORT"),
    /**
     * Redis Kubernetes configuration's REDISCLI_AUTH environment variable
     */
    REDIS_READMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD("REDISCLI_AUTH"),
    /**
     * Index of the database dedicated to the read model
     */
    REDIS_READMODEL_DATABASE_NUMBER("REDIS_READMODEL_DATABASE_NUMBER"),
    /**
     * Default user account usable on Redis server connection
     */
    REDIS_READMODEL_CONNECTION_DEFAULT_USERACCOUNT("REDIS_READMODEL_CONNECTION_DEFAULT_USERACCOUNT")

    ;

    /**
     * Name of this environment variable currently hosted by the system environment.
     */
    private String name;

    /**
     * Default constructor of a configuration variable that is readable from the
     * system environment variables set.
     * 
     * @param aName Mandatory name of the environment variable that is readable from
     *              the current system environment (e.g defined by the runtime
     *              container or operating system).
     * @throws IllegalArgumentException When mandatory parameter is not defined.
     */
    private ReadModelConfigurationVariable(String aName) throws IllegalArgumentException {
	if (aName == null || "".equalsIgnoreCase(aName))
	    throw new IllegalArgumentException("The name of this variable shall be defined!");
	this.name = aName;
    }

    @Override
    public String getName() {
	return this.name;
    }
}
