package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IReadModelProjection;
import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractReadModelRepository;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.event.SampleDomainQueryEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.SampleDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.SampleDomainReadModelImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.SampleDomain;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public List<SampleDataView> queryWhere(Map<String, String> searchCriteria, ISessionContext ctx) {
        List<SampleDataView> results = null;
        if (searchCriteria != null) {
            // Identify the projection name to query (projection that support the query parameters and specific data path/structure) from the query type name requested
            String queryName = searchCriteria.get(Command.TYPE);
            // Identify query event type from domain's referential of queries supported
            IEventType queryType = Enum.valueOf(SampleDomainQueryEventType.class, queryName);

            // Search the projection that is declared like supporting the query type (from its descriptor)
            IReadModelProjection managedProjection = this.findBySupportedQuery(queryType);
            if (managedProjection!=null) {
                // Execute the query via delegation to the found projection (owner of data structure and supported parameter types)

                // TODO préparer la query command é exécuter et instancier listener de réponse

                //Command queryToExecute = ConcreteQueryEvent
                //managedProjection.when();
            } // else unknown query name or not supported by the read-model under responsibility of this repository, which make impossible to perform the query with potential result finding

        }
        return results; // Null results returned by default
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
