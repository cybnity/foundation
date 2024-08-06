package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.DataTransferObject;
import org.cybnity.framework.domain.SerializationFormat;
import org.cybnity.framework.domain.event.IAttribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Example of data view representing a transaction managed by a read model of a domain.
 */
public class SampleDataView extends DataTransferObject {

    /**
     * Immutable set of DTO properties including a data view version's values.
     * The usage of Set collection type allow to specifically exclude duplicates.
     */
    public final Set<Attribute> attributes;

    /**
     * Attribute type managed by this DTO.
     */
    public enum PropertyAttributeKey implements IAttribute {
        /**
         * Sample domain object (vertex equals to data view type) identifier.
         */
        IDENTIFIED_BY,
        /**
         * Name of the sample domain object.
         */
        NAME,
        /**
         * Domain object type (equals to Vertex type)
         */
        DATAVIEW_TYPE,
        /**
         * Date of creation regarding the sample domain object.
         */
        CREATED,
        /**
         * Commit version of this sample domain object (based on the last change identifier).
         */
        COMMIT_VERSION,
        /**
         * Data of last refresh regarding the sample domain object.
         */
        LAST_UPDATED_AT;
    }

    public SampleDataView(String identifier, String label, Date createdAt, String commitVersion, Date updatedAt) throws IllegalArgumentException {
        if (identifier == null || identifier.isEmpty())
            throw new IllegalArgumentException("Identifier parameter is required!");
        if (label == null || label.isEmpty()) throw new IllegalArgumentException("Label parameter is required!");
        if (createdAt == null) throw new IllegalArgumentException("CreatedAt parameter is required!");

        // Prepare immutable attributes set
        Set<Attribute> s = new HashSet<>();
        s.add(new Attribute(PropertyAttributeKey.IDENTIFIED_BY.name(), identifier));
        s.add(new Attribute(PropertyAttributeKey.NAME.name(), label));

        DateFormat formatter = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
        s.add(new Attribute(PropertyAttributeKey.CREATED.name(), formatter.format(Objects.requireNonNullElseGet(createdAt, () -> Date.from(OffsetDateTime.now().toInstant())))));
        if (updatedAt != null) {
            s.add(new Attribute(PropertyAttributeKey.LAST_UPDATED_AT.name(), formatter.format(Objects.requireNonNullElseGet(updatedAt, () -> Date.from(OffsetDateTime.now().toInstant())))));
        }

        if (commitVersion != null && !commitVersion.isEmpty()) {
            s.add(new Attribute(PropertyAttributeKey.COMMIT_VERSION.name(), commitVersion));
        }
        attributes = Collections.unmodifiableSet(s);
    }

    /**
     * Get the value of the attribute.
     *
     * @param prop Mandatory key of property to read.
     * @return Value of this DTO attribute.
     */
    public String valueOfProperty(PropertyAttributeKey prop) {
        List<Attribute> values = attributes.stream().filter(attr -> attr.name().equals(prop.name())).collect(Collectors.toList());
        return values.get(0).value();
    }

    public boolean equals(Object obj) {
        boolean equalsObject = false;
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof SampleDataView) {
                SampleDataView item = (SampleDataView) obj;
                if (item.valueOfProperty(PropertyAttributeKey.IDENTIFIED_BY).equals(this.valueOfProperty(PropertyAttributeKey.IDENTIFIED_BY))) {
                    equalsObject = true;
                }
            }
            return equalsObject;
        }
    }
}
