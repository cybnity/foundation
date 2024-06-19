package org.cybnity.framework.domain.model.sample;

import org.cybnity.framework.domain.model.IDomainModel;

/**
 * Sample of domain name.
 */
public class AccessControlDomain implements IDomainModel {
    @Override
    public String domainName() {
        return "access-control";
    }
}
