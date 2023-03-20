package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Basic enumeration defining a set of variables (e.g port of server started,
 * current value of a runtime setting) usable by a component.
 * 
 * Each defined sub-enumeration shall be defined by the current runtime context
 * (e.g operating system).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public enum BasicConfigurationVariable implements IReadableConfiguration {
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
    private BasicConfigurationVariable(String aName) throws IllegalArgumentException {
	if (aName == null || "".equalsIgnoreCase(aName))
	    throw new IllegalArgumentException("The name of this variable shall be defined!");
	this.name = aName;
    }

    @Override
    public String getName() {
	return this.name;
    }

}
