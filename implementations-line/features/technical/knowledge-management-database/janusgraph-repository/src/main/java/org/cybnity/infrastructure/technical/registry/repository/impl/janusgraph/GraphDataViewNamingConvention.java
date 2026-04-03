package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;


/**
 * Utility class helping to define standardized logical names regarding the persistent data view manageable by a repository.
 */
public class GraphDataViewNamingConvention {

    /**
     * Category of naming convention supported and relative to specific type of element which shall be logically named according to a convention.
     * In a JanusGraph implementation repository supported by Tinkerpop graph computing framework (for graph database OLTP and for graph analytic systems OLAP) and Gremlin (functional and data-flow query language), the pattern applied is explained at <a href="https://tinkerpop.apache.org/docs/3.7.2/reference/#_namespace_conventions">Namespace conventions</a>.
     * Unique identifier can be natural or technical based according to the logical identification specific to managed object type.
     */
    public enum NamingConventionApplicability {
        ;

        private final String label;

        NamingConventionApplicability(String standardLabel) {
            this.label = standardLabel;
        }

        public String label() {
            return this.label;
        }
    }

}
