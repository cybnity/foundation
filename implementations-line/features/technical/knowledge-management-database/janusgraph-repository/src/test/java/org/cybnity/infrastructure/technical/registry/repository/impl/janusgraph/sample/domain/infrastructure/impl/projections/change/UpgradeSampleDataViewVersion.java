package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.apache.tinkerpop.gremlin.process.traversal.Merge;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Example of utility class implementing the Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a change directive.
 * Implementation about DomainEventType.SAMPLE_AGGREGATE_CHANGED domain event type.
 */
public class UpgradeSampleDataViewVersion extends AbstractDataViewVersionTransactionImpl implements IProjectionTransaction {

    /**
     * Manipulated graph model.
     */
    private final AbstractDomainGraphImpl graph;

    /**
     * Default constructor.
     *
     * @param notifiable Optional observer of transaction execution end which can be notified.
     * @param graph      Mandatory manipulable graph.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public UpgradeSampleDataViewVersion(AbstractGraphDataViewTransactionImpl notifiable, AbstractDomainGraphImpl graph) throws IllegalArgumentException {
        super(notifiable);
        if (graph == null) throw new IllegalArgumentException("Graph parameter is required!");
        this.graph = graph;
    }

    /**
     * Example of only creation change event that is supported by this transaction, as a directive to operate on data-view model.
     *
     * @return A set including only DomainEventType.SAMPLE_AGGREGATE_CHANGED event type as source of interest.
     */
    @Override
    public Set<IEventType> observerOf() {
        return Set.of(SampleDomainEventType.SAMPLE_AGGREGATE_CHANGED);
    }

    @Override
    public void when(DomainEvent event) {
        if (event != null) {
            // Identify and check that is a supported event type
            ConcreteDomainChangeEvent evt = (ConcreteDomainChangeEvent) event;
            if (evt.type().value().equals(SampleDomainEventType.SAMPLE_AGGREGATE_CHANGED.name())) {
                // A write-model regarding domain aggregate Sample object is notified as created
                // So a new data view shall be created as read-model projection (data view)
                Transaction tx = null;
                try {
                    // Open a traversal allowing graph manipulation
                    GraphTraversalSource source = graph.open();
                    // Initialize transaction
                    tx = source.tx();
                    GraphTraversalSource gtx = tx.begin();
                    gtx.tx().rollback();// Force refresh of transaction state about potential parallel changes executed on data-view to search

                    // Find existing data view node based on its domain object UUID attribute
                    String domainNodeType = SampleDataView.class.getSimpleName();
                    Attribute dataViewId = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), event.specification());
                    if (dataViewId != null) {
                        DateFormat formatter = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);

                        Map<Object, Object> searchFilter = new HashMap<>();
                        searchFilter.put(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), dataViewId.value());
                        searchFilter.put(/* vertex nature label*/ T.label, domainNodeType);

                        // Define properties to be updated in existing data view (vertex)
                        Map<Object, Object> updatedProperties = new HashMap<>();
                        Attribute dataViewName = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.NAME.name(), event.specification());
                        if (dataViewName!=null && dataViewName.value()!=null && !dataViewName.value().isEmpty()) {
                            updatedProperties.put(/* Name property */"name", dataViewName.value());
                        }
                        Attribute dataViewCreatedAt = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.CREATED.name(), event.specification());
                        if (dataViewCreatedAt!=null && dataViewCreatedAt.value()!=null && !dataViewCreatedAt.value().isEmpty()) {
                            updatedProperties.put(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.parse(dataViewCreatedAt.value()));
                        }
                        Attribute commitVersion = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), event.specification());
                        if (commitVersion!=null && commitVersion.value()!=null && !commitVersion.value().isEmpty()) {
                            updatedProperties.put(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(),commitVersion.value());
                        }
                        // Update the date of refreshed data view
                        updatedProperties.put(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), Date.from(Instant.now()));

                        // Update the changed domain object attributes into the data view
                        // See https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/index.html for implementation help
                        gtx.mergeV(searchFilter).option(Merge.onMatch,updatedProperties).next();
                        tx.commit(); // commit execution

                        // Notify the changed graph status
                        // TODO create a transaction end notification about execution finalized (e.g identified data-view changed, version...) with/without result
                        // TODO prepare result event
                        DomainEvent dataViewChanged = null;
                        notifyEvent(dataViewChanged);
                    } // Else impossible to upgrade an existing data-view regarding a non unique identified domain object!
                } catch (Exception e) {
                    if (graph.isSupportsTransactions() && tx != null) {
                        tx.rollback();
                    }
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
