package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * Generic factory of message mapper supported by a bounded context (e.g communication message type according to a type of serialization supported by a middleware protocol).
 * Utility class allowing to transform an object manageable by the space according to a type of data structure.
 * For example, translate a CommandEvent object into a String (message body).
 */
public interface IMessageMapperProvider {

    /**
     * Get an object mapper allowing transformation of a specific type of class.
     *
     * @param transformable   Origin object type to map.
     * @param transformableAs Targeted type to generate.
     * @return A mapper, or null when none supported mapping capability between the origin and targeted type.
     */
    MessageMapper getMapper(Class<?> transformable, Class<?> transformableAs);
}
