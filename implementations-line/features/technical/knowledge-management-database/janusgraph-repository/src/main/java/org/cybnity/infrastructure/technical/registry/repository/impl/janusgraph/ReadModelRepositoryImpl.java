package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.IReadModel;
import org.cybnity.framework.domain.IReadModelProjection;
import org.cybnity.framework.domain.infrastructure.IDomainRepository;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.infrastructure.technical.registry.adapter.api.KNRAdapter;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.KNRAdapterJanusGraphImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * Domain data repository implementation class using JanusGraph database resources (e.g projection builders defining graph read-model of domain's objects).
 * A repository that is providing graph builders and manage their lifecycle according to an applicative domain scope.
 * It's the data layer side dedicated to the denormalized queryable data over logical graph instance embedding the graph design and manipulation capabilities.
 * This read-model management component is responsible to select and maintain up-to-date the data views projections defined dynamically or statically by a domain.
 */
public abstract class ReadModelRepositoryImpl implements IDomainRepository<Aggregate>, IReadModel {

    /**
     * Repository logger.
     */
    private final Logger logger;

    /**
     * Adapter to registry system.
     */
    private final KNRAdapter adapter;

    /**
     * Perimeter of the IRead Model of this repository.
     * Identify the managed read-model projections (e.g GraphReadModelProjection instances) relative to the objects domain, that are materialized by a unified and consistent graph.
     * Order is maintained and none duplicate projection is authorized with same label and domain owner.
     */
    private LinkedHashSet<IReadModelProjection> domainProjections;

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public ReadModelRepositoryImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        logger = LoggerFactory.getLogger(this.getClass());
        this.adapter = new KNRAdapterJanusGraphImpl(ctx);
    }

    /**
     * Get the logger instance regarding this repository.
     *
     * @return A logger instance.
     */
    protected Logger logger() {
        return this.logger;
    }

    /**
     * Get the managed read-model projections relative to the objects domain, that are materialized by an unified and consistent graph.
     *
     * @return A set of domain object views projections, or empty collection.
     */
    protected LinkedHashSet<IReadModelProjection> domainProjections() {
        if (this.domainProjections == null) {
            this.domainProjections = new LinkedHashSet<>();
        }
        return this.domainProjections;
    }

    /**
     * Add a projection to the current managed read-model projections.
     *
     * @param dataViewModelProjection Mandatory projection to add.
     * @return True when added to the managed set. False when was already known as managed by this repository.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    protected boolean addProjection(IReadModelProjection dataViewModelProjection) throws IllegalArgumentException {
        if (dataViewModelProjection == null)
            throw new IllegalArgumentException("Data view model projection parameter is required!");
        boolean added = false;
        // Get managed projections
        LinkedHashSet<IReadModelProjection> managedDataViews = domainProjections();
        // Check that existing equals projection is not already managed
        if (!managedDataViews.contains(dataViewModelProjection)) {
            // Add it to the managed projections set
            managedDataViews.add(dataViewModelProjection);

            // Create graph schema specification into graph model

            // TODO créer specification et indexes du graph correspondant au read-model ajouté/managé

            added = true;
        }
        return added;
    }

    @Override
    public void freeResources() {
        // Freedom of resources allocated by the adapter
        this.adapter.freeUpResources();
    }

}
