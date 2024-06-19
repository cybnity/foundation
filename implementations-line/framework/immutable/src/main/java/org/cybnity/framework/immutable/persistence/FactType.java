package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.INaming;
import org.cybnity.framework.immutable.*;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represent a category of fact (e.g based on class type). Allow to identify a
 * type of fact (e.g DomainEvent, Command) which is persisted as a fact.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class FactType implements Unmodifiable, IVersionable, Serializable, IUniqueness, INaming {

    /**
     * Version of this class type.
     */
    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(FactType.class).hashCode();

    /**
     * Label identifying a unique name regarding a category of fact (e.g name of
     * class regarding a concrete event (e.g OrderConfirmed).
     * <p>
     * Define the uniqueness of this class type instance.
     */
    private final String name;

    /**
     * Auto-generated identifier of this fact type.
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    private String id;

    /**
     * Configuration about the minimum number of characters for identifier
     * generation process.
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    static private final int minLetterQty = 88;

    /**
     * Default constructor of a fact category.
     *
     * @param categoryName Mandatory label regarding the fact type logical naming.
     * @param identifier   Optional identifier of this fact type. When not defined,
     *                     a location-independent identifier is automatically
     *                     generated (based on categoryName natural key).
     * @throws IllegalArgumentException When mandatory parameter is missing (e.g
     *                                  null or empty).
     */
    public FactType(String categoryName, String identifier) throws IllegalArgumentException {
        if (categoryName == null || categoryName.isEmpty())
            throw new IllegalArgumentException("The categoryName parameter is required!");
        this.name = categoryName;
        this.id = identifier;
        if (this.id == null || this.id.isEmpty()) {
            try {
                // Generate automatic location-independent identifier regarding the fact type
                // label
                StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(this.name, minLetterQty);
                NaturalKeyIdentifierGenerator gen = new NaturalKeyIdentifierGenerator(builder);
                gen.build();
                id = builder.getResult();
            } catch (Exception e) {
                // Generation problem that should never arrive because build() method called
                // before result read

                // TODO: Add technical log in case of implementation evolution of the
                // builder.getResult() method usage requirements
            }
        }
    }

    /**
     * Default constructor of a fact category with a location-independent identifier
     * automatically generated (based on categoryName natural key).
     *
     * @param categoryName Mandatory label regarding the fact type logical naming.
     * @throws IllegalArgumentException When mandatory parameter is missing (e.g
     *                                  null or empty).
     */
    public FactType(String categoryName) throws IllegalArgumentException {
        this(categoryName, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactType factType = (FactType) o;
        return Objects.equals(name, factType.name) && Objects.equals(id, factType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public Set<Field> basedOn() {
        Set<Field> uniqueness = new HashSet<>();
        try {
            uniqueness.add(this.getClass().getDeclaredField("name"));
        } catch (NoSuchFieldException e) {
            // Problem of implementation that shall never be thrown
            // TODO: add log for developer error notification
        }
        return uniqueness;
    }

    /**
     * Get the name of this fact type.
     *
     * @return A label.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Get this fact type identifier.
     *
     * @return An identifier.
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    public String id() {
        return this.id;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        return new FactType(this.name, this.id);
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
        return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }

}
