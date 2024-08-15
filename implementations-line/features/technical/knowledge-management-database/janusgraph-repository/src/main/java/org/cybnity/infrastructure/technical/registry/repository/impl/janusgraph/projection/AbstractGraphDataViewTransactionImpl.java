package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IProjectionRead;
import org.cybnity.framework.domain.IProjectionTransaction;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.AbstractRealModelDataViewProjection;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph data view projection usable as read-model transaction, which defined the graph manipulation rules (e.g query with parameters; graph change directives and linking rules).
 * It manages the identification of one or several events as Data Layer inputs supported by a referenced graph model, and execute on it the read or change instructions on the graph model.
 */
public abstract class AbstractGraphDataViewTransactionImpl extends AbstractRealModelDataViewProjection {

    /**
     * Synchronized graph by this projection regarding the scope of denormalized data.
     * This instance is managing the graph schema creation, update, query according to the supported implementation technology (e.g TinkerPop).
     */
    private final AbstractDomainGraphImpl graphModel;

    /**
     * Set of actionable transaction around the changes detected as source of interest justifying a transaction execution on the domain aggregate view.
     */
    private final Map<String, IProjectionTransaction> supportedTransactions;

    /**
     * Set of actionable queries as source of interest for data-model read justifying an operation execution on the domain aggregate view.
     */
    private final Map<String, IProjectionRead> supportedQueries;

    /**
     * Default constructor regarding a graph read model projection.
     * Automatically initialize the supported queries and transactions.
     *
     * @param label     Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @param dataModel Mandatory database model that can be manipulated by this transaction about its data view(s).
     * @param observer  Optional observer of the transaction state evolution (e.g to be notified about progress or end of performed transaction).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public AbstractGraphDataViewTransactionImpl(String label, IDomainModel ownership, AbstractDomainGraphImpl dataModel, ITransactionStateObserver observer) throws IllegalArgumentException {
        super(label, ownership, observer);
        if (dataModel == null) throw new IllegalArgumentException("dataModel parameter is required!");

        // Initialize containers of projections
        supportedTransactions = new HashMap<>();
        supportedQueries = new HashMap<>();
        this.graphModel = dataModel;

        // Make initialization of supported transactions and queries
        initSupportedQueries();// Initialization of eligible delegated queries
        initSupportedTransactions(); // Initialization of eligible delegated transactions
    }
    /**
     * Define the set of transactions allowing management of the data views perimeter changes (e.g states refresh).
     * The defined transactions are responsible for state change of the data view.
     * To be redefined with add of IProjectionTransaction items into the supportedTransactions() container, according to the view's managed creation/upgrade operations.
     */
    protected abstract void initSupportedTransactions();

    /**
     * Define the set of queries allowing read of data-view states.
     * The defined queries are responsible for state access of the data view.
     * To be redefined with add of IProjectionRead items into the supportedQueries() container, according to the view's managed read operations.
     */
    protected abstract void initSupportedQueries();

    /**
     * Get set of actionable transaction around the changes detected as source of interest justifying a transaction execution on the domain aggregate view.
     * @return A container of transactions.
     */
    protected Map<String, IProjectionTransaction> supportedTransactions() {
        return this.supportedTransactions;
    }

    /**
     * get Set of actionable queries as source of interest for data-model read justifying an operation execution on the domain aggregate view.
     * @return A container of queries.
     */
    protected Map<String, IProjectionRead> supportedQueries() {
        return this.supportedQueries;
    }

    /**
     * Default implementation which check the supported queries container regarding the query type name.
     * @param queryType A query type to evaluate as supported.
     * @return False when not supported or when queryType parameter is null. True when supported query confirmed.
     */
    @Override
    public boolean isSupportedQuery(IEventType queryType) {
        if (queryType != null) {
            // Check all event types supported by the queries perimeter
            return supportedQueries().containsKey(queryType.name());
        }
        return false;
    }

    /**
     * Get the graph model that support the scope of data view manipulated by this transaction.
     *
     * @return A model.
     */
    protected AbstractDomainGraphImpl graphModel() {
        return this.graphModel;
    }

    @Override
    public void notifyTransactionState(DomainEvent domainEvent) {
        if (domainEvent != null) {
            ITransactionStateObserver notifiable = getObserver();
            if (notifiable != null)
                notifiable.notifyTransactionState(domainEvent);
        }
    }

    /**
     * Create or synchronize the graph model instance supporting this real-mode projection.
     *
     * @throws UnoperationalStateException When problem during the schema creation.
     */
    @Override
    public void activate() throws UnoperationalStateException {
        if (graphModel != null)
            // Create of synchronize the graph schema supporting this data view projection
            graphModel.createSchema();
    }

    /**
     * Delete the graph model instance supporting this real-mode projection.
     *
     * @throws UnoperationalStateException When problem during the schema dropping action.
     */
    @Override
    public void deactivate() throws UnoperationalStateException {
        if (graphModel != null)
            // Delete the graph schema supporting this data view projection
            graphModel.drop();
    }
}
