package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IReadModelProjection;

import java.util.HashMap;

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

    private void prepareDescription(String label, IDomainModel ownership) throws IllegalArgumentException {
        // Create description instance of this projection
        HashMap<String, Object> propertyCurrentValue = new HashMap<>();
        propertyCurrentValue.put(ReadModelProjectionDescriptor.PropertyAttributeKey.LABEL.name(), label);
        propertyCurrentValue.put(ReadModelProjectionDescriptor.PropertyAttributeKey.OWNERSHIP.name(), ownership);
        this.description = new ReadModelProjectionDescriptor(propertyCurrentValue);
    }

    @Override
    public ReadModelProjectionDescriptor description() {
        return this.description;
    }
}
