package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event;

import org.cybnity.framework.domain.event.IEventType;

/**
 * Example of event type supported by the SampleDomain.
 */
public enum SampleDomainEventType implements IEventType  {
    SAMPLE_AGGREGATE_CREATED,
    SAMPLE_AGGREGATE_CHANGED,
    SAMPLE_AGGREGATE_DELETED
    ;

    private SampleDomainEventType() {
    }
}
