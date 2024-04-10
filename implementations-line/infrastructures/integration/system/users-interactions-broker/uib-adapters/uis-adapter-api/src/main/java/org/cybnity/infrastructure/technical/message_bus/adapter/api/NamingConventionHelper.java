package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Utility class helping to define standardized logical names regarding the processing units (e.g feature consumer group).
 */
public class NamingConventionHelper {

    /**
     * Category of naming convention supported by the helper relative to specific type of element which shall be logically named according to a convention.
     */
    public enum NamingConventionApplicability {

        /**
         * Represent a processing unit (e.g UI capability processor) able to manage a treatment (e.g a capability event request processing).
         */
        PROCESSING_UNIT("pu"),
        /**
         * Naming convention relative to processing unit providing capability and/or application services.
         */
        FEATURE_PROCESSING_UNIT("feature_" + PROCESSING_UNIT),
        /**
         * Naming convention relative to pipeline that manage data and/or event treatment.
         */
        PIPELINE("pipeline"),

        /**
         * Naming convention relative to a gateway.
         */
        GATEWAY("gateway");

        private final String label;

        private NamingConventionApplicability(String standardLabel) {
            this.label = standardLabel;
        }

        public String label() {
            return this.label;
        }
    }

    /**
     * Build a logical name regarding a type of processing unit in a standardized way.
     *
     * @param category              Mandatory type of processing unit to be named.
     * @param domainName            Mandatory label naming an application domain (e.g "ac" short name of the Access Control domain).
     * @param componentMainFunction Optional label of the key function (e.g "io" when the named subject is a component manging input/output events) of the component to be named.
     * @param resourceType          Optional label about the type of resource (e.g "processing_unit" or "gateway") that is targeted to be named by the built name.
     * @param segregationLabel      Optional label used as segregation element regarding the targeted component to be named by the result name.
     * @return A standardized name respecting naming convention and built according to the specified parameters (e.g based on targeted component to be logically named). Value rules applied to build the result name include none blank space, NamingConventions.SPACE_ACTOR_NAME_SEPARATOR between each logical naming element.
     * @throws IllegalArgumentException When a mandatory parameter is not defined.
     */
    public static String buildComponentName(NamingConventionApplicability category, String domainName, String componentMainFunction, String resourceType, String segregationLabel) throws IllegalArgumentException {
        if (category == null) throw new IllegalArgumentException("category parameter is required!");
        if (domainName == null || domainName.isEmpty())
            throw new IllegalArgumentException("Domain name parameter is required!");

        StringBuilder standardLabel = new StringBuilder();
        // --- NAMING CONVENTION RULES ---
        // ---- Use NamingConventions.SPACE_ACTOR_NAME_SEPARATOR separator between each naming element
        // Start label with domain name
        standardLabel.append(domainName);
        // Include key function when defined
        if (componentMainFunction != null && !componentMainFunction.isEmpty()) {
            // Add separator
            standardLabel.append(NamingConventions.SPACE_ACTOR_NAME_SEPARATOR);
            // Add key function label
            standardLabel.append(componentMainFunction);
        }
        // Include resource type when defined
        if (resourceType != null && !resourceType.isEmpty()) {
            // Add separator
            standardLabel.append(NamingConventions.SPACE_ACTOR_NAME_SEPARATOR);
            // Add resource type label
            standardLabel.append(resourceType);
        }
        // Include category label of applicable convention
        standardLabel.append(NamingConventions.SPACE_ACTOR_NAME_SEPARATOR);
        standardLabel.append(category.label());

        // Include segregation element when defined
        if (segregationLabel != null && !segregationLabel.isEmpty()) {
            // Add separator
            standardLabel.append(NamingConventions.SPACE_ACTOR_NAME_SEPARATOR);
            // Add segregation label
            standardLabel.append(segregationLabel);
        }
        // Return built name with remove of any blank space potentially existing into the origin naming elements used
        return standardLabel.toString().trim();
    }
}
