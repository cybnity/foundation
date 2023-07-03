package org.cybnity.feature.defense_template.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a contract supported by a topic which can be reused as template.
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
	public String name();

	/**
	 * Get the description about the type of this template.
	 * 
	 * @return A named type (e.g risk assessment process) or null.
	 */
	public DomainObjectType type();
}
