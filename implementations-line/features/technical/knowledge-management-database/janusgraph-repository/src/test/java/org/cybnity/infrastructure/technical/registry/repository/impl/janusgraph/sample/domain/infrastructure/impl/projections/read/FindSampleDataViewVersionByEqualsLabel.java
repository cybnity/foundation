package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.read;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainQueryEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Example of utility class implementing the Query Language supported by the graph model (e.g Gremlin with TinkerPop) for execution of a query directive.
 * Implementation about DomainEventType.SAMPLE_AGGREGATE_FIND_BY_LABEL domain query command type.
 */
public class FindSampleDataViewVersionByEqualsLabel extends AbstractDataViewVersionTransactionImpl implements IProjectionRead {

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
    public FindSampleDataViewVersionByEqualsLabel(AbstractGraphDataViewTransactionImpl notifiable, AbstractDomainGraphImpl graph) throws IllegalArgumentException {
        super(notifiable);
        if (graph == null) throw new IllegalArgumentException("Graph parameter is required!");
        this.graph = graph;
    }

    /**
     * Example of only read query event that is supported by this transaction, as a directive to operate on data-view model.
     *
     * @return A set including only DomainEventType.SAMPLE_AGGREGATE_FIND_BY_LABEL event type as source of interest.
     */
    @Override
    public Set<IEventType> observerOf() {
        return Set.of(SampleDomainQueryEventType.SAMPLE_DATAVIEW_FIND_BY_LABEL);
    }

    @Override
    public IQueryResponse when(Command command) throws IllegalArgumentException, UnsupportedOperationException, UnoperationalStateException {
        if (command == null) throw new IllegalArgumentException("Command parameter is required!");
        // Check valid query type definition which can be processed by this projection read function
        Attribute queryType = EventSpecification.findSpecificationByName(Command.TYPE, command.specification());
        // Verify if event type supported by this transaction
        if (observerOf().contains(Enum.valueOf(SampleDomainQueryEventType.class, queryType.value()))) {
            // Read query parameters
            Attribute labelFilter = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.NAME.name(), command.specification());
            // Check mandatory search parameter provided
            if (labelFilter == null || labelFilter.value() == null || labelFilter.value().isEmpty())
                throw new IllegalArgumentException("Invalid transaction parameter (SampleDataView.PropertyAttributeKey.NAME.name() is required)!");
            String sampleDataViewNameLabelFilter = labelFilter.value();

            Attribute dataViewType = EventSpecification.findSpecificationByName(SampleDataView.PropertyAttributeKey.DATAVIEW_TYPE.name(), command.specification());
            // Check mandatory domain object type (data view) to filter
            if (dataViewType == null || dataViewType.value() == null || dataViewType.value().isEmpty())
                throw new IllegalArgumentException("Missing mandatory parameter (SampleDataView.PropertyAttributeKey.DATAVIEW_TYPE.name() is required and shall be defined)!");

            try {
                // Type of node can be statically defined by the implementation language (like here) or dynamically known by the requester (in case, use sampleDataViewType value)
                String domainNodeType = (dataViewType.value() != null && !dataViewType.value().isEmpty()) ? dataViewType.value() : SampleDataView.class.getSimpleName();
                GraphTraversalSource traversal = graph.open();
                GraphTraversalSource gtx = traversal.tx().begin();
                gtx.tx().rollback();// Force refresh of transaction state about potential parallel changes executed on data-view to search

                // Execute query
                Vertex foundEqualsLabelNode = gtx.V().has(T.label, domainNodeType /* vertex node type only consulted */).has("name", sampleDataViewNameLabelFilter).next();
                String dataViewId = foundEqualsLabelNode.value(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name());
                Date createdAt = foundEqualsLabelNode.value(SampleDataView.PropertyAttributeKey.CREATED.name());
                Date updatedAt = foundEqualsLabelNode.value(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name());
                String commitVersion = foundEqualsLabelNode.value(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name());
                // Prepare result response
                return () -> {
                    SampleDataView view = new SampleDataView(dataViewId, sampleDataViewNameLabelFilter, createdAt, commitVersion, updatedAt);
                    return Optional.of(view);
                };
            } catch (NoSuchElementException nse) {
                // None found query result
            } catch (Exception e) {
                throw new UnoperationalStateException(e);
            }

            // Confirm none found result
            return Optional::empty; // Default answer
        } else {
            throw new UnsupportedOperationException("Not supported command type!");
        }
    }

}
