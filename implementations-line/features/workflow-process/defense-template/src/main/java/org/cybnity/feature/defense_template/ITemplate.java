package org.cybnity.feature.defense_template;

import org.cybnity.framework.immutable.ImmutabilityException;
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
	 * @return An immutable version named type (e.g risk assessment process) or
	 *         null.
	 * @throws ImmutabilityException When problem of immutable version
	 *                               instantiation.
	 */
	public DomainObjectType type() throws ImmutabilityException;
}
