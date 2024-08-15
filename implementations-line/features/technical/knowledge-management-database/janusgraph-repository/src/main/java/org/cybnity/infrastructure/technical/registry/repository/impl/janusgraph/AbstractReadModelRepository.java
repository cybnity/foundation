package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IReadModelProjection;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.AbstractRealModelDataViewProjection;
import org.cybnity.framework.domain.model.IDomainEventSubscriber;
import org.cybnity.framework.domain.model.ITransactionStateObserver;
import org.cybnity.framework.domain.model.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Read-Model repository of data view managed regarding a scope.
 */
public abstract class AbstractReadModelRepository extends Repository implements IDomainEventSubscriber<DomainEvent>, ITransactionStateObserver {

    /**
     * Perimeter of projections that are under responsibility and status management by this repository.
     */
    private AbstractReadModelProjectionsSet managedProjections;

    /**
     * Graph manipulated by this repository's projections that can be a dedicated independent graph.
     * Alternatively, it can be a shared global graph model relative to all the domains managed by applications, where this repository's data view are projected (e.g with relations onto others subdomains graph sections).
     */
    private final AbstractDomainGraphImpl graphModel;

    /**
     * Repository technical logger.
     */
    private final Logger logger;

    /**
     * Default constructor.
     *
     * @param managedGraph Mandatory graph under responsibility of this repository.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public AbstractReadModelRepository(AbstractDomainGraphImpl managedGraph) throws IllegalArgumentException {
        if (managedGraph == null) throw new IllegalArgumentException("Managed graph parameter is required!");
        this.logger = Logger.getLogger(this.getClass().getName());
        // Define graph under responsibility of this repository
        this.graphModel = managedGraph;
    }

    /**
     * Get logger.
     *
     * @return A logger.
     */
    protected Logger logger() {
        return this.logger;
    }

    /**
     * Get the graph model currently managed by this repository.
     *
     * @return A graph.
     */
    protected AbstractDomainGraphImpl graphModel() {
        return this.graphModel;
    }

    /**
     * Identify the type of domain events potentially materializing a change of write-model (e.g event store) observed by this repository,
     * which are golden source of data-view projection that could need to be refreshed.
     *
     * @param domainEvent Potential write-model data change notification requiring related data-view status refresh.
     */
    @Override
    public void handleEvent(DomainEvent domainEvent) {
        if (domainEvent != null) {
            AbstractReadModelProjectionsSet readModelScope = getManagedProjections();
            AbstractRealModelDataViewProjection dataViewProjection;
            Class<?> sourceOfInterestType;
            if (readModelScope != null) {
                for (IReadModelProjection proj : readModelScope.projections()) {
                    // Identify the projections which are subscribers of this type of domain event (according to their declared event type interests)
                    if (AbstractRealModelDataViewProjection.class.isAssignableFrom(proj.getClass())) {
                        dataViewProjection = (AbstractRealModelDataViewProjection) proj;
                        // Is projection supporting the type of event?
                        sourceOfInterestType = dataViewProjection.subscribeToEventType();
                        if (sourceOfInterestType == null || sourceOfInterestType.isAssignableFrom(domainEvent.getClass())) {
                            // The projection expect to be notified about this type of event
                            // Forward the event to projections interested
                            dataViewProjection.handleEvent(domainEvent);
                        }
                    }
                }
            }
        }
    }

    /**
     * Confirm interested in any type of domain event that can be source of interest for projections.
     *
     * @return A DomainEvent.class type as observed.
     */
    @Override
    public Class<?> subscribeToEventType() {
        return DomainEvent.class; // Interested in any type of domain event
    }

    /**
     * Get the projections managed by this repository if defined.
     *
     * @return A set of read-model projections or null.
     */
    protected AbstractReadModelProjectionsSet getManagedProjections() {
        return this.managedProjections;
    }

    /**
     * Define the collection of projections that are managed by this repository as read-model perimeter.
     *
     * @param readModelPerimeter A set of projections.
     */
    protected void setManagedProjections(AbstractReadModelProjectionsSet readModelPerimeter) {
        this.managedProjections = readModelPerimeter;
    }

    /**
     * Search managed projection that is supporting a specific query (e.g specific to the projection data structured).
     *
     * @param aQueryEvent Type of the query to search into the managed projections.
     * @return A projection which is able to execute the query type. Else return null when none found, or when query parameter is not defined.
     */
    protected final IReadModelProjection findBySupportedQuery(IEventType aQueryEvent) {
        if (aQueryEvent != null) {
            // Search a projection that is supporting the query name (from its descriptor)
            AbstractReadModelProjectionsSet projections = getManagedProjections();
            if (projections != null) {
                return projections.findBySupportedQuery(aQueryEvent); // Find projection including a declaration/specification defining that the type of query is supported
            }
        }
        return null; // None found
    }

    /**
     * Default implement which execute a dispatch of the notification to all the registered subscribers to this repository.
     * This implementation use the promotionManager to promote automatically any notification received.
     *
     * @param notification Notification event. Ignored when null.
     */
    @Override
    public final void notifyTransactionState(DomainEvent notification) {
        if (notification != null)
            // Dispatch the notification about the repository read-model changes (e.g data view refreshed) to any existing observers of the perimeter
            subscribersManager().publish(notification);
    }

    /**
     * Get the name of the search criteria that can be evaluated to identify a query.
     * This information (e.g Command.TYPE) is generally added into each query parameters set that allow repository to identify query event types from domain's referential of queries supported.
     *
     * @return A query name based on query type (projection that support the query parameters and specific data path/structure).
     */
    protected abstract String queryNameBaseOn();

    /**
     * Build a set of attributes usable ad query command's parameter (e.g usable during a query execution).
     * @param searchCriteria Search criteria (e.g provided by a Command event received to execute a query on a repository) to read and to translate into attributes collection.
     * @return Build attributes collection including all defined search criteria (named, and valued). Empty collection when searchCriteria parameter is not defined or is empty.
     */
    protected Collection<Attribute> prepareQueryParameters(Map<String, String> searchCriteria) {
        // Prepare query command attributes set based on search criteria submitted
        Collection<Attribute> queryParameters = new HashSet<>();
        if (searchCriteria != null) {
            for (Map.Entry<String, String> param : searchCriteria.entrySet()) {
                // attribute name equals to search criteria label
                String paramName = param.getKey();
                // attribute value equals to search criteria value
                String paramValue = param.getValue();
                if (paramName != null && !paramName.isEmpty()) {
                    if (paramValue != null && !paramValue.isEmpty()) {
                        // Submit only search criteria with name AND value defined
                        queryParameters.add(new Attribute(paramName, paramValue));// Add valid query parameter submitted
                    }
                }
            }
        }
        return queryParameters;
    }
}
