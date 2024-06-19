package org.cybnity.framework.domain.application;

import org.cybnity.framework.domain.IBoundedContext;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;

/**
 * Example of specific domain context providing resources reusable by any
 * component into the same domain.
 * 
 * @author olivier
 *
 */
public interface IUserAccountManagementDomainContext extends IBoundedContext {

    /**
     * Get the data store supporting the write model of domain objects.
     * 
     * @return A store instance.
     */
    UserAccountStore getWriteModelStore();
}
