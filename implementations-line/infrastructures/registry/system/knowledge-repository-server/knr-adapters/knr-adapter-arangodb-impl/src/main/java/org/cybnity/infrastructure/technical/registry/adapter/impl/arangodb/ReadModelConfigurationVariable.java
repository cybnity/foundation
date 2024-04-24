package org.cybnity.infrastructure.technical.registry.adapter.impl.arangodb;

import org.cybnity.framework.IReadableConfiguration;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Enumeration defining a set of variables regarding a ReadModel provided by the
 * ArangoDB server.
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
     * ArangoDB server instance regarding the read model instance
     */
    ARANGODB_READMODEL_SERVER_HOST("ARANGODB_READMODEL_SERVER_HOST"),
    /**
     * ArangoDB server port regarding the read model instance
     */
    ARANGODB_READMODEL_SERVER_PORT("ARANGODB_READMODEL_SERVER_PORT"),
    /**
     * ArangoDB Kubernetes configuration's ARANGODBCLI_AUTH environment variable
     */
    //ARANGODB_READMODEL_CONNECTION_DEFAULT_AUTH_PASSWORD("REDISCLI_AUTH"),
    /**
     * Index of the database dedicated to the read model
     */
    //ARANGODB_READMODEL_DATABASE_NUMBER("ARANGODB_READMODEL_DATABASE_NUMBER"),
    /**
     * Default user account usable on ArangoDB server connection
     */
    //ARANGODB_READMODEL_CONNECTION_DEFAULT_USERACCOUNT("ARANGODB_READMODEL_CONNECTION_DEFAULT_USERACCOUNT")

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
