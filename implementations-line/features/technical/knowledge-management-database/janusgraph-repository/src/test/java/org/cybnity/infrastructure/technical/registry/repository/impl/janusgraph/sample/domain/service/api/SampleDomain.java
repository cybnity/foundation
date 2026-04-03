package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api;

import org.cybnity.framework.domain.model.IDomainModel;

/**
 * Example of an application domain.
 */
public class SampleDomain implements IDomainModel {
    @Override
    public String domainName() {
        return "sd";
    }
}
