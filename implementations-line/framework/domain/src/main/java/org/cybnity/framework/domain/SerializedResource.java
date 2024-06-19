package org.cybnity.framework.domain;

import org.cybnity.framework.domain.infrastructure.ResourceDescriptor;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represent a resource based on a serialized resource (e.g java object) that is including a description.
 * This type of resource can be managed by a storage system (e.g storage of object full state version as snapshot into a store).
 *
 * @author olivier
 */
public class SerializedResource extends ValueObject<Serializable> implements Serializable, IVersionable {

    private static final long serialVersionUID = new VersionConcreteStrategy().composeCanonicalVersionHash(SerializedResource.class).hashCode();

    private final Serializable value;

    private final ResourceDescriptor description;

    /**
     * Default constructor.
     *
     * @param resource    Mandatory value of the serialized resource.
     * @param description Mandatory description of this resource.
     * @throws IllegalArgumentException When missing mandatory parameter.
     */
    public SerializedResource(Serializable resource, ResourceDescriptor description) throws IllegalArgumentException {
        super();
        if (resource == null)
            throw new IllegalArgumentException("Resource parameter must be defined!");
        if (description == null)
            throw new IllegalArgumentException("Description parameter must be defined!");
        this.value = resource;
        this.description = description;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

    /**
     * Get the origin serializable value.
     *
     * @return A value.
     */
    public Serializable value() {
        return value;
    }

    /**
     * Get description of the serialized resource.
     *
     * @return A description.
     */
    public ResourceDescriptor description() {
        return description;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new SerializedResource(this.value, this.description);
    }

    /**
     * Return contributors.
     *
     * @return Contributors based on existing description attributes and serialized resource string version.
     */
    @Override
    public String[] valueHashCodeContributors() {
        // Prepare contributors based on existing description attributes
        ArrayList<String> contributors = new ArrayList<>();
        if (description.resourceId() != null && !description.resourceId().isEmpty())
            contributors.add(description.resourceId());
        if (description.versionHash() != null && !description.versionHash().isEmpty())
            contributors.add(description.versionHash());
        if (description.resourceTypeSerialVersionUID() != null && !description.resourceTypeSerialVersionUID().isEmpty())
            contributors.add(description.resourceTypeSerialVersionUID());
        contributors.add(value.toString());
        return contributors.toArray(new String[description.size()]);
    }
}
