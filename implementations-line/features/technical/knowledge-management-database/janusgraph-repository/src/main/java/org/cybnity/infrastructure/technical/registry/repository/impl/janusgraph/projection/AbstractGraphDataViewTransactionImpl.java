package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.projection;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.model.AbstractRealModelDataViewProjection;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.AbstractDomainGraphImpl;

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
     * Default constructor regarding a graph read model projection.
     *
     * @param label     Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @param dataModel Mandatory database model that can be manipulated by this transaction about its data view(s).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public AbstractGraphDataViewTransactionImpl(String label, IDomainModel ownership, AbstractDomainGraphImpl dataModel) throws IllegalArgumentException {
        super(label, ownership);
        if (dataModel == null) throw new IllegalArgumentException("dataModel parameter is required!");
        this.graphModel = dataModel;
    }

    /**
     * Get the graph model that support the scope of data view manipulated by this transaction.
     *
     * @return A model.
     */
    protected AbstractDomainGraphImpl graphModel() {
        return this.graphModel;
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
