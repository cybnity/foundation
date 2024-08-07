package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Set;

/**
 * Example of utility class implementing the Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a change directive.
 * Implementation about DomainEventType.SAMPLE_AGGREGATE_CREATED domain event type.
 */
public class CreateSampleDataViewVersion extends AbstractDataViewVersionTransactionImpl implements IProjectionTransaction {

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
    public CreateSampleDataViewVersion(ITransactionStateObserver notifiable, AbstractDomainGraphImpl graph) throws IllegalArgumentException {
        super(notifiable);
        if (graph == null) throw new IllegalArgumentException("Graph parameter is required!");
        this.graph = graph;
    }

    /**
     * Example of only creation change event that is supported by this transaction, as a directive to operate on data-view model.
     *
     * @return A set including only DomainEventType.SAMPLE_AGGREGATE_CREATED event type as source of interest.
     */
    @Override
    public Set<IEventType> observerOf() {
        return Set.of(SampleDomainEventType.SAMPLE_AGGREGATE_CREATED);
    }

    @Override
    public void when(DomainEvent event) {
        if (event != null) {
            // Identify and check that is a supported event type
            ConcreteDomainChangeEvent evt = (ConcreteDomainChangeEvent) event;
            if (evt.type().value().equals(SampleDomainEventType.SAMPLE_AGGREGATE_CREATED.name())) {
                // A write-model regarding domain aggregate Sample object is notified as created
                // So a new data view shall be created as read-model projection (data view)
                Transaction tx = null;
                try {
                    // Open a traversal allowing graph manipulation
                    GraphTraversalSource source = graph.open();
                    // Initialize transaction
                    tx = source.tx();
                    GraphTraversalSource gtx = tx.begin();

                    // Define vertex description to create
                    String domainNodeType = SampleDataView.class.getSimpleName();
                    Attribute dataViewName = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.NAME.name(), event.specification());
                    Attribute dataViewId = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), event.specification());
                    Attribute dataViewCreatedAt = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.CREATED.name(), event.specification());
                    Attribute commitVersion = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), event.specification());
                    DateFormat formatter = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);

                    // Execute the transaction creating a new graph vertex
                    final Vertex dataViewVertex = gtx.addV(/* Vertex nature */domainNodeType)
                            .property(/* Name property */"name", dataViewName.value())
                            .property(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), dataViewId.value())
                            .property(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.parse(dataViewCreatedAt.value()))
                            .property(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), Date.from(Instant.now()))
                            .property(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), commitVersion.value())
                            .next();
                    tx.commit(); // commit creation

                    // Notify the changed graph status
                    // TODO create a transaction end notification about execution finalized (e.g identified data-view changed, version...) with/without result
                    // TODO prepare result event
                    DomainEvent dataViewChanged = null;
                    notifyEvent(dataViewChanged);
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
