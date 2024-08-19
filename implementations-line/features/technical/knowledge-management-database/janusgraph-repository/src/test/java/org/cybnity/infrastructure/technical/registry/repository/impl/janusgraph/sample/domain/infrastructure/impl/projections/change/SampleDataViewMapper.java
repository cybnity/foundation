package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.cybnity.framework.domain.AbstractDTOMapper;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.infrastructure.util.DateConvention;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Example of DTO mapping implementation class ensuring the preparation of a data view version.
 */
public class SampleDataViewMapper extends AbstractDTOMapper<SampleDataView> {

    /**
     * Example implementation that convert a collection of attributes relative to a ConcreteDomainChangeEvent, into a degraded data view version supported by a read-model projection.
     *
     * @param source Mandatory ConcreteDomainChangeEvent object type which is provider of attributes to read.
     * @return An instantiated version of data view including only the data that make sens for a projection.
     * @throws IllegalArgumentException      When mandatory parameter is not defined.
     * @throws UnsupportedOperationException When impossible to read attributes that are required for sampled data view instance creation.
     */
    @Override
    public SampleDataView convertTo(Object source) throws IllegalArgumentException, UnsupportedOperationException {
        if (source == null) throw new IllegalArgumentException("Source parameter is required!");
        // Identify and check that is a supported event type
        try {
            // Check to cast into the expected type supported for read of attributes
            DomainEvent event = (DomainEvent) source;
            if (!event.type().value().equals(SampleDomainEventType.SAMPLE_AGGREGATE_CREATED.name()))
                throw new IllegalArgumentException("Invalid expected type (" + SampleDomainEventType.SAMPLE_AGGREGATE_CREATED.name() + ") when event type value have been read!");

            // Read source data values
            DateFormat formatter = DateConvention.dateFormatter();
            Attribute dataViewName = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.NAME.name(), event.specification());
            String label = (dataViewName!=null)?dataViewName.value():null;

            Attribute sourceDomainObjectIdCorrelatedAsDataViewUUID = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), event.specification());
            String identifier = (sourceDomainObjectIdCorrelatedAsDataViewUUID!=null)?sourceDomainObjectIdCorrelatedAsDataViewUUID.value():null;

            Attribute dataViewCreatedAt = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.CREATED.name(), event.specification());
            Date createdAt = (dataViewCreatedAt!=null)?formatter.parse(dataViewCreatedAt.value()):null;

            Attribute commitVersion = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), event.specification());
            String committedVersion = (commitVersion!=null)?commitVersion.value():null;

            // Try instantiation which is responsible for mandatory information required for creation
            return new SampleDataView(identifier, label, createdAt, committedVersion, Date.from(Instant.now()));
        } catch (Exception cce) {
            throw new UnsupportedOperationException("Invalid source parameter!");
        }
    }
}
