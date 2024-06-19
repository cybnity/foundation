package org.cybnity.framework.immutable.persistence;

import org.cybnity.framework.immutable.NaturalKeyIdentifierGenerator;
import org.cybnity.framework.immutable.StringBasedNaturalKeyBuilder;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;

/**
 * Represent a structural version of object type (e.g fact class, domain event
 * class).
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_ROB_3")
public class TypeVersion implements Serializable {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(TypeVersion.class).hashCode();

    /**
     * Hash value of an object type as a type version value.
     */
    private final String hash;

    /**
     * Auto-generated identifier of this version.
     */
    private String id;

    /**
     * Configuration about the minimum number of characters for identifier
     * generation process.
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_8")
    static private final int minLetterQty = 88;

    /**
     * Category of origin subject.
     */
    private final FactType factType;

    /**
     * Constructor of a version relative to an existing fact type and identified version.
     * This constructor can be used during a deserialization process of a previous existing type version instance that already auto-generated fact type, hash value and identifier via usage of the default constructor.
     *
     * @param subject          Mandatory type regarding the subject of versioning.
     * @param subjectHashValue Mandatory Hash value of an object type as a type version value.
     * @param identifier       Optional identifier of this version.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TypeVersion(FactType subject, String subjectHashValue, String identifier) throws IllegalArgumentException {
        if (subject == null)
            throw new IllegalArgumentException("subject parameter is required!");
        this.factType = subject;
        if (subjectHashValue == null || subjectHashValue.isEmpty())
            throw new IllegalArgumentException("subjectHashValue parameter is required!");
        this.hash = subjectHashValue;
        this.id = identifier;
    }

    /**
     * Default constructor of a version that shall detect and generate a relative fact type based on a subject class.
     *
     * @param subject    Mandatory type regarding the subject of versioning.
     * @param identifier Optional identifier of this version. When not defined, a
     *                   location-independent identifier is automatically generated
     *                   (based on subject natural key).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TypeVersion(Class<?> subject, String identifier) throws IllegalArgumentException {
        if (subject == null)
            throw new IllegalArgumentException("subject parameter is required!");
        this.factType = new FactType(subject.getSimpleName());
        // Generate hash value regarding the subject
        this.hash = new VersionConcreteStrategy().composeCanonicalVersionHash(subject);
        // Get or generate optional identifier of this version
        this.id = identifier;
        if (this.id == null || this.id.isEmpty()) {
            try {
                // Generate automatic location-independent identifier regarding the subject type
                // label
                StringBasedNaturalKeyBuilder builder = new StringBasedNaturalKeyBuilder(subject.getName(),
                        minLetterQty);
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
     * @param subject Mandatory type regarding the subject of versioning.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TypeVersion(Class<?> subject) throws IllegalArgumentException {
        this(subject, null);
    }

    /**
     * Get the type of origin fact.
     *
     * @return A categorized type of fact (e.g based on original event class name).
     */
    public FactType factType() {
        return factType;
    }

    /**
     * Get the version hash value of the subject.
     *
     * @return A hashed value (base64 encoded).
     */
    public String hash() {
        return this.hash;
    }

    /**
     * Get this version identifier.
     *
     * @return An identifier.
     */
    public String id() {
        return this.id;
    }

}
