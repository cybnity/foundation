package org.cybnity.framework.domain;

import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.model.Tenant;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a context of informations (e.g properties, resources url, settings)
 * managed and provisionned during interaction moment (e.g during a
 * communication between several systems or users).
 * 
 * For example, this type of context can specifically provide informations
 * regarding an authorized session which are reusable by system components
 * requiring to check specific authorized and accessible properties opened
 * specifically during a system usage moment.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface ISessionContext extends IContext {

    /**
     * Get the tenant relative to this session.
     * 
     * @return A tenant or null.
     */
    public Tenant tenant();
}
