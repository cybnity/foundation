package org.cybnity.framework.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cybnity.framework.domain.event.RandomUUIDFactory;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.Collection;

/**
 * Identifying information type that is based on a single text chain.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
@JsonDeserialize(using = IdentifierStringBasedDeserializer.class)
public class IdentifierStringBased extends ValueObject<String> implements Identifier {

    @JsonIgnore
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(IdentifierStringBased.class).hashCode();
    private final String value;
    private final String name;

    /**
     * Default constructor.
     *
     * @param name  Mandatory name of the identifier (e.g uuid).
     * @param value Mandatory value of the identifier.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    @JsonCreator
    public IdentifierStringBased(@JsonProperty("name") String name, @JsonProperty("value") String value) throws IllegalArgumentException {
        super();
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name parameter is required!");
        this.name = name;
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException("The value parameter is required!");
        this.value = value;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new IdentifierStringBased(name, value);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Serializable value() {
        return this.value;
    }

    @Override
    public String[] valueHashCodeContributors() {
        return new String[]{this.value, this.name};
    }

    /**
     * Generate an identifier based on a list (or unique contained instance) of
     * identifiers. This method is reusable for any class requiring calculation of
     * combined identifier.
     *
     * @param basedOn Mandatory set of identifiers (e.g unique instance or multiple
     *                to concatenate). Shall contain a minimum one instance of
     *                identifier usable for generation of resulting identifier to
     *                return.
     * @return An instance of identifier. When all the source identifiers have the
     * same identifying name, the returned instance use the same name. When
     * several names are found from the source identifiers, the name of the
     * returned instance is equals to BaseConstants.IDENTIFIER_ID.name() .
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static Identifier build(Collection<Identifier> basedOn) throws IllegalArgumentException {
        if (basedOn == null || basedOn.isEmpty())
            throw new IllegalArgumentException(
                    "basedOn parameter is required and shall contain a minimum one identifier!");
        StringBuilder combinedId = new StringBuilder();
        String uniqueIdName = null;
        boolean uniqueNameFound = true;
        for (Identifier id : basedOn) {
            combinedId.append(id.value());
            if (uniqueIdName != null) {
                // Check if same global name used for all the identifiers
                if (!uniqueIdName.equals(id.name()))
                    uniqueNameFound = false;
            } else {
                // Initialize default identifier name based on the found identifying information
                // label
                uniqueIdName = id.name();
            }
        }
        // Return combined identifier
        return new IdentifierStringBased((uniqueNameFound) ? uniqueIdName : BaseConstants.IDENTIFIER_ID.name(),
                combinedId.toString());
    }

    /**
     * Generator of standard identifier based on technical value automatically generated.
     *
     * @param salt Optional value to include into the identifier value auto-generated.
     * @return Generated identifier instance.
     */
    public static Identifier generate(String salt) {
        return new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), RandomUUIDFactory.generate(salt));
    }

}
