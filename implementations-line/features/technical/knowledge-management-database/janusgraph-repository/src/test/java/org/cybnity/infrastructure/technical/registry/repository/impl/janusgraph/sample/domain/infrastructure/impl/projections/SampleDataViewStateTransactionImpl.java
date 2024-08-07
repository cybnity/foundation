package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.ConcreteQueryEvent;
import org.cybnity.framework.domain.event.EventSpecification;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection.AbstractGraphDataViewTransactionImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change.CreateSampleDataViewVersion;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.change.UpgradeSampleDataViewVersion;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.read.FindSampleDataViewVersionByEqualsLabel;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Example of domain aggregate view (domain object data view projected) supporting a SampleDataView vertex type's lifecycle (e.g creation, upgrade/refresh, enhancement, remove) via transactions onto a graph.
 */
public class SampleDataViewStateTransactionImpl extends AbstractGraphDataViewTransactionImpl {

    /**
     * Label designing this type of data view lifecycle manager.
     * It's a logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     */
    public static final String LABEL = SampleDataView.class.getSimpleName();

    /**
     * Set of actionable transaction around the changes detected as source of interest justifying a transaction execution on the domain aggregate view.
     */
    private Map<String, IProjectionTransaction> supportedTransactions;

    /**
     * Set of actionable queries as source of interest for data-model read justifying an operation execution on the domain aggregate view.
     */
    private Map<String, IProjectionRead> supportedQueries;

    /**
     * Default constructor regarding a graph read model projection.
     *
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @param dataModel Mandatory database model that can be manipulated by this transaction about its data view(s).
     * @param observer  Optional observer of the transaction state evolution (e.g to be notified about progress or end of performed transaction).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public SampleDataViewStateTransactionImpl(IDomainModel ownership, AbstractDomainGraphImpl dataModel, ITransactionStateObserver observer) throws IllegalArgumentException {
        super(LABEL, ownership, dataModel, observer); // Define graph manipulable
        initSupportedQueries();// Initialization of eligible delegated queries
        initSupportedTransactions(); // Initialization of eligible delegated transactions
    }

    /**
     * Define the set of transactions allowing management of the data views perimeter changes (e.g states refresh).
     * The defined transactions are responsible for state change of the data view.
     */
    private void initSupportedTransactions() {
        // Define the transaction supporting the global view perimeter
        supportedTransactions = new HashMap<>();
        IProjectionTransaction tx = new CreateSampleDataViewVersion(this, this.graphModel());
        for (IEventType type : tx.observerOf()) {
            supportedTransactions.put(/* supported event type as condition of transaction execution */ type.name(), /* transaction to execute */ tx);
        }
        tx = new UpgradeSampleDataViewVersion(this, this.graphModel());
        for (IEventType type : tx.observerOf()) {
            supportedTransactions.put(/* supported event type as condition of transaction execution */ type.name(), /* transaction to execute */ tx);
        }
    }

    /**
     * Define the set of queries allowing read of data-view states.
     * The defined queries are responsible for state access of the data view.
     */
    private void initSupportedQueries() {
        // Define the read operation supporting the global view perimeter
        supportedQueries = new HashMap<>();
        IProjectionRead op = new FindSampleDataViewVersionByEqualsLabel(this, this.graphModel());
        for (IEventType type : op.observerOf()) {
            supportedQueries.put(/* supported query type as condition of operation execution */ type.name(),/* operation to execute */op);
        }
    }

    @Override
    public boolean isSupportedQuery(IEventType queryType) {
        if (queryType != null) {
            // Check all event types supported by the queries perimeter
            return supportedQueries.containsKey(queryType.name());
        }
        return false;
    }

    @Override
    public Class<?> subscribeToEventType() {
        return DomainEvent.class; // Any domain event type are source of interest for this transaction monitoring the confirmation of created Sample domain object to decide creation/upgrade of write-model instance into the data-view model.
        // Else can return a specific event type relative to the unique supported by the handleEvent(...) method
        // Else null to indicate that is interested to be handled for any type of domain event
    }

    /**
     * This method execution is based on any explicit call which is not based on an automatic write-model change detection.
     * Execute the explicit and requested command (e.g data view creation, change or procedure execution with or without collect of results).
     *
     * @param command Mandatory change or query command (CQRS pattern's input element) relative to the projection that can be performed.
     * @return Provider of optional data-view status collected as request results.
     * @throws IllegalArgumentException      When any mandatory parameter is missing.
     * @throws UnsupportedOperationException When request execution generated an issue (e.g query not supported by this projection; or error of request parameter types).
     * @throws UnoperationalStateException When query execution technical problem occurred.
     */
    @Override
    public IQueryResponse when(Command command) throws IllegalArgumentException, UnsupportedOperationException, UnoperationalStateException {
        if (command == null) throw new IllegalArgumentException("Command parameter is required!");
        try {
            // Identify the type of event which should be source of interest (or not) regarding this projection managed perimeter
            // Normally interpretation of event can be based on its specific domain type (e.g concrete domain event type), or based on specific specification attribute read from event, to detect the source of interest
            // Here, for example, this implementation check the type of attribute relative to the type of origin domain object concerned by the domain command
            IProjectionRead op;
            Attribute at = EventSpecification.findSpecificationByName(Command.TYPE, command.specification());
            if (at != null) {
                // Identify existing operation to execute about query type
                op = supportedQueries.get(at.value());
                if (op != null) {
                    // Execute the query operation that is interested in the monitored event
                    return op.when(command);
                }
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(e); // query execution problem
        }
        return Optional::empty; // Default answer
    }

    @Override
    public void handleEvent(DomainEvent evt) {
        if (evt != null) {
            // Identify the type of event which should be source of interest (or not) regarding this projection managed perimeter
            // Normally interpretation of event can be based on its specific domain type (e.g concrete domain event type), or based on specific specification attribute read from event, to detect the source of interest
            // Here, for example, this implementation check the type of attribute relative to the type of origin domain object concerned by the domain event
            IProjectionTransaction tx;
            Attribute at = EventSpecification.findSpecificationByName(ConcreteDomainChangeEvent.TYPE, evt.specification());
            if (at != null) {
                // Identify existing transaction to execute about event type
                tx = supportedTransactions.get(at.value());
                if (tx != null) {
                    // Execute the transaction that is interested in the monitored event
                    tx.when(evt);
                }
            }
        }
    }

}
