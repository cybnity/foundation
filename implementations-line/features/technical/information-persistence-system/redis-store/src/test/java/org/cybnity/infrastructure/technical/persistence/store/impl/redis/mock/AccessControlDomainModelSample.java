package org.cybnity.infrastructure.technical.persistence.store.impl.redis.mock;

import org.cybnity.framework.domain.model.IDomainModel;

/**
 * Example of Access Control application domain model definition.
 */
public class AccessControlDomainModelSample implements IDomainModel {

    @Override
    public String domainName() {
        return "ac";
    }
}
