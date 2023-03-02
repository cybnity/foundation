package org.cybnity.framework.domain.application.sample;

import org.cybnity.framework.Context;
import org.cybnity.framework.domain.application.IUserAccountManagementDomainContext;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;

/**
 * Example of a bounded context utility class managing informations specific to
 * an application domain.
 * 
 * @author olivier
 *
 */
public class UserAccountManagementDomainContext extends Context implements IUserAccountManagementDomainContext {

    public UserAccountManagementDomainContext() {
	super();
    }

    /**
     * Get a singleton instance of the write model store regarding
     * UserAccountAggregate.
     */
    @Override
    public UserAccountStore getWriteModelStore() {
	return (UserAccountStore) this.get(UserAccountStore.class.getName());
    }

}
