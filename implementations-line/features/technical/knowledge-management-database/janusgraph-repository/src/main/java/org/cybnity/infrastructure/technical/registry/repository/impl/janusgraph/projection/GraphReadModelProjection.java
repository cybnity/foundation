package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.model.AbstractRealModelDataViewProjection;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;

/**
 * Graph data view projection usable as read-model.
 */
public class GraphReadModelProjection extends AbstractRealModelDataViewProjection {

    /**
     * Synchronized graph by this projection regarding the scope of denormalized data.
     */
    private AbstractDomainGraphImpl graphModel;

    /**
     * Default constructor regarding a graph read model projection.
     *
     * @param label     Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public GraphReadModelProjection(String label, IDomainModel ownership) throws IllegalArgumentException {
        super(label, ownership);
    }

    @Override
    public void when(DomainEvent domainEvent) {
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
            graphModel.dropGraph();
    }

}
