package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event;

import org.cybnity.framework.domain.event.IEventType;

/**
 * Example of query command type supported by the SampleDomain.
 */
public enum SampleDomainQueryEventType implements IEventType  {

    SAMPLE_DATAVIEW_FIND_BY_LABEL
    ;

    private SampleDomainQueryEventType() {
    }
}
