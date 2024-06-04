package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IReadModelProjection;

/**
 * Stereotype of implementation class defining a read-model projection.
 */
public abstract class AbstractRealModelDataViewProjection implements IReadModelProjection {

    /**
     * Description elements of this projection.
     */
    private ReadModelProjectionDescriptor description;

    /**
     * Default constructor regarding a standard read model projection.
     *
     * @param label     Mandatory logical definition (e.g query name, projection finality unique name) of this projection that can be used for projections equals validation.
     * @param ownership Mandatory domain which is owner of the projection (as in its scope of responsibility).
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public AbstractRealModelDataViewProjection(String label, IDomainModel ownership) throws IllegalArgumentException {
        // Create description of this projection based on parameters
        prepareDescription(label, ownership);
    }

    /**
     * Prepare expected description of this read-model projection.
     * Called during the instance creation.
     *
     * @param label     Mandatory logical label regarding this projection.
     * @param ownership Mandatory domain which is owner of this projection.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    protected void prepareDescription(String label, IDomainModel ownership) throws IllegalArgumentException {
        // Create description instance of this projection
        this.description = ReadModelProjectionDescriptor.instanceOf(label, ownership);
    }

    @Override
    public ReadModelProjectionDescriptor description() {
        return this.description;
    }
}
