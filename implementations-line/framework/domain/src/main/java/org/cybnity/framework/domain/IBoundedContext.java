package org.cybnity.framework.domain;

import org.cybnity.framework.IContext;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a defined perimeter (e.g a core business domain, a subdomain of a
 * core domain or an area including some parts of several subdomains merged into
 * a mixed perimeter).
 * 
 * Include resources provided into this perimeter.
 * 
 * This contract can be specialized by a domain specific sub-interface providing
 * specific types of resources (e.g application services, user interface
 * properties, open host service settings) allowing to support modules,
 * aggregates, events and services components implemented in a domain.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IBoundedContext extends IContext {

}
