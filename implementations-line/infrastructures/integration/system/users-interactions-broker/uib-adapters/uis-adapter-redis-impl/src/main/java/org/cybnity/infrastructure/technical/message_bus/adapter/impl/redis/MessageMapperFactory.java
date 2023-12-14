package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.domain.Command;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.CommandToMapTransformer;

import java.util.Map;

/**
 * Utility class allowing to transform an object manageable by the space according to a type of data structure supported by Redis.
 * For example, translate a CommandEvent object into a Map (message body).
 */
public class MessageMapperFactory {

    private MessageMapperFactory() {
    }

    /**
     * Get an object mapper allowing transformation of a specific type of class.
     *
     * @param transformable   Origin object type to map.
     * @param transformableAs Targeted type to generate.
     * @return A mapper, or null when none supported mapping capability between the origin and targeted type.
     */
    static MessageMapper getMapper(Class<?> transformable, Class<?> transformableAs) {
        if (transformable != null && transformableAs != null) {
            // Select the origin type to be transformed
            if (Command.class.isAssignableFrom(transformable)) {
                // Select the provided mapper allowing transformation to targeted type
                if (Map.class.isAssignableFrom(transformableAs)) {
                    return new CommandToMapTransformer();
                }
            }
        }
        return null; // None supported types
    }
}