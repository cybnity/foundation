package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change;

import org.apache.tinkerpop.gremlin.process.traversal.Merge;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.DomainEventFactory;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.infrastructure.util.DateConvention;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.sql.Date;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;

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
                    Attribute sourceDomainObjectIdCorrelatedAsDataViewUUID = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), event.specification());
                    if (sourceDomainObjectIdCorrelatedAsDataViewUUID != null) {
                        DateFormat formatter = DateConvention.dateFormatter();

                        Map<Object, Object> searchFilter = new HashMap<>();
                        searchFilter.put(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), sourceDomainObjectIdCorrelatedAsDataViewUUID.value());
                        searchFilter.put(/* vertex nature label*/ T.label, domainNodeType);

                        // Define properties to be updated in existing data view (vertex)
                        Map<Object, Object> updatedProperties = new HashMap<>();
                        Attribute dataViewName = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.NAME.name(), event.specification());
                        if (dataViewName != null && dataViewName.value() != null && !dataViewName.value().isEmpty()) {
                            updatedProperties.put(/* Name property */"name", dataViewName.value());
                        }
                        Attribute dataViewCreatedAt = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.CREATED.name(), event.specification());
                        if (dataViewCreatedAt != null && dataViewCreatedAt.value() != null && !dataViewCreatedAt.value().isEmpty()) {
                            updatedProperties.put(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.parse(dataViewCreatedAt.value()));
                        }
                        Attribute commitVersion = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), event.specification());
                        if (commitVersion != null && commitVersion.value() != null && !commitVersion.value().isEmpty()) {
                            updatedProperties.put(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), commitVersion.value());
                        }
                        // Update the date of refreshed data view
                        updatedProperties.put(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), Date.from(Instant.now()));

                        // Update the changed domain object attributes into the data view
                        // See https://javadoc.io/doc/org.janusgraph/janusgraph-core/latest/index.html for implementation help
                        final Vertex dataViewVertex = gtx.mergeV(searchFilter).option(Merge.onMatch, updatedProperties).next();
                        tx.commit(); // commit execution

                        // --- READ-MODEL PROJECTION CHANGE NOTIFICATION ---
                        // Prepare domain event relative to the read-model projection perimeter (e.g including one or several Vertex, edges, attributes...) that could interest read-model observers
                        Collection<Attribute> dataViewChangeDefinition = new HashSet<>(); //  // Can contain set of any technical information (e.g time of update, id of graph element changed) and/or logical information (e.g detail about relation name changed on Vertex)
                        dataViewChangeDefinition.add(new Attribute("dataViewId", dataViewVertex.id().toString()));
                        dataViewChangeDefinition.add(new Attribute("viewOfDomainObjectId", sourceDomainObjectIdCorrelatedAsDataViewUUID.value()));
                        dataViewChangeDefinition.add(new Attribute("dataViewLabel", domainNodeType));
                        dataViewChangeDefinition.add(new Attribute("dataViewName", dataViewVertex.value("name") /* Current last version name (upgraded or not) */));

                        DomainEvent dataViewChanged = DomainEventFactory.create(/* Event type relative to data view performed operation */ SampleDomainEventType.SAMPLE_DATAVIEW_REFRESHED.name(),
                                /* UUID of change event performed under the transaction */ new DomainEntity(IdentifierStringBased.generate(null)),
                                /* Logical information relative to the changed data view vertex and/or attributes, and/or any information about the transaction realized */ dataViewChangeDefinition,
                                /* original event reference that was previous source of this event publication */ evt.reference(),
                                /* Identify the element of the domain model which was subject of domain event */ evt.changedModelElementReference());

                        // Notify the changed data view status of this projection relative to the transaction monitored
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
