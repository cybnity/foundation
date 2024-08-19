package org.cybnity.infrastructure.technical.registry.adapter.api.event;

/**
 * Type of standard/common attribute supported by a knowledge domain event or command.
 * Referential that can be used to identify a type of specification attribute with a value.
 */
public enum DataViewAttributeName {

    /**
     * Unique technical identifier of a read-model data view.
     * For example, can be valued by a Vertex id.
     */
    DATAVIEW_UID,

    /**
     * Label defining a node type relative to a data view (e.g equals to a Vertex nature).
     * It's define a type of node based on an unique label supported into a perimeter of nodes (e.g domain based).
     */
    DATAVIEW_NODE_LABEL,

    /**
     * Unique identifier of a domain abject (e.g any aggregate identifier).
     */
    DOMAIN_OBJECT_UID,

    /**
     * A specific property defining a unique logical name assigned to a node instance.
     */
    DATAVIEW_NODE_NAME
}
