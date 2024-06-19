package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent the basic and common contract of value providing regarding an
 * configuration element usable by a application or service component.
 * 
 * As defined by Security Policy, any component of the system shall be
 * configurable via settings (e.g environment variables, files, remote resources
 * values) that reflect the most restrictive mode consistent with operational
 * requirements. Environment variables definition is ensured by each
 * implementation component that identify the enumeration of variables that are
 * used in the application source codes.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_8370_CM6")
public interface IReadableConfiguration {

    /**
     * Get the current name of this configuration variable.
     * 
     * @return A value.
     */
    String getName();
}
