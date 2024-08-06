package org.cybnity.framework.domain;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.ReadModelProjectionDescriptor;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an optimized read-model projection allowing query and read of denormalized version of domain layer object (e.g status and value of a domain object version at a moment of life).
 * This interface contract is covering a perimeter of read-model projection based on a type of denormalized domain object view.
 * Each projection specialized for a domain layer (e.g aggregate view) shall extend this type of contract to add specific methods supporting optimized queries for the type of domain object view managed.
 */
public interface IReadModelProjection {

    /**
     * Capture and interpretation of a subject change or read directive which can be source of data regarding a data view of this projection.
     * Represent a monitoring capability supporting the creation or change of an origin data interesting this read-model projection which shall maintain its up-to-date status.
     * This method allow to make query on the projection (current status of the read-model projection) according to parameters (e.g used for transversal search, or filtering of data view graph) provided by the directive.
     * Read Model Projection is persisted after each execution and can be accessed by many readers, both inside and outside its bounded context.
     * When the completable instance is finalized, the caller can collect via sync or async function to process the query result type(s).
     * <br>
     * This method capture only event types which are considered as source of information of the projection data view.
     * If event does not be source of information for this projection, nothing else is performed.
     * If event is relative to a source of data that make sens for the projection, it executes the projection creation/update into the projected read-model graph database.
     * The main steps to implement by any redefinition of this method are:
     * - Identify if the type of event captured to evaluate if is it valuable for a refresh of data view in relation with this projection
     * - Call the creation or update method(s) provided by the graphModel which include the specific implementation code that know how to manipulate the graph according to its technological language (e.g TinkerPop).
     *
     * @param directive Change or Query command (CQRS pattern's input element) relative to the projection that can be performed.
     */
    public void when(Command directive);

    /**
     * Perform query on this projection to read the current status of the data-view managed scope.
     *
     * @param request        Mandatory query command (CQRS pattern's input element) relative to the projection that shall be performed.
     * @param resultObserver Optional provider of data-view status collected as request results.
     * @throws IllegalArgumentException      When any mandatory parameter is missing.
     * @throws UnsupportedOperationException When request execution generated an issue (e.g query not supported by this projection; or error of request parameter types).
     */
    public void when(Command request, CompletableFuture<IQueryResponse> resultObserver) throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * Get description of this read-model projection.
     *
     * @return A description providing specification element regarding this projection (e.g label, ownership, categorization elements).
     */
    public ReadModelProjectionDescriptor description();

    /**
     * Make active the projection (e.g create or sync the graph schema aligned and supporting this projection; create/update the indexes managed).
     *
     * @throws UnoperationalStateException When problem during the projection activation.
     */
    public void activate() throws UnoperationalStateException;

    /**
     * Delete or deactivate the projection (e.g drop a graph instance aligned and supporting this projection).
     *
     * @throws UnoperationalStateException When problem during the projection activation.
     */
    public void deactivate() throws UnoperationalStateException;

    /**
     * Confirm if the read-model support a type of query command.
     *
     * @param queryCommand Query type to evaluate as supported by this read-model projection.
     * @return True when the query is declared like supported by the read-model. Else return false and when the query command parameter is not defined.
     */
    public boolean isSupportedQuery(IEventType queryCommand);
}
