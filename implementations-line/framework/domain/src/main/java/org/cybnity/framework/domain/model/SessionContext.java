package org.cybnity.framework.domain.model;

import org.cybnity.framework.Context;
import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Implementation class regarding a session context (e.g allowed to a principal
 * and user during a system usage temporal period).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Security, reqId = "REQ_SEC_3")
public class SessionContext extends Context implements ISessionContext {

    /**
     * Naming attributes catalog regarding the resources managed by this context.
     */
    private enum ResourceName {
	TENANT
    }

    /**
     * Default constructor of context relative to a tenant.
     * 
     * @param tenant Optional tenant.
     */
    public SessionContext(Tenant tenant) {
	super();
	if (tenant != null)
	    // Save in this context
	    this.addResource(tenant, ResourceName.TENANT.name(), true);
    }

    /**
     * Get optional tenant instance regarding the current context.
     * 
     * @return A tenant or null.
     */
    @Override
    public Tenant tenant() {
	return (Tenant) get(ResourceName.TENANT.name());
    }

}
