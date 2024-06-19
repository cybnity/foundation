package org.cybnity.infastructure.technical.persistence.store.impl.redis;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.NamingConventions;

/**
 * Utility class helping to define standardized logical names regarding the persistent objects manageable by store.
 */
public class PersistentObjectNamingConvention {

    /**
     * Category of naming convention supported and relative to specific type of element which shall be logically named according to a convention.
     * In a Redis implementation store, the pattern applied is explained at <a href="https://redis.io/docs/data-types/streams/#streams-basics">Redis Stream basics</a> regarding each stream entry key.
     * The standardized naming pattern for each type of persistent object is "domain name:type of domain object:Domain object's unique identifier".
     * Unique identifier can be natural or technical based according to the logical identification specific to an aggregate.
     */
    public enum NamingConventionApplicability {

        /**
         * Represent a persistent tenant.
         */
        TENANT("tenant");

        private final String label;

        NamingConventionApplicability(String standardLabel) {
            this.label = standardLabel;
        }

        public String label() {
            return this.label;
        }
    }

    /**
     * Build a logical name regarding a persistence namespace in a standardized way.
     *
     * @param category   Mandatory type of persistent object to be named.
     * @param domainName Mandatory label naming an application domain (e.g "ac" short name of the Access Control domain).
     * @param suffix     Optional suffix.
     * @return A standardized name respecting naming convention and built according to the specified parameters. Value rules applied to build the result name include none blank space, NamingConventions.KEY_NAME_SEPARATOR between each logical naming element.
     * @throws IllegalArgumentException When a mandatory parameter is not defined.
     */
    public static String buildNamespace(NamingConventionApplicability category, String domainName, String suffix) throws IllegalArgumentException {
        if (category == null) throw new IllegalArgumentException("category parameter is required!");
        if (domainName == null || domainName.isEmpty())
            throw new IllegalArgumentException("Domain name parameter is required!");
        StringBuilder standardLabel = new StringBuilder();
        // --- NAMING CONVENTION RULES ---
        // ---- Use NamingConventions.KEY_NAME_SEPARATOR separator between each naming element
        // Start label with domain name
        standardLabel.append(domainName);

        // Include category label of applicable convention
        standardLabel.append(NamingConventions.KEY_NAME_SEPARATOR);
        standardLabel.append(category.label());

        // Include optional suffix label
        if (suffix != null && !suffix.isEmpty()) {
            standardLabel.append(NamingConventions.KEY_NAME_SEPARATOR);
            standardLabel.append(suffix);
        }

        // Return built name with remove of any blank space potentially existing into the origin naming elements used
        return standardLabel.toString().trim();
    }

    /**
     * Build a logical name regarding a persistable object in a standardized way.
     *
     * @param category        Mandatory type of persistent object to be named.
     * @param domainName      Mandatory label naming an application domain (e.g "ac" short name of the Access Control domain).
     * @param identifierValue Optional identifier of the persistent object name.
     * @return A standardized name respecting naming convention and built according to the specified parameters. Value rules applied to build the result name include none blank space, NamingConventions.KEY_NAME_SEPARATOR between each logical naming element.
     * @throws IllegalArgumentException When a mandatory parameter is not defined.
     */
    public static String buildComponentName(NamingConventionApplicability category, String domainName, String identifierValue) throws IllegalArgumentException {
        StringBuilder standardLabel = new StringBuilder();
        // --- NAMING CONVENTION RULES ---
        // ---- Use NamingConventions.KEY_NAME_SEPARATOR separator between each naming element
        // Start label with domain name
        standardLabel.append(buildNamespace(category, domainName, /* none suffix by default */null));

        // Include identifier value when defined
        if (identifierValue != null && !identifierValue.isEmpty()) {
            // Add separator
            standardLabel.append(NamingConventions.KEY_NAME_SEPARATOR);
            standardLabel.append(identifierValue);
        }
        // Return built name with remove of any blank space potentially existing into the origin naming elements used
        return standardLabel.toString().trim();
    }
}
