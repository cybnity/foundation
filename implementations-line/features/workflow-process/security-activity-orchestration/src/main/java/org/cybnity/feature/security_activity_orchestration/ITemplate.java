package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a contract of templating regarding an information.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface ITemplate {

	/**
	 * Get the logical name this object model identified as a template.
	 * 
	 * @return A template name or null.
	 */
	public Attribute name();
}
