package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.DomainEventFactory;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
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
            if (event.type().value().equals(SampleDomainEventType.SAMPLE_AGGREGATE_CREATED.name())) {
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
                    DateFormat formatter = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);

                    // Map origin domain object attributes from event to targeted (and normally satisfying completeness) data view type
                    SampleDataView expectedView = new SampleDataViewMapper().convertTo(event);

                    // Execute the transaction creating a new graph vertex
                    final Vertex dataViewVertex = gtx.addV(/* Vertex label nature */domainNodeType)
                            .property(/* Name property */"name", expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.NAME))
                            .property(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY))
                            .property(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.parse(expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.CREATED)))
                            .property(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), formatter.parse(expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT)))
                            .property(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.COMMIT_VERSION))
                            .next();
                    tx.commit(); // commit execution

                    // --- READ-MODEL PROJECTION CHANGE NOTIFICATION ---
                    // Prepare domain event relative to the read-model projection perimeter (e.g including one or several Vertex, edges, attributes...) that could interest read-model observers
                    Collection<Attribute> dataViewChangeDefinition = new HashSet<>(); //  // Can contain set of any technical information (e.g time of update, id of graph element changed) and/or logical information (e.g detail about relation name changed on Vertex)
                    dataViewChangeDefinition.add(new Attribute("dataViewId", dataViewVertex.id().toString()));
                    dataViewChangeDefinition.add(new Attribute("viewOfDomainObjectId", expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY)));
                    dataViewChangeDefinition.add(new Attribute("dataViewLabel", domainNodeType));
                    dataViewChangeDefinition.add(new Attribute("dataViewName", expectedView.valueOfProperty(SampleDataView.PropertyAttributeKey.NAME)));

                    DomainEvent dataViewChanged = DomainEventFactory.create(/* Event type relative to data view performed operation */ SampleDomainEventType.SAMPLE_DATAVIEW_CREATED.name(),
                            /* UUID of change event performed under the transaction */ new DomainEntity(IdentifierStringBased.generate(null)),
                            /* Logical information relative to the changed data view vertex and/or attributes, and/or any information about the transaction realized */ dataViewChangeDefinition,
                            /* original event reference that was previous source of this event publication */ event.reference(),
                            /* Identify the element of the domain model which was subject of domain event */(ConcreteDomainChangeEvent.class.isAssignableFrom(event.getClass())) ? ((ConcreteDomainChangeEvent) event).changedModelElementReference() : null);

                    // Notify the changed data view status of this projection relative to the transaction monitored
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
