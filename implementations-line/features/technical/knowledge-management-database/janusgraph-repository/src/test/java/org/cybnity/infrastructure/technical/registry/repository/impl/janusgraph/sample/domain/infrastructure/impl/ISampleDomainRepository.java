package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.infrastructure.IDomainRepository;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

/**
 * Example of repository read-model perimeter providing business methods.
 */
public interface ISampleDomainRepository extends IDomainRepository<SampleDataView> {

    /**
     * Drops the repository schema and data of the graph instance.
     *
     * @throws UnoperationalStateException Problem occurred during the attempt to close the graph or to perform the schema/data deletion.
     */
    public void drop() throws UnoperationalStateException;
}
