package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.event.QueryFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractReadModelRepository;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainQueryEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.SampleDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.SampleDomainReadModelImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.SampleDomain;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Example of domain repository managing transactions relative to an object type (e.g supported by one or several read-model projections representing it and its relations scope) or to a domain or to a subdomain.
 */
public class SampleDomainTransactionsRepository extends AbstractReadModelRepository implements ISampleDomainRepository {

    private static SampleDomainTransactionsRepository SINGLETON;

    /**
     * Configuration of the domain which is responsible for the data-views perimeter managed over this repository.
     */
    private static final IDomainModel READ_MODEL_OWNERSHIP = new SampleDomain();

    /**
     * Reserved constructor that initialize the graph instance under responsibility of this repository, with preparation of its read-model scope (set of projections supported).
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When impossible connection and initialization of the graph model manipulated by this repository.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    private SampleDomainTransactionsRepository(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        super(new SampleDomainGraphImpl(ctx));
        // Define set of projections identifying the read-model scope that can manipulate the graph
        this.setManagedProjections(new SampleDomainReadModelImpl(ctx, new SampleDomainGraphImpl(ctx), READ_MODEL_OWNERSHIP, this));
    }

    /**
     * Get a repository instance.
     *
     * @return A singleton instance.
     * @throws UnoperationalStateException When impossible connection and initialization of the graph model manipulated by this repository.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public static SampleDomainTransactionsRepository instance(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (SINGLETON == null) {
            // Initializes singleton instance
            SINGLETON = new SampleDomainTransactionsRepository(ctx);
        }
        return SINGLETON;
    }

    @Override
    public void freeResources() {
    }

    @Override
    public SampleDataView nextIdentity(ISessionContext iSessionContext) {
        return null;
    }

    @Override
    public SampleDataView factOfId(Identifier identifier, ISessionContext iSessionContext) {
        return null;
    }

    @Override
    public boolean remove(SampleDataView sampleDataView, ISessionContext iSessionContext) {
        return false;
    }

    @Override
    public void removeAll(Collection<SampleDataView> collection, ISessionContext iSessionContext) {

    }

    @Override
    public SampleDataView save(SampleDataView sampleDataView, ISessionContext iSessionContext) {
        return null;
    }

    @Override
    public void saveAll(Collection<SampleDataView> collection, ISessionContext iSessionContext) {

    }

    @Override
    protected String queryNameBaseOn() {
        return Command.TYPE;
    }

    @Override
    public List<SampleDataView> queryWhere(Map<String, String> searchCriteria, ISessionContext ctx) throws IllegalArgumentException, UnsupportedOperationException, UnoperationalStateException {
        if (searchCriteria != null) {
            // Identify the query name based on query type (projection that support the query parameters and specific data path/structure)
            String queryName = searchCriteria.get(queryNameBaseOn());
            if (queryName != null && !queryName.isEmpty()) {
                // Identify query event type from domain's referential of queries supported
                IEventType queryType = Enum.valueOf(/* Referential catalog of query types supported by the repository domain */ SampleDomainQueryEventType.class, queryName);

                // Search a projection that is declared supporting the requested query type
                IReadModelProjection managedProjection = this.findBySupportedQuery(queryType);
                if (managedProjection != null) {
                    // Prepare instance of query command event to submit on found projection
                    final Command queryToPerform = QueryFactory.create(/* Name of query type */ queryName, /* query command UUID */
                            new DomainEntity(IdentifierStringBased.generate(null)),
                            /* Prepare query command attributes set based on search criteria submitted */ this.prepareQueryParameters(searchCriteria),
                            /* None prior command managed during this explicit query call */ null);

                    // Execute the query via delegation to the found projection (owner of data structure and supported parameter types)
                    // See https://www.callicoder.com/java-8-completablefuture-tutorial/ for help and possible implementation approaches for sync/async execution of query
                    CompletableFuture<Optional<DataTransferObject>> executionResulting = CompletableFuture.supplyAsync(() -> {
                        // Execute the query onto the projection and deliver the optional results
                        try {
                            return managedProjection.when(queryToPerform);
                        } catch (UnoperationalStateException e) {
                            throw new RuntimeException(e);
                        }
                    }).thenApply(IQueryResponse::value);

                    try {
                        // Read results when available via CompletableFuture.get() blocking method invoked on IQueryResponse,
                        // that waits until the Future is completed (return result after its completion)
                        Optional<DataTransferObject> dto = executionResulting.get();
                        if (dto.isPresent()) {
                            // Build domain data view results to return
                            List<SampleDataView> results;
                            DataTransferObject resultProvider = dto.get();
                            if (SampleDataView.class.isAssignableFrom(resultProvider.getClass())) {
                                // Valid type of collected data view object managed by this repository
                                // that can be returned as unique result
                                results = new LinkedList<>();
                                results.add((SampleDataView) resultProvider);
                                return results;
                            } else if (List.class.isAssignableFrom(resultProvider.getClass())) {
                                // Potentially collected list of sample data view as results container
                                return (List<SampleDataView>) resultProvider;
                            }
                        }
                        return null; // Confirm that none results are provided from the executed query
                    } catch (Exception e) {
                        throw new UnoperationalStateException(e);
                    }
                } else {
                    // else unknown query name or not supported by the read-model under responsibility of this repository,
                    // which make impossible to perform the query with potential result finding
                    throw new UnsupportedOperationException("The requested query is not supported by any projection of this repository. Only SampleDomainQueryEventType values are supported as query name!");
                }
            } else {
                // else unknown query name to search on repository
                throw new IllegalArgumentException("A defined query name is required from search criteria (Command.TYPE criteria with defined value)!");
            }
        }
        throw new IllegalArgumentException("Search criteria parameter is required!");
    }

    @Override
    public SampleDataView nextIdentity() {
        return null;
    }

    @Override
    public SampleDataView factOfId(Identifier identifier) {
        return null;
    }

    @Override
    public boolean remove(SampleDataView sampleDataView) {
        return false;
    }

    @Override
    public void removeAll(Collection<SampleDataView> collection) {

    }

    @Override
    public SampleDataView save(SampleDataView sampleDataView) {
        return null;
    }

    @Override
    public void saveAll(Collection<SampleDataView> collection) {

    }

}
