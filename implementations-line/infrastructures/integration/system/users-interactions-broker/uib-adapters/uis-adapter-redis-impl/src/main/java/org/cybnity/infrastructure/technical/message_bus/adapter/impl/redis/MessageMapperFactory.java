package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.IDescribedToJSONMessageTransformer;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.IDescribedToStreamMessageTransformer;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.JSONMessageToIDescribedTransformer;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.StreamMessageToIDescribedTransformer;

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
    public static MessageMapper getMapper(Class<?> transformable, Class<?> transformableAs) {
        if (transformable != null && transformableAs != null) {
            // Select the origin type to be transformed
            if (IDescribed.class.isAssignableFrom(transformable)) {
                // Select the provided mapper allowing transformation to targeted type
                if (StreamMessage.class.isAssignableFrom(transformableAs)) {
                    return new IDescribedToStreamMessageTransformer();
                } else if (String.class.isAssignableFrom(transformableAs)) {
                    return new IDescribedToJSONMessageTransformer();
                }
            } else if (StreamMessage.class.isAssignableFrom(transformable)) {
                // Select the mapper allowing transformation to targeted type
                if (IDescribed.class.isAssignableFrom(transformableAs)) {
                    return new StreamMessageToIDescribedTransformer();
                }
            } else if (String.class.isAssignableFrom(transformable)) {
                // Select the mapper allowing transformation to targeted type
                if (IDescribed.class.isAssignableFrom(transformableAs)) {
                    return new JSONMessageToIDescribedTransformer();
                }
            }
        }
        return null; // None supported types
    }
}
