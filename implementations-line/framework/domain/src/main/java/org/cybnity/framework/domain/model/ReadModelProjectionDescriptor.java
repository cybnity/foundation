package org.cybnity.framework.domain.model;

import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.immutable.persistence.QualitativeDataBuilder;
import org.cybnity.framework.immutable.persistence.QualitativeDataGenerator;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * Definition regarding a read-model projection.
 *
 * @author olivier
 */
public class ReadModelProjectionDescriptor implements Unmodifiable, Serializable {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ReadModelProjectionDescriptor.class).hashCode();

    protected HashMap<String, Object> value;

    /**
     * Keys set regarding the multiple attribute defining this complex
     * descriptor, and that each change need to be versioned/treated as a single
     * atomic fact.
     */
    public enum PropertyAttributeKey {
        LABEL, OWNERSHIP
    }

    /**
     * Default constructor.
     *
     * @param propertyCurrentValue Mandatory set of properties including minimum expected.
     * @throws IllegalArgumentException When any mandatory parameter is missing, or when minimum expected properties and values are missing.
     */
    public ReadModelProjectionDescriptor(HashMap<String, Object> propertyCurrentValue) throws IllegalArgumentException {
        if (propertyCurrentValue == null)
            throw new IllegalArgumentException("Property current value parameter is required!");
        if (propertyCurrentValue.isEmpty())
            throw new IllegalArgumentException("Property current value parameter shall include minimum required values (LABEL, OWNERSHIP)!");
        // Check presence of minimum values
        checkMinimumRequiredProperties(propertyCurrentValue);
        this.value = propertyCurrentValue;
    }

    /**
     * Factory of descriptor instance.
     *
     * @param label     Mandatory label defining the projection to create (e.g read-model projection unique name in the domain).
     * @param ownership Mandatory domain that is owner of the read-model projection to create.
     * @return A created instance including properties.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     */
    public static ReadModelProjectionDescriptor instanceOf(String label, IDomainModel ownership) throws IllegalArgumentException {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(PropertyAttributeKey.LABEL.name(), label);
        properties.put(PropertyAttributeKey.OWNERSHIP.name(), ownership);
        return new ReadModelProjectionDescriptor(properties);
    }

    /**
     * Verify the quality about the minimum properties expected to define this descriptor.
     *
     * @param propertyCurrentValue Mandatory properties.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    private void checkMinimumRequiredProperties(HashMap<String, Object> propertyCurrentValue) throws IllegalArgumentException {
        QualitativeReadModelDescriptionBuilder dataQualChecker = new QualitativeReadModelDescriptionBuilder(propertyCurrentValue);
        // Execute quality check
        new QualitativeDataGenerator(dataQualChecker).build();
        try {
            Boolean validQuality = (Boolean) dataQualChecker.getResult();
            if (!validQuality) throw new IllegalArgumentException("Invalid minimum required properties!");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get the logical name of the read-model projection.
     *
     * @return A label or null.
     */
    public String label() {
        return (String) this.currentValue().getOrDefault(PropertyAttributeKey.LABEL.name(), null);
    }

    /**
     * Get the domain that is owner of read-model projection.
     *
     * @return A functional responsible of the projection.
     */
    public IDomainModel ownership() {
        return (IDomainModel) this.currentValue().getOrDefault(PropertyAttributeKey.OWNERSHIP.name(), null);
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        // Return immutable version of current properties
        return new ReadModelProjectionDescriptor((HashMap<String, Object>) Collections.unmodifiableMap(this.currentValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadModelProjectionDescriptor that = (ReadModelProjectionDescriptor) o;
        // Check logical label and ownership
        return that.label().equals(this.label()) && that.ownership().domainName().equals(this.ownership().domainName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Get the current value of this complex property.
     *
     * @return A set of valued attributes.
     */
    public HashMap<String, Object> currentValue() {
        return this.value;
    }

    /**
     * Builder of read model description quality validator.
     */
    private static class QualitativeReadModelDescriptionBuilder extends QualitativeDataBuilder {

        private final HashMap<String, Object> properties;

        private Boolean completeness, consistency, conformity, accuracy, integrity, timeliness;

        /**
         * Default constructor.
         *
         * @param propertyValues Mandatory definition of read-model to validate regarding its description attributes quality.
         * @throws IllegalArgumentException When mandatory parameter is missing.
         */
        public QualitativeReadModelDescriptionBuilder(HashMap<String, Object> propertyValues) throws IllegalArgumentException {
            super();
            if (propertyValues == null) throw new IllegalArgumentException("Property values parameter i required!");
            this.properties = propertyValues;
        }

        @Override
        public void makeCompleteness() {
            // Is all the requisite information available?

            // Check that a label property value is existing
            Object label = this.properties.getOrDefault(PropertyAttributeKey.LABEL.name(), null);

            // Check that an ownership property value is existing
            Object ownership = this.properties.getOrDefault(PropertyAttributeKey.OWNERSHIP.name(), null);

            // Verify that minimum required properties are defined with existing values
            completeness = (label != null && ownership != null);
        }

        @Override
        public void makeConsistency() {
            // Is data across all systems reflects the same information and are in sync with each other across the enterprise?
            consistency = true;
        }

        @Override
        public void makeConformity() {
            // Is following the set of standard data definitions like data type, size and format?

            // Verify that label is not empty string
            String labelValue = null;
            try {
                Object label = this.properties.getOrDefault(PropertyAttributeKey.LABEL.name(), null);
                labelValue = (String) label;
                if (labelValue.isEmpty()) labelValue = null; // Not acceptable empty value
            } catch (Exception e) {
                // Invalid type
            }

            // Check that an ownership property value
            IDomainModel ownerType = null;
            // Verify that ownership is a domain model
            try {
                Object ownership = this.properties.getOrDefault(PropertyAttributeKey.OWNERSHIP.name(), null);
                ownerType = (IDomainModel) ownership;
                if (ownerType.domainName() == null || ownerType.domainName().isEmpty())
                    ownerType = null; // Not acceptable non named domain
            } catch (Exception e) {
                // Invalid type
            }

            conformity = (labelValue != null && ownerType != null);
        }

        @Override
        public void makeAccuracy() {
            // is the degree to which data correctly reflects the real world object OR an event being described?
            accuracy = true;
        }

        @Override
        public void makeIntegrity() {
            // Is all data in a database can be traced and connected to other data?
            integrity = true;
        }

        @Override
        public void makeTimeliness() {
            timeliness = true;
        }

        /**
         * Result of quality rules applied.
         *
         * @return False or True value.
         * @throws Exception When quality process was nto executed.
         */
        @Override
        public Object getResult() throws Exception {
            if (completeness == null)
                throw new Exception("Quality rules process execution shall have been performed before to get the result!");
            return completeness & consistency & conformity & accuracy & integrity & timeliness;
        }
    }
}
