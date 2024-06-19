package org.cybnity.framework.domain.infrastructure;

import org.cybnity.framework.domain.SerializationFormat;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Common description of a resource managed into a Redis context.
 * Customized HashMap supporting pre-determined key names based on PropertyAttributeKey set.
 */
public class ResourceDescriptor extends HashMap<String, String> {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(ResourceDescriptor.class).hashCode();

    /**
     * Keys set regarding the multiple attribute defining this complex
     * descriptor, and that each change need to be versioned/treated as a single
     * atomic fact.
     */
    public enum PropertyAttributeKey {
        /**
         * Unique logical identifier of the resource.
         */
        RESOURCE_ID,

        /**
         * Label regarding the space path where the resource is accessible.
         */
        ACCESSIBILITY_NAMESPACE,

        /**
         * Date of version regarding the resource state.
         */
        VERSION_DATE,

        /**
         * Serial version UID (e.g Java class version UID) regarding the resource when it's a serializable custom object type.
         */
        RESOURCE_TYPE_SERIAL_VERSION_UID
    }

    /**
     * Default constructor.
     */
    public ResourceDescriptor() {
        super();
    }

    /**
     * Auto-feed constructor.
     * @param m Values to feed into the map.
     */
    public ResourceDescriptor(Map<? extends String, ? extends String> m) {
        super(m);
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     * @return Hash value.
     */
    public String versionHash() {
        return String.valueOf(serialVersionUID);
    }

    /**
     * Get serial version UID regarding the resource when it's a serializable custom object type.
     *
     * @return A version uid (e.g Java class version UID) or null.
     */
    public String resourceTypeSerialVersionUID() {
        return this.getOrDefault(PropertyAttributeKey.RESOURCE_TYPE_SERIAL_VERSION_UID.name(), null);
    }

    /**
     * Set a serial version UID regarding the resource when it's a serializable custom object type
     *
     * @param versionUID A serial version UID.
     */
    public void setResourceTypeSerialVersionUID(String versionUID) {
        this.put(PropertyAttributeKey.RESOURCE_TYPE_SERIAL_VERSION_UID.name(), versionUID);
    }

    /**
     * Set a serial version UID regarding the resource when it's a serializable custom object type
     *
     * @param versionUID A serial version UID.
     */
    public void setResourceTypeSerialVersionUID(Long versionUID) {
        if (versionUID != null) {
            this.setResourceTypeSerialVersionUID(versionUID.toString());
        }
    }

    /**
     * Get date of version regarding the resource state.
     *
     * @return A date or null.
     */
    public Date versionDate() {
        Date versionDate = null;
        String dateValue = this.getOrDefault(PropertyAttributeKey.VERSION_DATE.name(), null);
        if (dateValue != null) {
            try {
                DateFormat format = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
                versionDate = format.parse(dateValue);
            } catch (ParseException pe) {
                // Invalid date value found
            }
        }
        return versionDate;
    }

    /**
     * Set date of version regarding the resource state.
     *
     * @param aDate A date.
     */
    public void setVersionDate(Date aDate) {
        if (aDate != null) {
            DateFormat format = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
            this.put(PropertyAttributeKey.VERSION_DATE.name(), format.format(aDate));
        }
    }

    /**
     * Get label regarding the space path where the resource is accessible.
     *
     * @return A space path or null.
     */
    public String accessibilityNamespace() {
        return this.getOrDefault(PropertyAttributeKey.ACCESSIBILITY_NAMESPACE.name(), null);
    }

    /**
     * Set label regarding the space path where the resource is accessible.
     *
     * @param namespace A label.
     */
    public void setAccessibilityNamespace(String namespace) {
        this.put(PropertyAttributeKey.ACCESSIBILITY_NAMESPACE.name(), namespace);
    }

    /**
     * Get unique logical identifier of the resource.
     *
     * @return A resource id or null.
     */
    public String resourceId() {
        return this.getOrDefault(PropertyAttributeKey.RESOURCE_ID.name(), null);
    }

    /**
     * Set an unique logical identifier of the resource
     *
     * @param id A logical identifier.
     */
    public void setResourceId(String id) {
        this.put(PropertyAttributeKey.RESOURCE_ID.name(), id);
    }
}
