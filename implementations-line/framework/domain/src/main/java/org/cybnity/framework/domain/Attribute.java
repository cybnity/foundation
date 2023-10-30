package org.cybnity.framework.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cybnity.framework.immutable.IVersionable;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;

/**
 * Represent a characteristic which can be added to a topic (e.g an technical
 * named attribute which is defined on-fly on an existing object, including a
 * value). It's more or less like a generic property assignable to any topic or
 * object (e.g property on a workflow step instance).
 *
 * @author olivier
 */
public class Attribute extends ValueObject<String> implements Serializable, IVersionable {

    private static final long serialVersionUID = new VersionConcreteStrategy().composeCanonicalVersionHash(Attribute.class).hashCode();

    private final String value;

    private final String name;

    /**
     * Default constructor.
     *
     * @param attributeName  Mandatory name of this attribute (allowing topic search
     *                       by attribute name).
     * @param attributeValue Mandatory value of this named attribute.
     * @throws IllegalArgumentException When missing mandatory parameter.
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Attribute(@JsonProperty("name") String attributeName, @JsonProperty("value") String attributeValue) throws IllegalArgumentException {
        super();
        if (attributeName == null || attributeName.isEmpty())
            throw new IllegalArgumentException("Attribute name parameter must be defined!");
        if (attributeValue == null || attributeValue.isEmpty())
            throw new IllegalArgumentException("Attribute value parameter must be defined!");
        this.name = attributeName;
        this.value = attributeValue;
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
     * Get the value of this named attribute.
     *
     * @return A value.
     */
    public String value() {
        return value;
    }

    /**
     * Get the name of this attribute.
     *
     * @return A name.
     */
    public String name() {
        return name;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new Attribute(this.name, this.value);
    }

    @Override
    public String[] valueHashCodeContributors() {
        return new String[]{name, value};
    }
}
