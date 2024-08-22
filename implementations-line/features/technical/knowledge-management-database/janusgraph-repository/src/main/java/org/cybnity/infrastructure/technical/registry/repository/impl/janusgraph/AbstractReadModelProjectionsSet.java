package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.IReadModel;
import org.cybnity.framework.domain.IReadModelProjection;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.ReadModelProjectionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Implementation class of using JanusGraph database projections provider (e.g projection builder defining graph read-model of domain's objects).
 * Set of projection is generally defined per domain or subdomain scope regarding all supported projections.
 * It's the data layer's set of graph manipulation capabilities.
 * This read-model management component is responsible to select and maintain up-to-date the data views projections compatible with a domain graph perimeter.
 */
public abstract class AbstractReadModelProjectionsSet implements IReadModel {

    /**
     * Repository logger.
     */
    private final Logger logger;

    /**
     * Perimeter of the IRead Model of this repository.
     * Identify the managed read-model projections (e.g GraphReadModelProjection instances) relative to the objects domain, that are materialized by a unified and consistent graph.
     * Order is maintained and none duplicate projection is authorized with same label and domain owner.
     */
    private LinkedHashSet<IReadModelProjection> domainProjections;

    /**
     * Current context.
     */
    private final IContext context;

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public AbstractReadModelProjectionsSet(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        this.context = ctx;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Get current context.
     * @return A context.
     */
    protected IContext context() {
        return this.context;
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
     * Get the managed read-model projections relative to the objects domain, that are materialized by a unified and consistent graph.
     *
     * @return A set of domain object views projections, or empty collection.
     */
    @Override
    public Collection<IReadModelProjection> projections() {
        if (this.domainProjections == null) {
            this.domainProjections = new LinkedHashSet<>();
        }
        return domainProjections;
    }

    /**
     * Add a projection to the current managed read-model projections.
     *
     * @param dataViewModelProjection Mandatory projection to add.
     * @return True when added to the managed set. False when was already known as managed by this repository.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When problem during the projection activation. New projection have not been added into the projections list currently managed by this repository.
     */
    protected final boolean addProjection(IReadModelProjection dataViewModelProjection) throws IllegalArgumentException, UnoperationalStateException {
        if (dataViewModelProjection == null)
            throw new IllegalArgumentException("Data view model projection parameter is required!");
        boolean added = false;
        // Get managed projections
        Collection<IReadModelProjection> managedDataViews = projections();
        // Check that existing equals projection is not already managed
        if (!managedDataViews.contains(dataViewModelProjection)) {
            // Update graph schema specification into graph model (e.g sync of structure changes, indexes generation...)
            dataViewModelProjection.activate();
            // Add it to the managed projections set
            managedDataViews.add(dataViewModelProjection);
            added = true; // Confirm good activated projection and entry in management by this repository
        }
        return added;
    }

    @Override
    public final IReadModelProjection findBy(ReadModelProjectionDescriptor projectionIdentity) throws IllegalArgumentException {
        if (projectionIdentity == null)
            throw new IllegalArgumentException("Projection identity parameter is required!");
        // Search identity from current read-model's managed projections
        ReadModelProjectionDescriptor desc;
        for (IReadModelProjection managed : projections()) {
            desc = managed.description();
            if (desc.equals(projectionIdentity)) {
                return managed;
            }
        }
        return null;
    }

    @Override
    public final IReadModelProjection findBySupportedQuery(IEventType aQueryType) throws IllegalArgumentException {
        if (aQueryType == null)
            throw new IllegalArgumentException("Query type parameter is required!");
        // Search in each managed projection, which one is supporting a query with equals naming
        ReadModelProjectionDescriptor desc;
        for (IReadModelProjection managed : projections()) {
            if (managed.isSupportedQuery(aQueryType)) return managed;
        }
        return null;
    }
}
